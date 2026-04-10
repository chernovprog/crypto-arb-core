package com.achernov.cryptoarb.controller;

import com.achernov.cryptoarb.config.properties.JwtProperties;
import com.achernov.cryptoarb.dto.AuthRequest;
import com.achernov.cryptoarb.dto.AuthResponse;
import com.achernov.cryptoarb.entity.RefreshToken;
import com.achernov.cryptoarb.entity.User;
import com.achernov.cryptoarb.exception.InvalidRefreshTokenException;
import com.achernov.cryptoarb.repository.UserRepository;
import com.achernov.cryptoarb.security.JwtTokenProvider;
import com.achernov.cryptoarb.service.RefreshTokenService;
import com.achernov.cryptoarb.service.infrastructure.CookieService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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
  private final UserDetailsService userDetailsService;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final CookieService cookieService;
  private final JwtProperties properties;

  public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider,
                        RefreshTokenService refreshTokenService, UserDetailsService userDetailsService,
                        UserRepository userRepository, PasswordEncoder passwordEncoder,
                        CookieService cookieService, JwtProperties properties) {
    this.authenticationManager = authenticationManager;
    this.jwtTokenProvider = jwtTokenProvider;
    this.refreshTokenService = refreshTokenService;
    this.userDetailsService = userDetailsService;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.cookieService = cookieService;
    this.properties = properties;
  }

  @PostMapping("/signup")
  public ResponseEntity<?> signup(@RequestBody AuthRequest authRequest) {
    if (!StringUtils.hasText(authRequest.email()) || !StringUtils.hasText(authRequest.password())) {
      return ResponseEntity.badRequest()
              .body(Map.of("error", "Username and password are required"));
    }

    if (userRepository.findByUsername(authRequest.email()).isPresent()) {
      return ResponseEntity.status(HttpStatus.CONFLICT)
              .body(Map.of("error", "Registration failed. Please check your data and try again"));
    }

    User user = new User();
    user.setUsername(authRequest.email().trim());
    user.setPassword(passwordEncoder.encode(authRequest.password().trim()));
    user.setEmail(authRequest.email().trim());

    userRepository.save(user);

    UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
    Authentication authentication = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());

    SecurityContextHolder.getContext().setAuthentication(authentication);

    String jwt = jwtTokenProvider.generateToken(authentication.getName());
    RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

    ResponseCookie accessCookie = cookieService.createResponseCookie(
            properties.access().cookieName(),
            jwt,
            properties.access().ttl()
    );

    ResponseCookie refreshCookie = cookieService.createResponseCookie(
            properties.refresh().cookieName(),
            refreshToken.getToken(),
            properties.refresh().ttl()
    );

    return ResponseEntity.status(HttpStatus.CREATED)
            .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
            .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
            .body(Map.of("message", "User registered and logged in successfully"));
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
    Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(authRequest.email(), authRequest.password())
    );

    SecurityContextHolder.getContext().setAuthentication(authentication);

    String jwt = jwtTokenProvider.generateToken(authRequest.email());

    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    User user = (User) userDetails;

    RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

    ResponseCookie accessCookie = cookieService.createResponseCookie(
            properties.access().cookieName(),
            jwt,
            properties.access().ttl()
    );

    ResponseCookie refreshCookie = cookieService.createResponseCookie(
            properties.refresh().cookieName(),
            refreshToken.getToken(),
            properties.refresh().ttl()
    );

    return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
            .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
            .body(Map.of("message", "Logged in successfully"));
  }

  @PostMapping("/refresh")
  public ResponseEntity<?> refreshToken(HttpServletRequest request) {
    Cookie refreshTokenCookie = WebUtils.getCookie(request, properties.refresh().cookieName());

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

              ResponseCookie accessCookie = cookieService.createResponseCookie(
                      properties.access().cookieName(),
                      newJwt,
                      properties.access().ttl()
              );

              ResponseCookie refreshCookie = cookieService.createResponseCookie(
                      properties.refresh().cookieName(),
                      newRefreshToken.getToken(),
                      properties.refresh().ttl()
              );

              return ResponseEntity.ok()
                      .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                      .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                      .body(Map.of("message", "Token refreshed"));
            })
            .orElseThrow(() -> new InvalidRefreshTokenException("Authentication required"));
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logout(HttpServletRequest request) {
    Cookie refreshTokenCookie = WebUtils.getCookie(request, properties.refresh().cookieName());

    Optional.ofNullable(refreshTokenCookie)
            .map(Cookie::getValue)
            .flatMap(refreshTokenService::findByToken)
            .ifPresent(refreshTokenService::deleteToken);

    ResponseCookie accessCookie = cookieService
            .deleteCookie(properties.access().cookieName());

    ResponseCookie refreshCookie = cookieService
            .deleteCookie(properties.refresh().cookieName());

    SecurityContextHolder.clearContext();

    return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
            .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
            .body(Map.of("message", "Logged out"));
  }

  @GetMapping("/me")
  public ResponseEntity<AuthResponse> checkAuth(@AuthenticationPrincipal User user) {
    AuthResponse response = new AuthResponse(
            user.getId(),
            user.getFirstName(),
            user.getEmail()
    );

    return ResponseEntity.ok(response);
  }
}
