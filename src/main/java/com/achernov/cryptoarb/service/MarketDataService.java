package com.achernov.cryptoarb.service;

import com.achernov.cryptoarb.dto.client.MarketDataView;
import com.achernov.cryptoarb.repository.MarketDataCache;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MarketDataService {

  private final MarketDataCache cache;

  public MarketDataView getCache() {
    return cache.getCache();
  }
}
