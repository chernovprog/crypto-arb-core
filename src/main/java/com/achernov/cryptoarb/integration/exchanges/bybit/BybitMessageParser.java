package com.achernov.cryptoarb.integration.exchanges.bybit;

import com.achernov.cryptoarb.dto.TickerDto;
import com.achernov.cryptoarb.integration.core.strategy.MessageParser;
import com.achernov.cryptoarb.integration.exchanges.bybit.dto.BybitRawMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class BybitMessageParser implements MessageParser {
  private static final String TICKER_TOPIC_PREFIX = "tickers.";
  private static final String OP_PONG = "pong";
  private static final String OP_SUBSCRIBE = "subscribe";

  private final ObjectMapper objectMapper;

  @Override
  public TickerDto parse(String payload) throws Exception {
    BybitRawMessage msg = objectMapper.readValue(payload, BybitRawMessage.class);

    if (msg.getOp() != null && !msg.getOp().isBlank()) {
      if (OP_PONG.equals(msg.getOp())) {
        log.trace("Pong received");
        return null;
      }

      if (OP_SUBSCRIBE.equals(msg.getOp())) {
        if (msg.getSuccess()) {
          log.info("Subscription confirmed");
        } else {
          log.error("Subscription failed: {}", payload);
        }
        return null;
      }
    }

    if (msg.getData() != null && msg.getTopic() != null
            && msg.getTopic().startsWith(TICKER_TOPIC_PREFIX)) {
      return new TickerDto(
              msg.getData().getSymbol(),
              msg.getData().getLastPrice(),
              System.currentTimeMillis()
      );
    }

    return null;
  }
}
