package com.appdirect.demo.functions.domain.bo;

import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FieldResolverConfig {

  private Map<String, Field> fields;

  @Data
  @NoArgsConstructor
  public static class Field {

    private List<FieldReference> ref;
    private String resolverId;
  }

  @Data
  @NoArgsConstructor
  public static class FieldReference {

    private String name;
    private String arg;
  }
}
