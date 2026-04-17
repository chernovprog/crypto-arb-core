package com.achernov.cryptoarb.integration.exchanges.bybit;

import com.achernov.cryptoarb.integration.core.strategy.PingService;
import com.achernov.cryptoarb.integration.core.strategy.ReconnectPolicy;
import com.achernov.cryptoarb.integration.core.strategy.SubscriptionService;
import com.achernov.cryptoarb.integration.core.strategy.impl.DefaultSubscriptionService;
import com.achernov.cryptoarb.integration.core.strategy.impl.ExponentialBackoffReconnectPolicy;
import com.achernov.cryptoarb.integration.core.streaming.ExchangeStreamingService;
import com.achernov.cryptoarb.integration.properties.ExchangeConfig;
import com.achernov.cryptoarb.integration.properties.IntegrationProperties;
import com.achernov.cryptoarb.repository.CurrencyCache;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;

import java.util.List;
import java.util.Map;

@Configuration
@ConditionalOnBooleanProperties(
        value = {
                @ConditionalOnBooleanProperty(prefix = "app.integrations", name = "enabled"),
                @ConditionalOnBooleanProperty(prefix = "app.integrations.providers.bybit", name = "enabled")
        }
)
public class BybitConfig {

  private final SimpMessagingTemplate messagingTemplate;
  private final TaskScheduler taskScheduler;
  private final ObjectMapper objectMapper;
  private final CurrencyCache currencyCache;

  private final IntegrationProperties properties;

  private final BybitMessageParser parser;

  public BybitConfig(SimpMessagingTemplate messagingTemplate,
                     TaskScheduler taskScheduler,
                     ObjectMapper objectMapper,
                     CurrencyCache currencyCache,
                     IntegrationProperties properties,
                     BybitMessageParser parser) {
    this.messagingTemplate = messagingTemplate;
    this.taskScheduler = taskScheduler;
    this.objectMapper = objectMapper;
    this.currencyCache = currencyCache;
    this.properties = properties;
    this.parser = parser;
  }

  @Bean
  public ExchangeStreamingService bybitStreamingService() {

    SubscriptionService subscriptionService = new DefaultSubscriptionService(
            objectMapper,
            () -> {
              List<String> topics = properties.baseCurrency()
                      .stream()
                      .map(ticker -> "tickers."
                              + ticker.toUpperCase()
                              + properties.quoteCurrency().toUpperCase())
                      .toList();

              return Map.of("op", "subscribe", "args", topics);
            }
    );

    PingService pingService = (session) -> {
    };

    ReconnectPolicy reconnectPolicy
            = new ExponentialBackoffReconnectPolicy(8, 5000);

    return new ExchangeStreamingService(
            messagingTemplate,
            taskScheduler,
            properties.get("bybit"),
            parser,
            objectMapper,
            currencyCache,
            subscriptionService,
            pingService,
            reconnectPolicy
    );
  }
}
