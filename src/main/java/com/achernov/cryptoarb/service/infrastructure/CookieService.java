package com.achernov.cryptoarb.service.infrastructure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class CookieService {

  @Value("${app.cookie.secure:true}")
  private boolean cookieSecure;

  public ResponseCookie createResponseCookie(String name, String value, long expirationMs) {
    return ResponseCookie.from(name, value)
            .httpOnly(true)
            .secure(cookieSecure)
            .sameSite("Strict")
            .maxAge(Duration.ofMillis(expirationMs))
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
