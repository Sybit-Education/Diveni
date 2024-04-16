package io.diveni.backend.controller;

import com.google.gson.Gson;
import io.diveni.backend.dto.GptConfidentialData;
import io.diveni.backend.service.ai.AiService;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AiControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private AiService service;

  @BeforeEach
  public void setUp() {
    // improve Title
    Mockito.when(service.improveTitle(Mockito.any(GptConfidentialData.class)))
        .thenReturn(new ResponseEntity<>("'improvedTitle': 'TEST'", HttpStatus.OK));
    // improve Description
    String improvedAcceptanceCriteria = "* pictures \n, * navigation bar\n";
    String improvedDescription = "As a User i want a homepage. \n ##### Acceptance Criteria: \n";
    Mockito.when(service.improveDescription(Mockito.any(GptConfidentialData.class)))
        .thenReturn(
            new ResponseEntity<>(
                "{ 'improved_description': '"
                    + improvedDescription
                    + "', 'improved_acceptance_criteria': '"
                    + improvedAcceptanceCriteria
                    + "'}",
                HttpStatus.OK));
    // grammar check
    String correctedDescription =
        "As a backend developer, I want to establish websocket communication between the server and"
            + " client, so that real-time data can be transmitted and received.";
    Mockito.when(service.grammarCheck(Mockito.any(GptConfidentialData.class)))
        .thenReturn(
            new ResponseEntity<>(
                "{ 'improved_description': '" + correctedDescription + "'}", HttpStatus.OK));
  }

  @Test
  public void getTitle_valid_statusOKAndContainsTitle() throws Exception {
    Map<String, String> content = new HashMap<>();
    content.put("test-company", "company");
    JSONObject testObject = new JSONObject(content);
    GptConfidentialData data = new GptConfidentialData("1", "TEST", "TEST", "3", true, testObject, "english");
    this.mockMvc
        .perform(
            post("/ai/improve-title")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(data)))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("improvedTitle")));
  }

  @Test
  public void getDescription_valid_statusOKAndContainsDescriptionAndAcceptanceCriteria()
      throws Exception {
    Map<String, String> content = new HashMap<>();
    content.put("test-company", "company");
    JSONObject testObject = new JSONObject(content);
    GptConfidentialData data = new GptConfidentialData("1", "TEST", "TEST", "3", true, testObject, "english");

    this.mockMvc
        .perform(
            post("/ai/improve-description")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(data)))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("'improved_description'")))
        .andExpect(content().string(containsString("'improved_acceptance_criteria'")));
  }

  @Test
  public void grammarCheck_valid_statusOKAndContainsDescription() throws Exception {
    Map<String, String> content = new HashMap<>();
    content.put("test-company", "company");
    JSONObject testObject = new JSONObject(content);
    GptConfidentialData data = new GptConfidentialData("1", "TEST", "TEST", "3", true, testObject, "english");

    this.mockMvc
        .perform(
            post("/ai/grammar-check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(data)))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("'improved_description'")));
  }
}
