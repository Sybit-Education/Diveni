/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 Diveni Team, AUME-Team 21/22, HTWG Konstanz
*/
package io.diveni.backend.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple in-memory rate limiter that tracks requests per IP address.
 * Allows up to MAX_REQUESTS requests within WINDOW_SIZE_MS per IP.
 * This is a best-effort protection against abuse, not a production-grade
 * distributed rate limiter.
 */
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

  private static final Logger LOGGER = LoggerFactory.getLogger(RateLimitInterceptor.class);

  static final int MAX_REQUESTS = 60;

  static final long WINDOW_SIZE_MS = 60_000L;

  private final Map<String, Window> requestCounts = new ConcurrentHashMap<>();

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    String clientIp = getClientIp(request);
    Window window = requestCounts.compute(clientIp, (key, existing) -> {
      long now = System.currentTimeMillis();
      if (existing == null || now - existing.startTime > WINDOW_SIZE_MS) {
        return new Window(now, new AtomicInteger(1));
      }
      existing.count.incrementAndGet();
      return existing;
    });

    if (window.count.get() > MAX_REQUESTS) {
      LOGGER.warn("Rate limit exceeded for IP: {}", clientIp);
      response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(),
          "Too many requests. Please try again later.");
      return false;
    }

    return true;
  }

  public void cleanUp() {
    long now = System.currentTimeMillis();
    requestCounts.entrySet().removeIf(entry -> now - entry.getValue().startTime > WINDOW_SIZE_MS);
  }

  private static String getClientIp(HttpServletRequest request) {
    String xfHeader = request.getHeader("X-Forwarded-For");
    if (xfHeader != null && !xfHeader.isBlank()) {
      return xfHeader.split(",")[0].trim();
    }
    return request.getRemoteAddr();
  }

  private record Window(long startTime, AtomicInteger count) {}
}
