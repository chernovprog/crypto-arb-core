package com.achernov.cryptoarb.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TickerDto(
        @JsonProperty("id") Long currencyId,
        @JsonProperty("p") String price,
        @JsonProperty("t") Long timestamp
) {}
