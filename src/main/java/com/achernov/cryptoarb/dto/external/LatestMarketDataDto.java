package com.achernov.cryptoarb.dto.external;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.math.BigDecimal;
import java.util.List;

public record LatestMarketDataDto(
        List<Data> data,
        Status status
) {

  public record Data(
          String name,
          String symbol,
          Quote quote
  ) {

    public record Quote(
            @JsonAlias("USD")
            USD usd
    ) {

      public record USD(
              BigDecimal price,
              @JsonAlias("volume_24h")
              BigDecimal volume24h,
              @JsonAlias("percent_change_1h")
              BigDecimal percentChange1h,
              @JsonAlias("percent_change_24h")
              BigDecimal percentChange24h,
              @JsonAlias("percent_change_7d")
              BigDecimal percentChange7d,
              @JsonAlias("market_cap")
              BigDecimal marketCap
      ) {}
    }
  }

  public record Status(
          String timestamp,
          @JsonAlias("error_code")
          int errorCode,
          @JsonAlias("error_message")
          String errorMessage
  ) {}
}
