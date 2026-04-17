package com.achernov.cryptoarb.integration.core.strategy;

import com.achernov.cryptoarb.integration.mapper.TickerDataMapper;

public interface MessageParser {
  TickerDataMapper parse(String payload) throws Exception;
}
