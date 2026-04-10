package com.achernov.cryptoarb.integration.core.strategy;

import org.springframework.web.socket.WebSocketSession;

import java.util.List;

public interface SubscriptionService {
  void subscribe(WebSocketSession session, List<String> tickers) throws Exception;
}
