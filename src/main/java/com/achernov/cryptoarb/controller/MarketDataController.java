package com.achernov.cryptoarb.controller;

import com.achernov.cryptoarb.dto.client.MarketDataView;
import com.achernov.cryptoarb.service.MarketDataService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class MarketDataController {

  private MarketDataService marketDataService;

  @GetMapping("/market-data")
  public ResponseEntity<MarketDataView> getMarketData() {
    MarketDataView data = marketDataService.getCache();
    if (data == null) {
      return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }
    return ResponseEntity.ok(data);
  }
}
