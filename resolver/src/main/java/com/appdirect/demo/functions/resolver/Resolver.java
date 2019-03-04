package com.appdirect.demo.functions.resolver;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.UUID;
import lombok.ToString;

public interface Resolver<T> {

  String getId();

  T resolve(Map<String, String> rawFields);

  //-- resolvers --//


  @ToString
  class StringNoneResolver implements Resolver<String> {

    @Override
    public String getId() {
      return "string-none-resolver";
    }

    @Override
    public String resolve(Map<String, String> rawFields) {
      return rawFields.get("v0");
    }
  }

  @ToString
  class BigDecimalNoneResolver implements Resolver<BigDecimal> {

    @Override
    public String getId() {
      return "bigdecimal-none-resolver";
    }

    @Override
    public BigDecimal resolve(Map<String, String> rawFields) {
      return new BigDecimal(rawFields.get("v0"));
    }
  }

  @ToString
  class UUIDGenResolver implements Resolver<String> {

    @Override
    public String getId() {
      return "uuid-gen-resolver";
    }

    @Override
    public String resolve(Map<String, String> rawFields) {
      return UUID.randomUUID().toString();
    }
  }

  @ToString
  class LongDateResolver implements Resolver<OffsetDateTime> {

    @Override
    public String getId() {
      return "long-date-resolver";
    }

    @Override
    public OffsetDateTime resolve(Map<String, String> rawFields) {
      Long millis = Long.valueOf(rawFields.get("v0"));
      return Instant
          .ofEpochMilli(millis)
          .atZone(ZoneId.systemDefault())
          .toOffsetDateTime();
    }
  }

  @ToString
  class FlatPriceResolver implements Resolver<BigDecimal> {

    @Override
    public String getId() {
      return "flat-price-resolver";
    }

    @Override
    public BigDecimal resolve(Map<String, String> rawFields) {
      BigDecimal quantity = new BigDecimal(rawFields.get("v0"));
      BigDecimal unitPrice = new BigDecimal(rawFields.get("v1"));
      return quantity.multiply(unitPrice);
    }
  }
}

