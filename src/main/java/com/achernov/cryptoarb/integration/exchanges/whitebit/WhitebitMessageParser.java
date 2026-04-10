package com.achernov.cryptoarb.integration.exchanges.whitebit;

import com.achernov.cryptoarb.dto.TickerDto;
import com.achernov.cryptoarb.integration.core.strategy.MessageParser;
import com.achernov.cryptoarb.integration.exchanges.whitebit.dto.WhitebitRawMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class WhitebitMessageParser implements MessageParser {
  private static final String SUBSCRIPTION_NAME = "lastprice_update";

  private final ObjectMapper objectMapper;

  @Override
  public TickerDto parse(String payload) throws Exception {
    WhitebitRawMessage msg = objectMapper.readValue(payload, WhitebitRawMessage.class);

    if (msg.isPong()) {
      log.trace("Pong received");
      return null;
    }

    if (msg.getResult() != null) {
      log.info("System message received: {}", msg.getResult());
    }

    if (msg.getMethod() != null && SUBSCRIPTION_NAME.equals(msg.getMethod())
            && msg.getParams() != null && msg.getParams().size() == 2) {
      return new TickerDto(
              msg.getParams().get(0),
              msg.getParams().get(1),
              System.currentTimeMillis()
      );
    }

    return null;
  }
}
