package io.diveni.backend.controller;

import com.google.gson.Gson;
import io.diveni.backend.model.UserStory;
import io.diveni.backend.service.ai.AiService;
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

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AiControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private AiService service;

  private final String improvedDescription =
      "As a User i want a homepage. \n ##### Acceptance Criteria: \n";

  private final String improvedAcceptanceCriteria = "* pictures \n, * navigation bar\n";

  @BeforeEach
  public void setUp() {
    UserStory userStory = new UserStory("1", "TEST", "TEST", "3", true);

    Mockito.when(service.improveTitle(userStory))
        .thenReturn(new ResponseEntity<>("'improvedTitle': 'TEST'", HttpStatus.OK));
    Mockito.when(service.improveDescription(userStory))
        .thenReturn(
            new ResponseEntity<>(
                "{ 'improved_description': '"
                    + improvedDescription
                    + "', 'improved_acceptance_criteria': '"
                    + improvedAcceptanceCriteria
                    + "'}",
                HttpStatus.OK));
  }

  @Test
  public void getTitle_valid_statusOKandContainsTitle() throws Exception {
    UserStory userStory = new UserStory("1", "TEST", "TEST", "3", true);

    this.mockMvc
        .perform(
            post("/ai/improve-title")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(userStory)))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("improvedTitle")));
  }

  @Test
  public void getDescription_valid_statusOKandContainsDescriptionAndAcceptanceCriteria()
      throws Exception {
    UserStory userStory = new UserStory("1", "TEST", "TEST", "3", true);

    this.mockMvc
        .perform(
            post("/ai/improve-description")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(userStory)))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("'improved_description'")))
        .andExpect(content().string(containsString("'improved_acceptance_criteria'")));
  }
}
