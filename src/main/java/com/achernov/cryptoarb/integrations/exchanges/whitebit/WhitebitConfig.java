package com.achernov.cryptoarb.integrations.exchanges.whitebit;

import com.achernov.cryptoarb.integrations.core.strategy.PingService;
import com.achernov.cryptoarb.integrations.core.strategy.ReconnectPolicy;
import com.achernov.cryptoarb.integrations.core.strategy.SubscriptionService;
import com.achernov.cryptoarb.integrations.core.strategy.impl.DefaultSubscriptionService;
import com.achernov.cryptoarb.integrations.core.strategy.impl.ExponentialBackoffReconnectPolicy;
import com.achernov.cryptoarb.integrations.core.streaming.ExchangeStreamingService;
import com.achernov.cryptoarb.integrations.properties.ExchangeConfig;
import com.achernov.cryptoarb.integrations.properties.IntegrationProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;

import java.util.Map;

@Configuration
@ConditionalOnProperties(
        value = {
                @ConditionalOnProperty(prefix = "app.integrations", name = "enabled", havingValue = "true"),
                @ConditionalOnProperty(prefix = "app.integrations.providers.whitebit", name = "enabled", havingValue = "true")
        }
)
public class WhitebitConfig {

  private final SimpMessagingTemplate messagingTemplate;
  private final TaskScheduler taskScheduler;
  private final ObjectMapper objectMapper;

  private final ExchangeConfig config;

  private final WhitebitMessageParser parser;

  public WhitebitConfig(SimpMessagingTemplate messagingTemplate,
                        TaskScheduler taskScheduler,
                        ObjectMapper objectMapper,
                        IntegrationProperties properties,
                        WhitebitMessageParser parser) {
    this.messagingTemplate = messagingTemplate;
    this.taskScheduler = taskScheduler;
    this.objectMapper = objectMapper;
    this.config = properties.get("whitebit");
    this.parser = parser;
  }

  @Bean
  public ExchangeStreamingService whitebitStreamingService() {

    SubscriptionService subscriptionService = new DefaultSubscriptionService(
            objectMapper,
            tickers -> Map.of(
                    "id", 1,
                    "method", "lastprice_subscribe",
                    "params", tickers
            )
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
