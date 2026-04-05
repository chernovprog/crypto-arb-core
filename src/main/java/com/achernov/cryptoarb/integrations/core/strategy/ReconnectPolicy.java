package com.achernov.cryptoarb.integrations.core.strategy;

public interface ReconnectPolicy {
  boolean canRetry(int attempt);
  long nextDelay(int attempt);
}
