package com.achernov.cryptoarb.service;

import com.achernov.cryptoarb.entity.RefreshToken;
import com.achernov.cryptoarb.entity.User;
import com.achernov.cryptoarb.exception.RefreshTokenExpiredException;
import com.achernov.cryptoarb.repository.RefreshTokenRepository;
import com.achernov.cryptoarb.service.infrastructure.ClientInfoResolver;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

  @Value("${jwt.refresh.expiration}")
  private long refreshExpirationMs;

  private final RefreshTokenRepository refreshTokenRepository;
  private final ClientInfoResolver clientInfoResolver;

  public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, ClientInfoResolver clientInfoResolver) {
    this.refreshTokenRepository = refreshTokenRepository;
    this.clientInfoResolver = clientInfoResolver;
  }

  public RefreshToken createRefreshToken(User user) {
    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
            .getRequestAttributes()).getRequest();

    RefreshToken refreshToken = new RefreshToken();
    refreshToken.setUser(user);
    refreshToken.setExpiryDate(Instant.now().plusMillis(refreshExpirationMs));
    refreshToken.setToken(UUID.randomUUID().toString());
    refreshToken.setDeviceId(clientInfoResolver.extractDeviceInfo(request));
    refreshToken.setIpAddress(clientInfoResolver.getClientIp(request));
    refreshToken.setUserAgent(clientInfoResolver.getUserAgent(request));

    return refreshTokenRepository.save(refreshToken);
  }

  public Optional<RefreshToken> findByToken(String token) {
    return refreshTokenRepository.findByToken(token);
  }

  public void deleteToken(RefreshToken token) {
    refreshTokenRepository.delete(token);
  }

  public RefreshToken verifyExpiration(RefreshToken token) {
    if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
      refreshTokenRepository.delete(token);
      throw new RefreshTokenExpiredException("Session expired");
    }
    return token;
  }
}
