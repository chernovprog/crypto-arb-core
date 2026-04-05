package com.achernov.cryptoarb.integrations.core.streaming;

import com.achernov.cryptoarb.config.properties.ExchangeProperties;
import com.achernov.cryptoarb.dto.TickerDto;
import com.achernov.cryptoarb.integrations.core.strategy.MessageParser;
import com.achernov.cryptoarb.integrations.core.strategy.PingService;
import com.achernov.cryptoarb.integrations.core.strategy.SubscriptionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@AllArgsConstructor
public class ExchangeWebSocketHandler extends TextWebSocketHandler {

  private final ObjectMapper objectMapper;
  private final MessageParser parser;
  private final SimpMessagingTemplate messagingTemplate;
  private final SubscriptionService subscriptionService;
  private final PingService pingService;
  private final ExchangeProperties properties;
  private final Runnable onCloseCallback;

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    try (MDC.MDCCloseable ignored = MDC.putCloseable("exchange", properties.exchangeName())) {
      log.info("Connected to WebSocket. Session ID: {}", session.getId());

      subscriptionService.subscribe(session, properties.tickers());

      pingService.start(session);
    }
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) {
    try (MDC.MDCCloseable ignored = MDC.putCloseable("exchange", properties.exchangeName())) {
      String payload = message.getPayload();
      log.debug("Received message: {}", payload);

      TickerDto dto = parser.parse(payload);
      if (dto != null && dto.isValid()) {
        String tickerJson = objectMapper.writeValueAsString(dto);

        messagingTemplate.convertAndSend(properties.topicDestination(), tickerJson);

        log.debug("Ticker sent to topic: {}", tickerJson);
      }
    } catch (Exception e) {
      log.error("Unexpected error processing message", e);
    }
  }

  @Override
  public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
    log.error("Transport error on session {}: {}",
            session.getId(), exception.getMessage(), exception);
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
    try (MDC.MDCCloseable ignored = MDC.putCloseable("exchange", properties.exchangeName())) {
      log.warn("Session closed. Status: {} - {}",
              status.getCode(), status.getReason());
      onCloseCallback.run();
    }
  }
}
