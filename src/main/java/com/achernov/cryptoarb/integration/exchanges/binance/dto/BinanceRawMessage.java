package com.achernov.cryptoarb.integration.exchanges.binance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;

@Getter
public class BinanceRawMessage {

  private JsonNode result;
  private Long id;
  private BinanceError error;

  @JsonProperty("e")
  private String eventType;
  @JsonProperty("s")
  private String symbol;
  @JsonProperty("c")
  private String closePrice;

  @Getter
  public class BinanceError {
    private int code;
    private String msg;
  }
}

