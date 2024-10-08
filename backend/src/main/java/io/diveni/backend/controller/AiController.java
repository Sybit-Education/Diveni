package io.diveni.backend.controller;

import io.diveni.backend.dto.AiServiceResponse;
import io.diveni.backend.dto.GptConfidentialData;
import io.diveni.backend.service.ai.AiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
public class AiController {
  private static final Logger LOGGER = LoggerFactory.getLogger(AiController.class);

  @Autowired AiService aiService;

  @PostMapping("/improve-title")
  public ResponseEntity<String> improveTitle(@RequestBody GptConfidentialData data) {
    LOGGER.debug("--> improveTitle(), userstory={}", data);
    ResponseEntity<String> response = aiService.improveTitle(data);
    LOGGER.debug("<-- improveTitle()");
    return response;
  }

  @PostMapping("/improve-description")
  public ResponseEntity<String> improveDescription(@RequestBody GptConfidentialData data) {
    LOGGER.debug("--> improveDescription(), userStory={}", data);
    ResponseEntity<String> response = aiService.improveDescription(data);
    LOGGER.debug("<-- improveDescription()");
    return response;
  }

  @PostMapping("/grammar-check")
  public ResponseEntity<String> grammarCheck(@RequestBody GptConfidentialData data) {
    LOGGER.debug("--> grammarCheck(), userStory={}", data);
    ResponseEntity<String> response = aiService.grammarCheck(data);
    LOGGER.debug("<-- grammarCheck()");
    return response;
  }

  @PostMapping("estimate-user-story")
  public ResponseEntity<String> estimateUserStory(@RequestBody GptConfidentialData data) {
    LOGGER.debug("--> estimateUserStory(), GptConfidentialData={}", data);
    ResponseEntity<String> response = aiService.estimateUserStory(data);
    LOGGER.debug("<-- estimateUserStory()");
    return response;
  }

  @PostMapping("split-user-story")
  public ResponseEntity<String> splitUserStory(@RequestBody GptConfidentialData data) {
    LOGGER.debug("--> splitUserStory(), GptConfidentialData={}", data);
    ResponseEntity<String> response = aiService.splitUserStory(data);
    LOGGER.debug("<-- splitUserStory()");
    return response;
  }

  @PostMapping("mark-description")
  public ResponseEntity<String> markDescription(@RequestBody GptConfidentialData data) {
    LOGGER.debug("--> markDescription(), GptConfidentialData={}", data);
    ResponseEntity<String> response = aiService.markDescription(data);
    LOGGER.debug("<-- markDescription()");
    return response;
  }

  @GetMapping("check-api-key")
  public ResponseEntity<AiServiceResponse> ensureServiceAndApiKey() {
    LOGGER.debug("--> checkApiKey()");
    ResponseEntity<AiServiceResponse> response = aiService.ensureServiceAndApiKey();
    LOGGER.debug("<-- checkApiKey()");
    return response;
  }
}
