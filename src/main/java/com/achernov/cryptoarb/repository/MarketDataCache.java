package com.achernov.cryptoarb.repository;

import com.achernov.cryptoarb.config.properties.CoinMarketCapProperties;
import com.achernov.cryptoarb.dto.client.MarketDataView;
import com.achernov.cryptoarb.dto.external.LatestMarketDataDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Slf4j
@Getter
@Repository
@RequiredArgsConstructor
public class MarketDataCache {

  private final CoinMarketCapProperties props;

  private volatile MarketDataView cache;

  public void updateCache(LatestMarketDataDto newData) {
    if (newData == null || newData.data() == null || newData.status() == null) {
      return;
    }

    if (newData.status().errorCode() > 0) {
      log.warn("CMC API returned error: code={}, message={}",
              newData.status().errorCode(), newData.status().errorMessage());
      return;
    }

    this.cache = mapToView(newData);
  }

  private MarketDataView mapToView(LatestMarketDataDto dto) {
    List<MarketDataView.CurrencyShortInfoDto> list = dto.data().stream()
            .map(d -> {
              var usd = d.quote().usd();
              return new MarketDataView.CurrencyShortInfoDto(
                      d.name(),
                      d.symbol(),
                      usd.price(),
                      usd.volume24h(),
                      usd.percentChange1h(),
                      usd.percentChange24h(),
                      usd.percentChange7d(),
                      usd.marketCap()
              );
            })
            .toList();

    long epochMillis = Instant.parse(dto.status().timestamp()).toEpochMilli();

    return new MarketDataView(
            list,
            props.quoteCurrency(),
            epochMillis
    );
  }
}
