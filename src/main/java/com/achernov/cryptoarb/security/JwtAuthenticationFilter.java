package com.achernov.cryptoarb.security;

import com.achernov.cryptoarb.config.properties.JwtProperties;
import com.achernov.cryptoarb.service.TokenBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;
  private final UserDetailsService userDetailsService;
  private final JwtProperties properties;
  private final TokenBlacklistService blacklistService;

  public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider,
                                 UserDetailsService userDetailsService,
                                 JwtProperties properties,
                                 TokenBlacklistService blacklistService) {
    this.jwtTokenProvider = jwtTokenProvider;
    this.userDetailsService = userDetailsService;
    this.properties = properties;
    this.blacklistService = blacklistService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {

    Cookie jwtCookie = WebUtils.getCookie(request, properties.access().cookieName());

    if (jwtCookie != null && StringUtils.hasText(jwtCookie.getValue())) {
      String token = jwtCookie.getValue();

      try {
        if (blacklistService.isBlacklisted(token)) {
          log.warn("Attempt to use blacklisted token");
          filterChain.doFilter(request, response);
          return;
        }

        String username = jwtTokenProvider.extractUsername(token);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
          UserDetails userDetails = userDetailsService.loadUserByUsername(username);

          if (jwtTokenProvider.validateToken(token, userDetails)) {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authToken);
          }
        }
      } catch (Exception ex) {
        log.error("Invalid JWT token", ex);
      }
    }

    filterChain.doFilter(request, response);
  }
}
