package io.diveni.backend.service.ai;

import com.google.gson.Gson;
import io.diveni.backend.model.UserStory;
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

  public ResponseEntity<String> improveTitle(UserStory userStory) {
    LOGGER.debug("--> improveTitle()");
    Map<String, String> content = new HashMap<>();
    content.put("name", userStory.getTitle());
    ResponseEntity<String> response =
        executeRequest(aiUrl + "/improve-title", HttpMethod.POST, new Gson().toJson(content));
    LOGGER.debug("<-- improveTitle()");
    return response;
  }

  public ResponseEntity<String> improveDescription(UserStory userStory) {
    LOGGER.debug("--> improveDescription()");
    Map<String, String> content = new HashMap<>();
    content.put("title", userStory.getTitle());
    content.put("description", userStory.getDescription());
    ResponseEntity<String> response =
        executeRequest(aiUrl + "/improve-description", HttpMethod.POST, new Gson().toJson(content));
    LOGGER.debug("<-- improveDescription()");
    return response;
  }

  public ResponseEntity<String> grammarCheck(UserStory userStory) {
    LOGGER.debug("--> grammarCheck()");
    Map<String, String> content = new HashMap<>();
    content.put("title", userStory.getTitle());
    content.put("description", userStory.getDescription());
    ResponseEntity<String> response =
      executeRequest(aiUrl + "/grammar-check", HttpMethod.POST, new Gson().toJson(content));
    LOGGER.debug("<-- grammarCheck()");
    return response;
  }
}

