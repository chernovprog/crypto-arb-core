package com.achernov.cryptoarb.integration.core.streaming;

import com.achernov.cryptoarb.dto.common.TickerDto;
import com.achernov.cryptoarb.integration.core.strategy.MessageParser;
import com.achernov.cryptoarb.integration.core.strategy.PingService;
import com.achernov.cryptoarb.integration.core.strategy.SubscriptionService;
import com.achernov.cryptoarb.integration.mapper.TickerDataMapper;
import com.achernov.cryptoarb.integration.properties.ExchangeConfig;
import com.achernov.cryptoarb.repository.CurrencyCache;
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
  private final ExchangeConfig config;
  private final Runnable onCloseCallback;
  private final CurrencyCache currencyCache;

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    try (MDC.MDCCloseable ignored = MDC.putCloseable("exchange", config.exchangeName())) {
      log.info("Connected to WebSocket. Session ID: {}", session.getId());

      subscriptionService.subscribe(session);

      pingService.start(session);
    }
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) {
    try (MDC.MDCCloseable ignored = MDC.putCloseable("exchange", config.exchangeName())) {
      String payload = message.getPayload();
      log.debug("Received message: {}", payload);

      TickerDataMapper tickerData = parser.parse(payload);

      if (tickerData != null) {
        Long tickerId = currencyCache.getIdByTradingPair(tickerData.symbol());

        if (tickerId != null) {
          TickerDto dto = new TickerDto(
                  tickerId,
                  tickerData.price(),
                  tickerData.timestamp()
          );

          String tickerJson = objectMapper.writeValueAsString(dto);

          messagingTemplate.convertAndSend(config.topicDestination(), tickerJson);

          log.debug("Ticker sent to topic: {}", tickerJson);
        } else {
          log.warn("Ticker id not found: {}", tickerData.symbol());
        }
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
    try (MDC.MDCCloseable ignored = MDC.putCloseable("exchange", config.exchangeName())) {
      log.warn("Session closed. Status: {} - {}",
              status.getCode(), status.getReason());
      onCloseCallback.run();
    }
  }
}
