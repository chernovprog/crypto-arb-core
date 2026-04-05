package com.achernov.cryptoarb.config.properties;

import jakarta.validation.Valid;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

@Validated
@ConfigurationProperties(prefix = "app.integrations")
public record ExternalExchangeProperties(
        @Valid Map<String, ExchangeProperties> providers
) {

  public ExchangeProperties get(String exchangeName) {
    ExchangeProperties properties = providers.get(exchangeName);
    if (properties == null) {
      throw new RuntimeException("Provider not found: " + exchangeName);
    }
    return properties;
  }
}
