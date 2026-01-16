package com.achernov.cryptoarb.exception;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.AuthenticationException;

public class InvalidRefreshTokenException extends AuthenticationException {

  public InvalidRefreshTokenException(@Nullable String msg) {
    super(msg);
  }
}
