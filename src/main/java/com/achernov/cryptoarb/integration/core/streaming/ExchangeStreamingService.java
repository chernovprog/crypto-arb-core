package com.achernov.cryptoarb.integration.core.streaming;

import com.achernov.cryptoarb.integration.properties.ExchangeConfig;
import com.achernov.cryptoarb.integration.core.strategy.MessageParser;
import com.achernov.cryptoarb.integration.core.strategy.PingService;
import com.achernov.cryptoarb.integration.core.strategy.ReconnectPolicy;
import com.achernov.cryptoarb.integration.core.strategy.SubscriptionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
public class ExchangeStreamingService {

  private final SimpMessagingTemplate messagingTemplate;
  private final TaskScheduler scheduler;
  private final ExchangeConfig config;
  private final MessageParser parser;
  private final ObjectMapper objectMapper;
  private final SubscriptionService subscriptionService;
  private final PingService pingService;
  private final ReconnectPolicy reconnectPolicy;

  private volatile boolean shuttingDown;
  private int attempts;

  @PostConstruct
  public void init() {
    shuttingDown = false;
    attempts = 0;
    connect();
  }

  public void connect() {
    if (shuttingDown) return;

    WebSocketClient client = new StandardWebSocketClient();

    ExchangeWebSocketHandler handler = new ExchangeWebSocketHandler(
            objectMapper,
            parser,
            messagingTemplate,
            subscriptionService,
            pingService,
            config,
            this::scheduleReconnect
    );

    client.execute(handler, config.spotMarketUrl());
  }

  private void scheduleReconnect() {
    if (shuttingDown || !reconnectPolicy.canRetry(attempts)) {
      log.warn("{}Max reconnect attempts reached ({}). Giving up.",
              config.exchangeName(), attempts);
      return;
    }

    attempts++;
    long delay = reconnectPolicy.nextDelay(attempts);

    scheduler.schedule(this::connect, Instant.now().plusMillis(delay));
  }

  @PreDestroy
  public void shutdown() {
    shuttingDown = true;
    log.info("[{}] Shutdown", config.exchangeName());
  }
}
