package com.achernov.cryptoarb.integration.exchanges.whitebit.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;

import java.util.List;

@Getter
public class WhitebitRawMessage {

  private Long id;

  private JsonNode result;
  private JsonNode error;

  private String method;
  private List<String> params;

  public boolean isPong() {
    return result != null && result.isTextual()
            && "pong".equals(result.asText());
  }
}
