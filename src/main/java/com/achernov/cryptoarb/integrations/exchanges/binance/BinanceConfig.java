package com.achernov.cryptoarb.integrations.exchanges.binance;

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
public class BinanceConfig {

  private final SimpMessagingTemplate messagingTemplate;
  private final TaskScheduler taskScheduler;
  private final ObjectMapper objectMapper;

  private final ExchangeProperties properties;

  private final BinanceMessageParser parser;

  public BinanceConfig(SimpMessagingTemplate messagingTemplate,
                       TaskScheduler taskScheduler,
                       ObjectMapper objectMapper,
                       ExternalExchangeProperties exchangeProperties,
                       BinanceMessageParser parser) {
    this.messagingTemplate = messagingTemplate;
    this.taskScheduler = taskScheduler;
    this.objectMapper = objectMapper;
    this.properties = exchangeProperties.get("binance");
    this.parser = parser;
  }

  @Bean
  public ExchangeStreamingService binanceStreamingService() {

    SubscriptionService subscriptionService = new DefaultSubscriptionService(
            objectMapper,
            tickers -> {
              List<String> params = tickers.stream()
                      .map(symbol -> symbol.toLowerCase() + "@miniTicker")
                      .toList();

              return Map.of(
                      "id", 1,
                      "method", "SUBSCRIBE",
                      "params", params);
            }
    );

    PingService pingService = (session) -> {
    };

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
