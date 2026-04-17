package com.achernov.cryptoarb.integration.exchanges.binance;

import com.achernov.cryptoarb.integration.mapper.TickerDataMapper;
import com.achernov.cryptoarb.integration.core.strategy.MessageParser;
import com.achernov.cryptoarb.integration.exchanges.binance.dto.BinanceRawMessage;
import com.achernov.cryptoarb.integration.util.SymbolNormalizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@RequiredArgsConstructor
@Component
public class BinanceMessageParser implements MessageParser {

  private static final String TICKER_EVENT = "24hrMiniTicker";

  private final ObjectMapper objectMapper;

  @Override
  public TickerDataMapper parse(String payload) throws Exception {
    BinanceRawMessage msg = objectMapper.readValue(payload, BinanceRawMessage.class);

    if (msg.getError() != null) {
      log.error("Error [{}]: {}", msg.getError().getCode(), msg.getError().getMsg());
      return null;
    }

    if (msg.getId() != null && msg.getEventType() == null) {
      log.info("System message received: {}", msg.getId());
      return null;
    }

    if (TICKER_EVENT.equals(msg.getEventType()) && isMessageValid(msg)) {

      String standardizedSymbol = SymbolNormalizer.normalize(msg.getSymbol());

      return new TickerDataMapper(
              standardizedSymbol,
              msg.getClosePrice(),
              System.currentTimeMillis()
      );
    }

    return null;
  }

  private boolean isMessageValid(BinanceRawMessage msg) {
    return StringUtils.hasText(msg.getSymbol())
            && StringUtils.hasText(msg.getClosePrice());
  }
}
