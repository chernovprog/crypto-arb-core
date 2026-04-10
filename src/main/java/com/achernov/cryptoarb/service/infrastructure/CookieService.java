package com.achernov.cryptoarb.service.infrastructure;

import com.achernov.cryptoarb.config.properties.CookieProperties;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class CookieService {

  private final CookieProperties properties;

  public CookieService(CookieProperties properties) {
    this.properties = properties;
  }

  public ResponseCookie createResponseCookie(String name, String value, Duration maxAge) {
    return ResponseCookie.from(name, value)
            .httpOnly(properties.httpOnly())
            .secure(properties.secure())
            .sameSite(properties.sameSite())
            .maxAge(maxAge)
            .path("/")
            .build();
  }

  public ResponseCookie deleteCookie(String name) {
    return ResponseCookie.from(name, "")
            .maxAge(0)
            .path("/")
            .build();
  }
}
