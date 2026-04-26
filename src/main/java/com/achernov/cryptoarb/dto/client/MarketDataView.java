package com.achernov.cryptoarb.dto.client;

import java.math.BigDecimal;
import java.util.List;

public record MarketDataView(
        List<CurrencyShortInfoDto> data,
        String quoteCurrency,
        Long timestamp
) {

  public record CurrencyShortInfoDto(
          String name,
          String symbol,
          BigDecimal price,
          BigDecimal volume24h,
          BigDecimal percentChange1h,
          BigDecimal percentChange24h,
          BigDecimal percentChange7d,
          BigDecimal marketCap
  ) {}
}
