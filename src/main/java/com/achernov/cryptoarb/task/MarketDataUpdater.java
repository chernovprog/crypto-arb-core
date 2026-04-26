package com.achernov.cryptoarb.task;

import com.achernov.cryptoarb.config.properties.CoinMarketCapProperties;
import com.achernov.cryptoarb.dto.external.LatestMarketDataDto;
import com.achernov.cryptoarb.repository.MarketDataCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class MarketDataUpdater {

  private final CoinMarketCapProperties props;
  private final RestClient client;
  private final MarketDataCache cache;

  @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES)
  public void fetchLatestMarketData() {
    try {
      LatestMarketDataDto data = client.get()
              .uri(uriBuilder -> uriBuilder
                      .path(props.endpoints().listings())
                      .queryParam("start", props.pagination().start())
                      .queryParam("limit", props.pagination().limit())
                      .queryParam("convert", props.quoteCurrency())
                      .build())
              .retrieve()
              .body(LatestMarketDataDto.class);

      cache.updateCache(data);
      log.debug("Market data successfully updated");
    } catch (Exception e) {
      log.error("Failed to fetch data from CoinMarketCap: {}", e.getMessage());
    }
  }
}
