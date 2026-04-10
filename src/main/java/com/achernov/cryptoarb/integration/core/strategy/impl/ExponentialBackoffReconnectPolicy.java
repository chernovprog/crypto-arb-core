package com.achernov.cryptoarb.integration.core.strategy.impl;

import com.achernov.cryptoarb.integration.core.strategy.ReconnectPolicy;

public class ExponentialBackoffReconnectPolicy implements ReconnectPolicy {

  private final int maxAttempts;
  private final long initialDelay;

  public ExponentialBackoffReconnectPolicy(int maxAttempts, long initialDelay) {
    this.maxAttempts = maxAttempts;
    this.initialDelay = initialDelay;
  }

  @Override
  public boolean canRetry(int attempt) {
    return attempt < maxAttempts;
  }

  @Override
  public long nextDelay(int attempt) {
    long delay = initialDelay * (long) Math.pow(2, attempt - 1);
    long jitter = (long) (delay * 0.1 * (Math.random() - 0.5) * 2);
    return Math.min(delay + jitter, 120_000);
  }
}
