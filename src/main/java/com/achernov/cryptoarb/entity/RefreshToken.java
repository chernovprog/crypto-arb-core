package com.achernov.cryptoarb.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.time.Instant;

@Data
@RedisHash("refresh_tokens")
public class RefreshToken {

  @Id
  private String token;

  @Indexed
  private Long userId;

  private String userEmail;

  private Instant expiryDate;

  private String deviceId;

  private String ipAddress;

  private String userAgent;

  private Instant createdAt;

  @TimeToLive
  public long getTimeToLive() {
    long ttl = expiryDate.getEpochSecond() - Instant.now().getEpochSecond();
    return ttl > 0 ? ttl : 0;
  }
}
