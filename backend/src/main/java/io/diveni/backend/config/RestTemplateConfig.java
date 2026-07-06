package io.diveni.backend.config;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@Configuration
public class RestTemplateConfig {

  @Bean
  public RestTemplate restTemplate() {
    PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
    connectionManager.setMaxTotal(200);
    connectionManager.setDefaultMaxPerRoute(20);

    CloseableHttpClient httpClient =
        HttpClientBuilder.create()
            .setConnectionManager(connectionManager)
            .evictIdleConnections(30, TimeUnit.SECONDS)
            .build();

    HttpComponentsClientHttpRequestFactory factory =
        new HttpComponentsClientHttpRequestFactory(httpClient);
    factory.setConnectTimeout(5_000);
    factory.setReadTimeout(10_000);

    return new RestTemplate(factory);
  }
}
