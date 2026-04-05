package com.achernov.cryptoarb.integrations.core.strategy.impl;

import com.achernov.cryptoarb.integrations.core.strategy.SubscriptionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.function.Function;

public class DefaultSubscriptionService implements SubscriptionService {

  private final ObjectMapper objectMapper;
  private final Function<List<String>, Object> requestFactory;

  public DefaultSubscriptionService(ObjectMapper objectMapper,
                                    Function<List<String>, Object> requestFactory) {
    this.objectMapper = objectMapper;
    this.requestFactory = requestFactory;
  }

  @Override
  public void subscribe(WebSocketSession session, List<String> tickers) throws Exception {
    Object request = requestFactory.apply(tickers);
    String payload = objectMapper.writeValueAsString(request);
    session.sendMessage(new TextMessage(payload));
  }
}
