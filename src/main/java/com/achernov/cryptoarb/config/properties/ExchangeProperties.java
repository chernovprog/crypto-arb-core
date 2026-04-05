package com.achernov.cryptoarb.config.properties;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.Duration;
import java.util.List;

public record ExchangeProperties(
        @NotEmpty String exchangeName,
        @NotEmpty String spotMarketUrl,
        @NotEmpty String topicDestination,
        @NotNull Duration pingInterval,
        @NotEmpty List<String> tickers
) {
  public ExchangeProperties {
    Duration minInterval = Duration.ofSeconds(20);
    if (pingInterval == null || pingInterval.compareTo(minInterval) < 0) {
      pingInterval = minInterval;
    }

    tickers = (tickers != null) ? List.copyOf(tickers) : List.of();
  }
}
