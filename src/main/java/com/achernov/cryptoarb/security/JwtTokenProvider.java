package com.achernov.cryptoarb.security;

import com.achernov.cryptoarb.config.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.function.Function;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
public class JwtTokenProvider {

  private final JwtProperties properties;

  public JwtTokenProvider(JwtProperties properties) {
    this.properties = properties;
  }

  private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(properties.secret().getBytes(UTF_8));
  }

  public String generateToken(String username) {
    Date expiration = Date.from(Instant.now().plus(properties.access().ttl()));
    return Jwts.builder()
            .subject(username)
            .issuedAt(new Date())
            .expiration(expiration)
            .signWith(getSigningKey(), Jwts.SIG.HS512)
            .compact();
  }

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  public boolean validateToken(String token, UserDetails userDetails) {
    final String tokenUsername = extractUsername(token);
    return tokenUsername.equals(userDetails.getUsername())
            && !isTokenExpired(token)
            && userDetails.isEnabled()
            && userDetails.isAccountNonLocked();
  }
}
