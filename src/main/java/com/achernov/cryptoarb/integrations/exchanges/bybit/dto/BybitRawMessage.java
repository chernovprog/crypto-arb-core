package com.achernov.cryptoarb.integrations.exchanges.bybit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class BybitRawMessage {

  private Boolean success;
  @JsonProperty("ret_msg")
  private String retMsg;
  @JsonProperty("connId")
  private String conn_id;
  private String op;

  private String topic;
  private Data data;

  @Getter
  public class Data {
    private String symbol;
    private String lastPrice;
  }
}
