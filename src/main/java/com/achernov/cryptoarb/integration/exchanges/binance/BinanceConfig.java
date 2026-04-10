package com.achernov.cryptoarb.integration.exchanges.binance;

import com.achernov.cryptoarb.integration.core.strategy.PingService;
import com.achernov.cryptoarb.integration.core.strategy.ReconnectPolicy;
import com.achernov.cryptoarb.integration.core.strategy.SubscriptionService;
import com.achernov.cryptoarb.integration.core.strategy.impl.DefaultSubscriptionService;
import com.achernov.cryptoarb.integration.core.strategy.impl.ExponentialBackoffReconnectPolicy;
import com.achernov.cryptoarb.integration.core.streaming.ExchangeStreamingService;
import com.achernov.cryptoarb.integration.properties.ExchangeConfig;
import com.achernov.cryptoarb.integration.properties.IntegrationProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;

import java.util.List;
import java.util.Map;

@Configuration
@ConditionalOnProperties(
        value = {
                @ConditionalOnProperty(prefix = "app.integrations", name = "enabled", havingValue = "true"),
                @ConditionalOnProperty(prefix = "app.integrations.providers.binance", name = "enabled", havingValue = "true")
        }
)
public class BinanceConfig {

  private final SimpMessagingTemplate messagingTemplate;
  private final TaskScheduler taskScheduler;
  private final ObjectMapper objectMapper;

  private final ExchangeConfig config;

  private final BinanceMessageParser parser;

  public BinanceConfig(SimpMessagingTemplate messagingTemplate,
                       TaskScheduler taskScheduler,
                       ObjectMapper objectMapper,
                       IntegrationProperties properties,
                       BinanceMessageParser parser) {
    this.messagingTemplate = messagingTemplate;
    this.taskScheduler = taskScheduler;
    this.objectMapper = objectMapper;
    this.config = properties.get("binance");
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
            config,
            parser,
            objectMapper,
            subscriptionService,
            pingService,
            reconnectPolicy
    );
  }
}
