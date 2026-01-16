package com.achernov.cryptoarb.task;

import com.achernov.cryptoarb.repository.RefreshTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Slf4j
public class RefreshTokenCleanupService {

  private final RefreshTokenRepository refreshTokenRepository;

  public RefreshTokenCleanupService(RefreshTokenRepository refreshTokenRepository) {
    this.refreshTokenRepository = refreshTokenRepository;
  }

  @Scheduled(cron = "${jwt.refresh.cleanup-cron:0 0 3 * * ?}")
  public void cleanupExpiredRefreshTokens() {
    int deleted = refreshTokenRepository.deleteExpiredTokens(Instant.now());
    log.info("Deleted {} expired refresh tokens", deleted);
  }
}
