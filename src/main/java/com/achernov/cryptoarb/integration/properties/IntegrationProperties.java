package com.achernov.cryptoarb.integration.properties;

import jakarta.validation.Valid;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

@Validated
@ConfigurationProperties(prefix = "app.integrations")
public record IntegrationProperties(
        boolean enabled,
        @Valid Map<String, ExchangeConfig> providers
) {

  public ExchangeConfig get(String exchangeName) {
    ExchangeConfig properties = providers.get(exchangeName);
    if (properties == null) {
      throw new RuntimeException("Provider not found: " + exchangeName);
    }
    return properties;
  }
}
