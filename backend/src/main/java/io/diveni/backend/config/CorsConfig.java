/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 Diveni Team, AUME-Team 21/22, HTWG Konstanz
*/
package io.diveni.backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.annotation.PostConstruct;

@Configuration
public class CorsConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(CorsConfig.class);

  @Value("${SERVER_URL:#{null}}")
  private String SERVER_URL;

  private final String[] ALLOWED_METHODS = {"GET", "POST", "PUT", "DELETE"};

  @PostConstruct
  public void logConfig() {
    LOGGER.info("CorsConfig:");
    LOGGER.info("    SERVER_URL={}", SERVER_URL);
  }

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins(SERVER_URL).allowedMethods(ALLOWED_METHODS);
      }
    };
  }
}
