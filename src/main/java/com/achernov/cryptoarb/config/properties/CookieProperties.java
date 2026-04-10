package com.achernov.cryptoarb.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.cookie")
public record CookieProperties(
        Boolean secure,
        Boolean httpOnly,
        String sameSite
) {
  public CookieProperties {
    if (secure == null) secure = true;
    if (httpOnly == null) httpOnly = true;
    if (sameSite == null || sameSite.isBlank()) {
      sameSite = "Strict";
    }
  }
}
