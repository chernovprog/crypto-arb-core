package com.achernov.cryptoarb.integration.properties;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.Duration;
import java.util.List;

public record ExchangeConfig(
        @NotEmpty String exchangeName,
        int displayPriority,
        boolean enabled,
        @NotEmpty String spotMarketUrl,
        @NotEmpty String topicDestination,
        @NotNull Duration pingInterval
) {
  public ExchangeConfig {
    Duration minInterval = Duration.ofSeconds(20);
    if (pingInterval == null || pingInterval.compareTo(minInterval) < 0) {
      pingInterval = minInterval;
    }
  }
}
