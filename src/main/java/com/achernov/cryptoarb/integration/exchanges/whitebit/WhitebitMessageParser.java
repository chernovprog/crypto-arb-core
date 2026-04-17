package com.achernov.cryptoarb.integration.exchanges.whitebit;

import com.achernov.cryptoarb.integration.mapper.TickerDataMapper;
import com.achernov.cryptoarb.integration.core.strategy.MessageParser;
import com.achernov.cryptoarb.integration.exchanges.whitebit.dto.WhitebitRawMessage;
import com.achernov.cryptoarb.integration.util.SymbolNormalizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@RequiredArgsConstructor
@Component
public class WhitebitMessageParser implements MessageParser {
  private static final String SUBSCRIPTION_NAME = "lastprice_update";

  private final ObjectMapper objectMapper;

  @Override
  public TickerDataMapper parse(String payload) throws Exception {
    WhitebitRawMessage msg = objectMapper.readValue(payload, WhitebitRawMessage.class);

    if (msg.isPong()) {
      log.trace("Pong received");
      return null;
    }

    if (msg.getResult() != null) {
      log.info("System message received: {}", msg.getResult());
    }

    if (SUBSCRIPTION_NAME.equals(msg.getMethod()) && isMessageValid(msg)) {

      String standardizedSymbol = SymbolNormalizer.normalize(msg.getParams().get(0));

      return new TickerDataMapper(
              standardizedSymbol,
              msg.getParams().get(1),
              System.currentTimeMillis()
      );
    }

    return null;
  }

  private boolean isMessageValid(WhitebitRawMessage msg) {
    return msg.getParams() != null
            && msg.getParams().size() == 2
            && StringUtils.hasText(msg.getParams().get(0))
            && StringUtils.hasText(msg.getParams().get(1));
  }
}
