package com.achernov.cryptoarb.integration.exchanges.bybit;

import com.achernov.cryptoarb.integration.mapper.TickerDataMapper;
import com.achernov.cryptoarb.integration.core.strategy.MessageParser;
import com.achernov.cryptoarb.integration.exchanges.bybit.dto.BybitRawMessage;
import com.achernov.cryptoarb.integration.util.SymbolNormalizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@RequiredArgsConstructor
@Component
public class BybitMessageParser implements MessageParser {
  private static final String TICKER_TOPIC_PREFIX = "tickers.";
  private static final String OP_PONG = "pong";
  private static final String OP_SUBSCRIBE = "subscribe";

  private final ObjectMapper objectMapper;

  @Override
  public TickerDataMapper parse(String payload) throws Exception {
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

    if (msg.getTopic() != null
            && msg.getTopic().startsWith(TICKER_TOPIC_PREFIX)
            && isMessageValid(msg)) {

      String standardizedSymbol = SymbolNormalizer.normalize(msg.getData().getSymbol());

      return new TickerDataMapper(
              standardizedSymbol,
              msg.getData().getLastPrice(),
              System.currentTimeMillis()
      );
    }

    return null;
  }

  private boolean isMessageValid(BybitRawMessage msg) {
    return msg.getData() != null
            && StringUtils.hasText(msg.getData().getSymbol())
            && StringUtils.hasText(msg.getData().getLastPrice());
  }
}
