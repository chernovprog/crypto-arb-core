package com.achernov.cryptoarb.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "app.external-services.coinmarketcap")
public record CoinMarketCapProperties(
        boolean enabled,
        Api api,
        String apiKey,
        String baseUrl,
        String headerName,
        String quoteCurrency,
        Pagination pagination,
        Endpoints endpoints
) {

  public record Api(
          Timeout timeout
  ) {

    public record Timeout(
       Duration read
    ) {}
  }

  public record Pagination(
          int start,
          int limit
  ) {}

  public record Endpoints(
          String listings
  ) {}
}
