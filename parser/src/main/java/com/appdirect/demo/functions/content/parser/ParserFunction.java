package com.appdirect.demo.functions.content.parser;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.cloud.gcp.storage.GoogleStorageProtocolResolver.PROTOCOL;

import com.appdirect.demo.functions.domain.bo.CsvParserConfig;
import com.appdirect.demo.functions.domain.bo.RawEvent;
import com.appdirect.demo.functions.domain.bo.UploadNotification;
import com.appdirect.demo.functions.domain.exception.ParserException;
import com.appdirect.demo.functions.domain.exception.ParserIOException;
import com.google.cloud.storage.Storage;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;
import org.simpleflatmapper.csv.CsvParser;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gcp.storage.GoogleStorageResource;
import org.springframework.core.io.Resource;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

@SpringBootApplication
public class ParserFunction implements Function<UploadNotification, RawEvent> {

  private static final Logger LOGGER = getLogger(MethodHandles.lookup().lookupClass());

  private Storage storage;

  @Autowired
  public ParserFunction(Storage storage) {
    this.storage = storage;
  }


  @Override
  public RawEvent apply(UploadNotification notif) {

    try {
      Resource gcsFile = gcsFileResource(notif.getBucketId(), notif.getObjectId());
      Path dest = downloadLocal(gcsFile);
      removeRemote(gcsFile);

      CsvParserConfig parserConfig = parserConfig();

      Stream<String[]> records = parse(dest, parserConfig.delimiter());
      records.map(r -> mapper(parserConfig()));

      // todo: stream the response and send each message to channel

    } catch (URISyntaxException e) {
      throw new ParserException("Invalid resource identifier", e);
    } catch (IOException e) {
      throw new ParserIOException("IO error in parsing", e);
    }

    return null;
  }

  //......##### internal #####......//

  
  private Function<? extends String[], RawEvent> mapper(CsvParserConfig parserConfig) {
    return r -> RawEvent.builder().referenceId(UUID.randomUUID().toString()).build();
  }

  private Path downloadLocal(Resource gcsFile) throws IOException {
    Path dest = Paths.get(System.getProperty("java.io.tmpdir"), gcsFile.getFilename());
    ((GoogleStorageResource) gcsFile).getBlob().downloadTo(dest);
    return dest;
  }

  private void removeRemote(Resource gcsFile) throws IOException {
    boolean deleted = storage.delete(((GoogleStorageResource) gcsFile).getBlob().getBlobId());
    if (!deleted) {
      LOGGER.error("Unable to delete remote file in google storage");
    }
  }

  private Resource gcsFileResource(String bucketId, String objectId) throws URISyntaxException {
    String location = new URI(PROTOCOL, bucketId, objectId, null, null).toString();
    return new GoogleStorageResource(this.storage, location);
  }

  private CsvParserConfig parserConfig() {
    InputStream is = getClass().getClassLoader().getResourceAsStream("schema/parser.yaml");
    Yaml yaml = new Yaml(new Constructor(CsvParserConfig.class));
    return yaml.load(is);
  }

  private Stream<String[]> parse(Path p, char delim) throws IOException {
    CsvParser.DSL parser = CsvParser.separator(delim).trimSpaces();
    return parser.stream(new FileReader(p.toFile()));
  }

  @SuppressWarnings("unused")
  private Stream<String[]> parse(String payload, char delim) throws IOException {
    CsvParser.DSL parser = CsvParser.separator(delim).trimSpaces();
    return parser.stream(payload);
  }

  public static void main(String[] args) {
    SpringApplication.run(ParserFunction.class, args);
  }
}
