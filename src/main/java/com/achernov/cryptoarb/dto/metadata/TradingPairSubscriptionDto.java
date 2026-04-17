package com.achernov.cryptoarb.dto.metadata;

import java.util.Set;

public record TradingPairSubscriptionDto(
        Long quoteCurrency,
        Set<Long> baseCurrencies
) {}
