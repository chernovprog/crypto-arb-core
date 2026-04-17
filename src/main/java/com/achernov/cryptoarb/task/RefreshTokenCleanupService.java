package com.achernov.cryptoarb.task;

import com.achernov.cryptoarb.repository.RefreshTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@Transactional
@Profile("prod")
public class RefreshTokenCleanupService {

  private final RefreshTokenRepository refreshTokenRepository;

  public RefreshTokenCleanupService(RefreshTokenRepository refreshTokenRepository) {
    this.refreshTokenRepository = refreshTokenRepository;
  }

  @Scheduled(cron = "${app.jwt.refresh.cleanup-cron:0 0 3 * * ?}")
  public void cleanupExpiredRefreshTokens() {
    int deleted = refreshTokenRepository.deleteExpiredTokens(Instant.now());
    log.info("Deleted {} expired refresh tokens", deleted);
  }
}
