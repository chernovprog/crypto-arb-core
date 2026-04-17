package com.achernov.cryptoarb.integration.core.strategy.impl;

import com.achernov.cryptoarb.integration.core.strategy.SubscriptionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.function.Supplier;

public class DefaultSubscriptionService implements SubscriptionService {

  private final ObjectMapper objectMapper;
  private final Supplier<Object> requestFactory;

  public DefaultSubscriptionService(ObjectMapper objectMapper,
                                    Supplier<Object> requestFactory) {
    this.objectMapper = objectMapper;
    this.requestFactory = requestFactory;
  }

  @Override
  public void subscribe(WebSocketSession session) throws Exception {
    Object request = requestFactory.get();
    String payload = objectMapper.writeValueAsString(request);
    session.sendMessage(new TextMessage(payload));
  }
}
