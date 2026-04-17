package com.achernov.cryptoarb.repository;

import com.achernov.cryptoarb.entity.Currency;
import com.achernov.cryptoarb.integration.properties.IntegrationProperties;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class CurrencyCache {

  private final Map<Long, Currency> currencies = new ConcurrentHashMap<>();
  private final Map<String, Long> tickerToId = new ConcurrentHashMap<>();

  private final Map<String, Long> tradingPairToId = new ConcurrentHashMap<>();

  private final IntegrationProperties properties;

  public CurrencyCache(IntegrationProperties properties) {
    this.properties = properties;
  }

  @PostConstruct
  public void init() {
    refreshCache();
  }

  /* TODO: fetch it from database */
  public void refreshCache() {
    for (Currency currency : CURRENCY_DATABASE) {
      currencies.put(currency.getId(), currency);
      tickerToId.put(currency.getTicker(), currency.getId());
    }

    initTradingPairs();
  }

  public Long getIdByTicker(String ticker) {
    return tickerToId.get(ticker.toUpperCase());
  }

  public Long getIdByTradingPair(String tradingPair) {
    return tradingPairToId.get(tradingPair);
  }

  public List<Currency> getCurrencies(Set<Long> ids) {
    List<Currency> result = new ArrayList<>();
    for (Long id : ids) {
      Currency currency = currencies.get(id);
      if (currency != null) result.add(currency);
    }
    return result;
  }

  private void initTradingPairs() {
    String quoteCurrencyUpperCase = properties.quoteCurrency().toUpperCase();

    for (String baseCurrency : properties.baseCurrency()) {
      String baseCurrencyUpperCase = baseCurrency.toUpperCase();
      Long currencyId = tickerToId.get(baseCurrencyUpperCase);

      if (currencyId != null) {
        tradingPairToId.put(
                baseCurrencyUpperCase + quoteCurrencyUpperCase,
                currencyId
        );
      }
    }
  }

  private static final List<Currency> CURRENCY_DATABASE = new ArrayList<>() {{
    add(new Currency(1L, "Bitcoin", "BTC"));
    add(new Currency(2L, "Ethereum", "ETH"));
    add(new Currency(3L, "Tether", "USDT"));
    add(new Currency(4L, "XRP", "XRP"));
    add(new Currency(5L, "BNB", "BNB"));
    add(new Currency(6L, "USDC", "USDC"));
    add(new Currency(7L, "Solana", "SOL"));
    add(new Currency(8L, "TRON", "TRX"));
    add(new Currency(9L, "Dogecoin", "DOGE"));
    add(new Currency(10L, "Cardano", "ADA"));
    add(new Currency(11L, "Hyperliquid", "HYPE"));
    add(new Currency(12L, "Bitcoin Cash", "BCH"));
    add(new Currency(13L, "Chainlink", "LINK"));
    add(new Currency(14L, "Monero", "XMR"));
    add(new Currency(15L, "Stellar", "XLM"));
    add(new Currency(16L, "Zcash", "ZEC"));
    add(new Currency(17L, "Litecoin", "LTC"));
  }};
}
