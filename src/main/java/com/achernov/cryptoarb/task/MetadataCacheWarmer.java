package com.achernov.cryptoarb.task;

import com.achernov.cryptoarb.dto.metadata.MetadataSection;
import com.achernov.cryptoarb.service.MetadataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@Profile("prod")
@RequiredArgsConstructor
public class MetadataCacheWarmer {

  private final MetadataService metadataService;

  @Scheduled(fixedRate = 1, timeUnit = TimeUnit.HOURS)
  public void warmUpMetadataCacheForArbitrageBoard() {
    log.info("Starting scheduled metadata cache warming for arbitrage board...");

    metadataService.refreshAndPutMetadata(
            EnumSet.of(
                    MetadataSection.EXCHANGE_SUBSCRIPTIONS,
                    MetadataSection.TRADING_PAIR_SUBSCRIPTIONS,
                    MetadataSection.CURRENCY_SUBSCRIPTIONS
            )
    );

    log.info("Metadata cache for arbitrage board is warm and ready!");
  }
}
