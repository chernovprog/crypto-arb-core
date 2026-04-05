package com.achernov.cryptoarb.integrations.exchanges.bybit;

import com.achernov.cryptoarb.config.properties.ExchangeProperties;
import com.achernov.cryptoarb.config.properties.ExternalExchangeProperties;
import com.achernov.cryptoarb.integrations.core.strategy.PingService;
import com.achernov.cryptoarb.integrations.core.strategy.ReconnectPolicy;
import com.achernov.cryptoarb.integrations.core.strategy.SubscriptionService;
import com.achernov.cryptoarb.integrations.core.strategy.impl.DefaultSubscriptionService;
import com.achernov.cryptoarb.integrations.core.strategy.impl.ExponentialBackoffReconnectPolicy;
import com.achernov.cryptoarb.integrations.core.streaming.ExchangeStreamingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;

import java.util.List;
import java.util.Map;

@Configuration
public class BybitConfig {

  private final SimpMessagingTemplate messagingTemplate;
  private final TaskScheduler taskScheduler;
  private final ObjectMapper objectMapper;

  private final ExchangeProperties properties;

  private final BybitMessageParser parser;

  public BybitConfig(SimpMessagingTemplate messagingTemplate,
                     TaskScheduler taskScheduler,
                     ObjectMapper objectMapper,
                     ExternalExchangeProperties exchangeProperties,
                     BybitMessageParser parser) {
    this.messagingTemplate = messagingTemplate;
    this.taskScheduler = taskScheduler;
    this.objectMapper = objectMapper;
    this.properties = exchangeProperties.get("bybit");
    this.parser = parser;
  }

  @Bean
  public ExchangeStreamingService bybitStreamingService() {

    SubscriptionService subscriptionService = new DefaultSubscriptionService(
            objectMapper,
            tickers -> {
              List<String> topics = tickers.stream()
                      .map(symbol -> "tickers." + symbol.toUpperCase())
                      .toList();

              return Map.of("op", "subscribe", "args", topics);
            }
    );

    PingService pingService = (session) -> {};

    ReconnectPolicy reconnectPolicy
            = new ExponentialBackoffReconnectPolicy(8, 5000);

    return new ExchangeStreamingService(
            messagingTemplate,
            taskScheduler,
            properties,
            parser,
            objectMapper,
            subscriptionService,
            pingService,
            reconnectPolicy
    );
  }
}
