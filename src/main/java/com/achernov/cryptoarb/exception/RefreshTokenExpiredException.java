package com.achernov.cryptoarb.exception;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.AuthenticationException;

public class RefreshTokenExpiredException extends AuthenticationException {

  public RefreshTokenExpiredException(@Nullable String msg) {
    super(msg);
  }
}
