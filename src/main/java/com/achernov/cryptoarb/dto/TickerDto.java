package com.achernov.cryptoarb.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public record TickerDto(
        @JsonProperty("s") String symbol,
        @JsonProperty("p") String price,
        @JsonProperty("t") Long timestamp
) {

  @JsonIgnore
  public boolean isValid() {
    return symbol != null && !symbol.isBlank() &&
            price != null && !price.isBlank() &&
            timestamp != null;
  }
}
