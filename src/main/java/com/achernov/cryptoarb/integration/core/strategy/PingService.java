package com.achernov.cryptoarb.integration.core.strategy;

import org.springframework.web.socket.WebSocketSession;

public interface PingService {
  void start(WebSocketSession session);
}
