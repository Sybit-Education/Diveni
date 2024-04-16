package io.diveni.backend.service.ai;

import com.google.gson.Gson;
import io.diveni.backend.dto.GptConfidentialData;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AiService.class);

  @Value("${python_ai_url}")
  private String aiUrl;

  @PostConstruct
  public void logConfig() {
    LOGGER.info("Url to Server is: " + aiUrl);
  }

  public ResponseEntity<String> executeRequest(String url, HttpMethod method, Object body) {
    LOGGER.debug("--> executeRequest()");
    // Create a RestTemplate object
    RestTemplate restTemplate = new RestTemplate();
    // Set the headers for the request
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
        executeRequest(aiUrl + "/improve-title", HttpMethod.POST, new Gson().toJson(content));
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
        executeRequest(aiUrl + "/improve-description", HttpMethod.POST, new Gson().toJson(content));
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
        executeRequest(aiUrl + "/grammar-check", HttpMethod.POST, new Gson().toJson(content));
    LOGGER.debug("<-- grammarCheck()");
    return response;
  }
}
