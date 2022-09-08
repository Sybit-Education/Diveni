package io.diveni.backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(CorsConfig.class);

  @Value("${SERVER_URL:#{null}}")
  private String SERVER_URL;

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    LOGGER.info("CORS: allow configured origin '{}'", SERVER_URL);
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins(SERVER_URL);
      }
    };
  }
}
