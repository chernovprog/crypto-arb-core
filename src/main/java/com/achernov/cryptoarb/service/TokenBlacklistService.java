package com.achernov.cryptoarb.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;

@Service
public class TokenBlacklistService {

  private static final String BLACKLIST_PREFIX = "jwt_blacklist:";
  private final RedisTemplate<String, String> redisTemplate;

  public TokenBlacklistService(RedisTemplate<String, String> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  public void blacklistToken(String token, Date expirationDate) {
    long ttlSeconds = (expirationDate.getTime() - System.currentTimeMillis()) / 1000;
    if (ttlSeconds > 0) {
      redisTemplate.opsForValue().set(
              BLACKLIST_PREFIX + token,
              "blacklisted",
              Duration.ofSeconds(ttlSeconds)
      );
    }
  }

  public boolean isBlacklisted(String token) {
    return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + token));
  }
}
