package com.appdirect.demo.functions.content.parser;

import com.appdirect.demo.functions.domain.bo.CsvParserConfig;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

@Component
public class CsvParserConfigReader {

  private static final String EXT_YAML = ".yaml";
  private static final String EXT_YML = ".yml";
  private static final String DIR = "schema/";

  public CsvParserConfig map(String fileId) throws IOException {

    InputStream is = null;
    try {
      is = getClass().getClassLoader()
          .getResourceAsStream(DIR.concat(fileId).concat(EXT_YAML));

      if (is == null) {
        is = getClass().getClassLoader()
            .getResourceAsStream(DIR.concat(fileId).concat(EXT_YML));
      }

      Yaml yaml = new Yaml(new Constructor(CsvParserConfig.class));
      CsvParserConfig config = yaml.load(is);
      return config;

    } finally {
      if (is != null) {
        is.close();
      }
    }
  }
}
