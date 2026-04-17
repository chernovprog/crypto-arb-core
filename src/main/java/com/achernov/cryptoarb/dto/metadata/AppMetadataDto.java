package com.achernov.cryptoarb.dto.metadata;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AppMetadataDto(
        List<ExchangeSubscriptionDto> exchangeSubscriptions,
        TradingPairSubscriptionDto tradingPairSubscriptions,
        Map<Long, CurrencyDto> currencySubscriptions,
        UiConfigDto uiConfig
) {}
