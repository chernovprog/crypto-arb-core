package com.achernov.cryptoarb.integration.core.strategy;

public interface ReconnectPolicy {
  boolean canRetry(int attempt);
  long nextDelay(int attempt);
}
