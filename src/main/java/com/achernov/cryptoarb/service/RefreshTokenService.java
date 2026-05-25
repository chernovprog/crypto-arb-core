package com.achernov.cryptoarb.service;

import com.achernov.cryptoarb.config.properties.JwtProperties;
import com.achernov.cryptoarb.entity.RefreshToken;
import com.achernov.cryptoarb.entity.User;
import com.achernov.cryptoarb.exception.RefreshTokenExpiredException;
import com.achernov.cryptoarb.repository.redis.RefreshTokenRepository;
import com.achernov.cryptoarb.repository.jpa.UserRepository;
import com.achernov.cryptoarb.service.infrastructure.ClientInfoResolver;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

  private final RefreshTokenRepository refreshTokenRepository;
  private final ClientInfoResolver clientInfoResolver;
  private final JwtProperties properties;
  private final UserRepository userRepository;

  public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
                             ClientInfoResolver clientInfoResolver,
                             JwtProperties properties,
                             UserRepository userRepository) {
    this.refreshTokenRepository = refreshTokenRepository;
    this.clientInfoResolver = clientInfoResolver;
    this.properties = properties;
    this.userRepository = userRepository;
  }

  public RefreshToken createRefreshToken(User user) {
    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
            .getRequestAttributes()).getRequest();

    Instant expiration = Instant.now().plus(properties.refresh().ttl());

    RefreshToken refreshToken = new RefreshToken();
    refreshToken.setToken(UUID.randomUUID().toString());
    refreshToken.setUserId(user.getId());
    refreshToken.setUserEmail(user.getEmail());
    refreshToken.setExpiryDate(expiration);
    refreshToken.setDeviceId(clientInfoResolver.extractDeviceInfo(request));
    refreshToken.setIpAddress(clientInfoResolver.getClientIp(request));
    refreshToken.setUserAgent(clientInfoResolver.getUserAgent(request));

    return refreshTokenRepository.save(refreshToken);
  }

  public Optional<RefreshToken> findByToken(String token) {
    return refreshTokenRepository.findById(token);
  }

  public User getUserByToken(RefreshToken token) {
    return userRepository.findById(token.getUserId())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
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
