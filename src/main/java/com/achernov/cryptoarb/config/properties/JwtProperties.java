package com.achernov.cryptoarb.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(
        String secret,
        AccessTokenConfig access,
        RefreshTokenConfig refresh
) {
  public record AccessTokenConfig(
          Duration ttl,
          String cookieName
  ) {}

  public record RefreshTokenConfig(
          Duration ttl,
          String cookieName,
          String cleanupCron
  ) {}
}
