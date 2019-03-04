package com.appdirect.demo.functions.domain.bo;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FieldResolverConfig {

  private List<Field> fields;

  @Data
  @NoArgsConstructor
  public static class Field {

    private String id;
    private List<String> refId;
    private String resolverId;
  }
}
