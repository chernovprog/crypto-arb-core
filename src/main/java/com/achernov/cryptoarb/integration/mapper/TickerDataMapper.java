package com.achernov.cryptoarb.integration.mapper;

public record TickerDataMapper(
        String symbol,
        String price,
        Long timestamp
) {}
