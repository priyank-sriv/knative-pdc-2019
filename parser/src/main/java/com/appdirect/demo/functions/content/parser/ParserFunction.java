package com.appdirect.demo.functions.content.parser;

import static org.springframework.cloud.gcp.storage.GoogleStorageProtocolResolver.PROTOCOL;

import com.appdirect.demo.functions.content.parser.csv.CsvMapper;
import com.appdirect.demo.functions.domain.bo.RawEvent;
import com.appdirect.demo.functions.domain.bo.UploadNotification;
import com.google.cloud.storage.Storage;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gcp.storage.GoogleStorageResource;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

@SpringBootApplication
public class ParserFunction implements Function<UploadNotification, RawEvent> {

  private CsvMapper mapper;
  private Storage storage;

  @Autowired
  public ParserFunction(CsvMapper csvMapper, Storage storage) {
    this.mapper = csvMapper;
    this.storage = storage;
  }

  @Override
  public RawEvent apply(UploadNotification uploadNotification) {

    Resource gcsFile = gcsFileResource(
        uploadNotification.getBucketId(), uploadNotification.getObjectId());

    try {
      Path dest = Paths.get("/tmp");
      ((GoogleStorageResource) gcsFile).getBlob().downloadTo(dest);

      Stream<String[]> records = mapper.parse(new FileReader(dest.toFile()));
      records.forEach(r -> System.out.println(Arrays.toString(r)));

    } catch (IOException e) {
      e.printStackTrace();
    }

    return RawEvent.builder()
        .referenceId(UUID.randomUUID().toString())
        .processingTimeMillis(Instant.now().toEpochMilli())
        .build();
  }

  private Resource gcsFileResource(String bucketId, String objectId) {
    String location = null;
    try {
      location = new URI(PROTOCOL, bucketId, objectId, null, null).toString();

    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
    return new GoogleStorageResource(this.storage, location);
  }

  @Bean
  public CsvMapper csvMapper() {
    return new CsvMapper();
  }

  public static void main(String[] args) {
    SpringApplication.run(ParserFunction.class, args);
  }
}
