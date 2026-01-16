package com.achernov.cryptoarb.controller;

import com.achernov.cryptoarb.dto.AuthRequest;
import com.achernov.cryptoarb.entity.RefreshToken;
import com.achernov.cryptoarb.entity.User;
import com.achernov.cryptoarb.exception.InvalidRefreshTokenException;
import com.achernov.cryptoarb.repository.UserRepository;
import com.achernov.cryptoarb.security.JwtTokenProvider;
import com.achernov.cryptoarb.service.RefreshTokenService;
import com.achernov.cryptoarb.service.UserDetailsServiceImpl;
import com.achernov.cryptoarb.service.infrastructure.CookieService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final JwtTokenProvider jwtTokenProvider;
  private final RefreshTokenService refreshTokenService;
  private final UserDetailsServiceImpl userDetailsService;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final CookieService cookieService;

  @Value("${jwt.access.expiration}")
  private long jwtExpirationMs;

  @Value("${jwt.refresh.expiration}")
  private long refreshExpirationMs;

  @Value("${jwt.access.cookie-name}")
  private String accessCookieName;

  @Value("${jwt.refresh.cookie-name}")
  private String refreshCookieName;


  public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider,
                        RefreshTokenService refreshTokenService, UserDetailsServiceImpl userDetailsService,
                        UserRepository userRepository, PasswordEncoder passwordEncoder,
                        CookieService cookieService) {
    this.authenticationManager = authenticationManager;
    this.jwtTokenProvider = jwtTokenProvider;
    this.refreshTokenService = refreshTokenService;
    this.userDetailsService = userDetailsService;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.cookieService = cookieService;
  }

  @PostMapping("/signup")
  public ResponseEntity<?> signup(@RequestBody AuthRequest authRequest) {
    if (!StringUtils.hasText(authRequest.username()) || !StringUtils.hasText(authRequest.password())) {
      return ResponseEntity.badRequest()
              .body(Map.of("error", "Username and password are required"));
    }

    if (userRepository.findByUsername(authRequest.username()).isPresent()) {
      return ResponseEntity.status(HttpStatus.CONFLICT)
              .body(Map.of("error", "Registration failed. Please check your data and try again"));
    }

    User user = new User();
    user.setUsername(authRequest.username().trim());
    user.setPassword(passwordEncoder.encode(authRequest.password().trim()));
    user.setEmail(authRequest.username().trim());

    userRepository.save(user);

    UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
    Authentication authentication = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());

    SecurityContextHolder.getContext().setAuthentication(authentication);

    String jwt = jwtTokenProvider.generateToken(authentication.getName());
    RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

    ResponseCookie accessCookie = cookieService.createResponseCookie(accessCookieName, jwt, jwtExpirationMs);
    ResponseCookie refreshCookie = cookieService.createResponseCookie(
            refreshCookieName, refreshToken.getToken(), refreshExpirationMs);

    return ResponseEntity.status(HttpStatus.CREATED)
            .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
            .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
            .body(Map.of("message", "User registered and logged in successfully"));
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
    Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password())
    );

    SecurityContextHolder.getContext().setAuthentication(authentication);

    String jwt = jwtTokenProvider.generateToken(authRequest.username());

    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    User user = (User) userDetails;

    RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

    ResponseCookie accessCookie = cookieService.createResponseCookie(accessCookieName, jwt, jwtExpirationMs);
    ResponseCookie refreshCookie = cookieService.createResponseCookie(
            refreshCookieName, refreshToken.getToken(), refreshExpirationMs);

    return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
            .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
            .body(Map.of("message", "Logged in successfully"));
  }

  @PostMapping("/refresh")
  public ResponseEntity<?> refreshToken(HttpServletRequest request) {
    Cookie refreshTokenCookie = WebUtils.getCookie(request, refreshCookieName);

    if (refreshTokenCookie == null) {
      throw new InvalidRefreshTokenException("Authentication required");
    }

    return refreshTokenService.findByToken(refreshTokenCookie.getValue())
            .map(refreshTokenService::verifyExpiration)
            .map(refreshToken -> {
              refreshTokenService.deleteToken(refreshToken);

              User user = refreshToken.getUser();

              String newJwt = jwtTokenProvider.generateToken(user.getUsername());
              RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);

              ResponseCookie accessCookie = cookieService.createResponseCookie(accessCookieName, newJwt, jwtExpirationMs);
              ResponseCookie refreshCookie = cookieService.createResponseCookie(
                      refreshCookieName, newRefreshToken.getToken(), refreshExpirationMs);

              return ResponseEntity.ok()
                      .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                      .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                      .body(Map.of("message", "Token refreshed"));
            })
            .orElseThrow(() -> new InvalidRefreshTokenException("Authentication required"));
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logout(HttpServletRequest request) {
    Cookie refreshTokenCookie = WebUtils.getCookie(request, refreshCookieName);

    Optional.ofNullable(refreshTokenCookie)
            .map(Cookie::getValue)
            .flatMap(refreshTokenService::findByToken)
            .ifPresent(refreshTokenService::deleteToken);

    ResponseCookie accessCookie = cookieService.deleteCookie(accessCookieName);
    ResponseCookie refreshCookie = cookieService.deleteCookie(refreshCookieName);

    SecurityContextHolder.clearContext();

    return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
            .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
            .body(Map.of("message", "Logged out"));
  }

  @GetMapping("/me")
  public ResponseEntity<Void> checkAuth() {
    return ResponseEntity.noContent().build();
  }
}
