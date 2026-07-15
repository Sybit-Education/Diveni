/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 Diveni Team, AUME-Team 21/22, HTWG Konstanz
*/
package io.diveni.backend.service.ai;

import io.diveni.backend.dto.AiServiceResponse;
import io.diveni.backend.dto.GptConfidentialData;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;

@Service
public class AiService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AiService.class);

  @Value("${python_ai_url}")
  private String aiUrl;

  @Autowired private RestTemplate restTemplate;

  @PostConstruct
  public void logConfig() {
    LOGGER.info("Url to Server is: " + aiUrl);
  }

  public ResponseEntity<String> executeRequest(String url, HttpMethod method, Object body)
      throws RestClientException {
    LOGGER.debug("--> executeRequest()");
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(List.of(MediaType.APPLICATION_JSON));
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<Object> request = new HttpEntity<>(body, headers);
    LOGGER.debug("<-- executeRequest()");
    return restTemplate.exchange(url, method, request, String.class);
  }

  public ResponseEntity<String> improveTitle(GptConfidentialData data) {
    LOGGER.debug("--> improveTitle()");
    Map<String, Object> content = new HashMap<>();
    content.put("name", data.getTitle());
    content.put("confidential_data", data.getConfidentialData().toMap());
    ResponseEntity<String> response =
        executeRequest(aiUrl + "/improve-title", HttpMethod.POST, content);
    LOGGER.debug("<-- improveTitle()");
    return response;
  }

  public ResponseEntity<String> improveDescription(GptConfidentialData data) {
    LOGGER.debug("--> improveDescription()");
    Map<String, Object> content = new HashMap<>();
    content.put("title", data.getTitle());
    content.put("description", data.getDescription());
    content.put("confidential_data", data.getConfidentialData().toMap());
    content.put("language", data.getLanguage());
    ResponseEntity<String> response =
        executeRequest(aiUrl + "/improve-description", HttpMethod.POST, content);
    LOGGER.debug("<-- improveDescription()");
    return response;
  }

  public ResponseEntity<String> grammarCheck(GptConfidentialData data) {
    LOGGER.debug("--> grammarCheck()");
    Map<String, Object> content = new HashMap<>();
    content.put("title", data.getTitle());
    content.put("description", data.getDescription());
    content.put("confidential_data", data.getConfidentialData().toMap());
    content.put("language", data.getLanguage());
    ResponseEntity<String> response =
        executeRequest(aiUrl + "/grammar-check", HttpMethod.POST, content);
    LOGGER.debug("<-- grammarCheck()");
    return response;
  }

  public ResponseEntity<String> estimateUserStory(GptConfidentialData data) {
    LOGGER.debug("--> estimateUserStory()");
    Map<String, Object> content = new HashMap<>();
    content.put("title", data.getTitle());
    content.put("description", data.getDescription());
    content.put("confidential_data", data.getConfidentialData().toMap());
    content.put("voteSet", data.getVoteSet());
    ResponseEntity<String> response =
        executeRequest(aiUrl + "/estimate-user-story", HttpMethod.POST, content);
    LOGGER.debug("<-- estimateUserStory()");
    return response;
  }

  public ResponseEntity<String> splitUserStory(GptConfidentialData data) {
    LOGGER.debug("--> splitUserStory()");
    Map<String, Object> content = new HashMap<>();
    content.put("title", data.getTitle());
    content.put("description", data.getDescription());
    content.put("confidential_data", data.getConfidentialData().toMap());
    content.put("language", data.getLanguage());
    ResponseEntity<String> response =
        executeRequest(aiUrl + "/split-user-story", HttpMethod.POST, content);
    LOGGER.debug("<-- splitUserStory()");
    return response;
  }

  public ResponseEntity<String> markDescription(GptConfidentialData data) {
    LOGGER.debug("--> markDescription");
    Map<String, Object> content = new HashMap<>();
    content.put("title", data.getTitle());
    content.put("description", data.getDescription());
    content.put("confidential_data", data.getConfidentialData().toMap());
    content.put("language", data.getLanguage());
    ResponseEntity<String> response =
        executeRequest(aiUrl + "/mark-description", HttpMethod.POST, content);
    LOGGER.debug("<-- markDescription");
    return response;
  }

  @SuppressWarnings("unchecked")
  public ResponseEntity<AiServiceResponse> ensureServiceAndApiKey() {
    LOGGER.debug("--> ensureServiceAndApiKey()");
    AiServiceResponse result;
    try {
      ResponseEntity<Map<String, Object>> response =
          restTemplate.exchange(
              aiUrl + "/check-api-key",
              HttpMethod.GET,
              null,
              (Class<Map<String, Object>>) (Class<?>) Map.class);
      Map<String, Object> body = response.getBody();
      result =
          AiServiceResponse.builder()
              .apiKeyValid(body != null && Boolean.TRUE.equals(body.get("has_api_key")))
              .serviceAvailable(response.getStatusCode().is2xxSuccessful())
              .build();
    } catch (RestClientException rce) {
      LOGGER.debug("AI Service is offline/unavailable: {}", rce.getMessage());
      result = AiServiceResponse.builder().apiKeyValid(false).serviceAvailable(false).build();
    }
    LOGGER.debug("<-- ensureServiceAndApiKey()");
    return new ResponseEntity<>(result, HttpStatus.OK);
  }
}
