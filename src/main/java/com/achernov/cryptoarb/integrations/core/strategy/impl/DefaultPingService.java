package com.achernov.cryptoarb.integrations.core.strategy.impl;

import com.achernov.cryptoarb.integrations.core.strategy.PingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.time.Duration;
import java.util.function.Supplier;

@Slf4j
@AllArgsConstructor
public class DefaultPingService implements PingService {

  private final TaskScheduler scheduler;
  private final ObjectMapper objectMapper;
  private final Supplier<Object> pingRequestSupplier;
  private final Duration pingInterval;

  @Override
  public void start(WebSocketSession session) {
    scheduler.scheduleAtFixedRate(() -> {
      try {
        if (session != null && session.isOpen()) {
          String ping = objectMapper
                  .writeValueAsString(pingRequestSupplier.get());
          session.sendMessage(new TextMessage(ping));
        }
      } catch (Exception e) {
        log.warn("Failed to send ping", e);
      }
    }, pingInterval);
  }
}
