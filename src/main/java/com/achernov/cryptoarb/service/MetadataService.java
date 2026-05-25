package com.achernov.cryptoarb.service;

import com.achernov.cryptoarb.constants.CacheNames;
import com.achernov.cryptoarb.dto.metadata.*;
import com.achernov.cryptoarb.entity.Currency;
import com.achernov.cryptoarb.integration.properties.ExchangeConfig;
import com.achernov.cryptoarb.integration.properties.IntegrationProperties;
import com.achernov.cryptoarb.repository.CurrencyCache;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class MetadataService {

  private final CurrencyCache currencyCache;
  private final IntegrationProperties properties;

  @Cacheable(value = CacheNames.SYSTEM_METADATA, key = "@cacheKeyHelper.getMetadataKey(#sections)")
  public AppMetadataDto assembleMetadata(Set<MetadataSection> sections) {
    return buildMetadata(sections);
  }

  @CachePut(value = CacheNames.SYSTEM_METADATA, key = "@cacheKeyHelper.getMetadataKey(#sections)")
  public AppMetadataDto refreshAndPutMetadata(Set<MetadataSection> sections) {
    log.info("Forced cache update for sections: {}", sections);
    return buildMetadata(sections);
  }

  private AppMetadataDto buildMetadata(Set<MetadataSection> sections) {
    var builder = AppMetadataDto.builder();

    if (sections.contains(MetadataSection.EXCHANGE_SUBSCRIPTIONS)) {

      List<ExchangeSubscriptionDto> subscriptions = properties.providers()
              .values()
              .stream()
              .sorted(Comparator.comparing(ExchangeConfig::displayPriority))
              .map(config ->
                      new ExchangeSubscriptionDto(
                              config.exchangeName(),
                              config.topicDestination()
                      ))
              .toList();

      builder.exchangeSubscriptions(subscriptions);
    }

    if (sections.contains(MetadataSection.TRADING_PAIR_SUBSCRIPTIONS)) {
      if (!properties.baseCurrency().isEmpty()
              && StringUtils.hasText(properties.quoteCurrency())) {

        Long quoteCurrencyId = currencyCache.getIdByTicker(properties.quoteCurrency());

        Set<Long> baseCurrencyIds = new HashSet<>();
        for (String baseCurrency : properties.baseCurrency()) {
          Long currencyId = currencyCache.getIdByTicker(baseCurrency);
          if (currencyId != null) baseCurrencyIds.add(currencyId);
        }

        if (quoteCurrencyId != null && !baseCurrencyIds.isEmpty()) {
          builder.tradingPairSubscriptions(
                  new TradingPairSubscriptionDto(quoteCurrencyId, baseCurrencyIds)
          );
        }
      }
    }

    if (sections.contains(MetadataSection.CURRENCY_SUBSCRIPTIONS)) {
      Set<Long> currencyIds = new HashSet<>();

      if (StringUtils.hasText(properties.quoteCurrency())) {
        Long quoteCurrencyId = currencyCache.getIdByTicker(properties.quoteCurrency());
        if (quoteCurrencyId != null) currencyIds.add(quoteCurrencyId);
      }

      if (!properties.baseCurrency().isEmpty()) {
        for (String baseCurrency : properties.baseCurrency()) {
          Long currencyId = currencyCache.getIdByTicker(baseCurrency);
          if (currencyId != null) currencyIds.add(currencyId);
        }
      }

      List<Currency> currencies = currencyCache.getCurrencies(currencyIds);
      if (!currencies.isEmpty()) {
        Map<Long, CurrencyDto> result = new HashMap<>();

        for (Currency c : currencies) {
          result.put(c.getId(), new CurrencyDto(c.getName(), c.getTicker()));
        }

        builder.currencySubscriptions(result);
      }
    }

    if (sections.contains(MetadataSection.UI_CONFIG)) {
      builder.uiConfig(new UiConfigDto("light", "en"));
    }

    return builder.build();
  }
}
