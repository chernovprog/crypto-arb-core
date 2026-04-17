package com.achernov.cryptoarb.integration.core.strategy;

import org.springframework.web.socket.WebSocketSession;

public interface SubscriptionService {
  void subscribe(WebSocketSession session) throws Exception;
}
