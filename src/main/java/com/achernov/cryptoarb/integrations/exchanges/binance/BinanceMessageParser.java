package com.achernov.cryptoarb.integrations.exchanges.binance;

import com.achernov.cryptoarb.dto.TickerDto;
import com.achernov.cryptoarb.integrations.core.strategy.MessageParser;
import com.achernov.cryptoarb.integrations.exchanges.binance.dto.BinanceRawMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class BinanceMessageParser implements MessageParser {

  private static final String TICKER_EVENT = "24hrMiniTicker";

  private final ObjectMapper objectMapper;

  @Override
  public TickerDto parse(String payload) throws Exception {
    BinanceRawMessage msg = objectMapper.readValue(payload, BinanceRawMessage.class);

    if (msg.getError() != null) {
      log.error("Error [{}]: {}", msg.getError().getCode(), msg.getError().getMsg());
      return null;
    }

    if (msg.getId() != null && msg.getEventType() == null) {
      log.info("System message received: {}", msg.getId());
      return null;
    }

    if (TICKER_EVENT.equals(msg.getEventType())
            && msg.getSymbol() != null && msg.getClosePrice() != null) {
      return new TickerDto(
              msg.getSymbol(),
              msg.getClosePrice(),
              System.currentTimeMillis()
      );
    }

    return null;
  }
}
