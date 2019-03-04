package com.appdirect.demo.functions.domain.bo;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Builder
@Data
public class ResolvedEvent {

  @NonNull
  private String eventId;

  @NonNull
  private OffsetDateTime eventDateTime;

  private String userId;
  private String productId;
  private BigDecimal quantity;
  private BigDecimal totalPrice;
}
