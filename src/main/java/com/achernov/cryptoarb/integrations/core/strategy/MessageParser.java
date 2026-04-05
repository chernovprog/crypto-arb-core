package com.achernov.cryptoarb.integrations.core.strategy;

import com.achernov.cryptoarb.dto.TickerDto;

public interface MessageParser {
  TickerDto parse(String payload) throws Exception;
}
