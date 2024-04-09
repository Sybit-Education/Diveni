package io.diveni.backend.controller;

import io.diveni.backend.dto.GptConfidentalData;
import io.diveni.backend.model.UserStory;
import io.diveni.backend.service.ai.AiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class AiController {
  private static final Logger LOGGER = LoggerFactory.getLogger(AiController.class);

  @Autowired AiService aiService;

  @PostMapping("/improve-title")
  public ResponseEntity<String> improveTitle(@RequestBody UserStory userStory) {
    LOGGER.debug("--> improveTitle(), userstory={}", userStory);
    ResponseEntity<String> response = aiService.improveTitle(userStory);
    LOGGER.debug("<-- improveTitle()");
    return response;
  }

  @PostMapping("/improve-description")
  public ResponseEntity<String> improveDescription(@RequestBody UserStory userStory) {
    LOGGER.debug("--> improveDescription(), userStory={}", userStory);
    ResponseEntity<String> response = aiService.improveDescription(userStory);
    LOGGER.debug("<-- improveDescription()");
    return response;
  }

  @PostMapping("/grammar-check")
  public ResponseEntity<String> grammarCheck(@RequestBody GptConfidentalData data) {
    LOGGER.debug("--> grammarCheck(), userStory={}", data);
    ResponseEntity<String> response = aiService.grammarCheck(new UserStory(data.getId(), data.getTitle(), data.getDescription(), data.getEstimation(), data.getIsActive()), data.getConfidentalData());
    LOGGER.debug("<-- grammarCheck()");
    return response;
  }
}
