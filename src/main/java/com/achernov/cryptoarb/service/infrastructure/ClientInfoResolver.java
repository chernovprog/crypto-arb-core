package com.achernov.cryptoarb.service.infrastructure;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class ClientInfoResolver {

  public String getClientIp(HttpServletRequest request) {
    String ip = request.getHeader("X-Forwarded-For");
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("X-Real-IP");
    }
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr();
    }
    return ip;
  }

  public String extractDeviceInfo(HttpServletRequest request) {
    String userAgent = getUserAgent(request);

    if (userAgent == null) return "unknown";
    if (userAgent.contains("Mobile") || userAgent.contains("Android") || userAgent.contains("iPhone")) {
      return "Mobile";
    }
    if (userAgent.contains("Tablet") || userAgent.contains("iPad")) {
      return "Tablet";
    }
    return "Desktop";
  }

  public String getUserAgent(HttpServletRequest request) {
    return request.getHeader("User-Agent");
  }
}
