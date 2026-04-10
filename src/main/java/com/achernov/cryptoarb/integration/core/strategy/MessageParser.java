package com.achernov.cryptoarb.integration.core.strategy;

import com.achernov.cryptoarb.dto.TickerDto;

public interface MessageParser {
  TickerDto parse(String payload) throws Exception;
}
