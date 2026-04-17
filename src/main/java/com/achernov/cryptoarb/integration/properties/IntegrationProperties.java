package com.achernov.cryptoarb.integration.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;

@Validated
@ConfigurationProperties(prefix = "app.integrations")
public record IntegrationProperties(
        boolean enabled,
        List<@NotEmpty String> baseCurrency,
        @NotNull String quoteCurrency,
        @Valid Map<String, ExchangeConfig> providers
) {
  public IntegrationProperties {
    if (baseCurrency == null || baseCurrency.isEmpty()) {
      baseCurrency = List.of();
    }
    if (providers == null || providers.isEmpty()) {
      providers = Map.of();
    }
  }

  public ExchangeConfig get(String exchangeName) {
    ExchangeConfig properties = providers.get(exchangeName);
    if (properties == null) {
      throw new RuntimeException("Provider not found: " + exchangeName);
    }
    return properties;
  }
}
