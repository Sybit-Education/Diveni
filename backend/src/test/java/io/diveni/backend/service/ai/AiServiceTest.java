package io.diveni.backend.service.ai;

import io.diveni.backend.model.UserStory;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class AiServiceTest {

  @InjectMocks private AiService aiService = new AiService();

  @Test
  public void improveTitle() {
    aiService = Mockito.mock(AiService.class);
    String jsonReturnValue = "{'improvedTitle' : 'test'}";
    HttpHeaders mockedHeaders = new HttpHeaders();
    mockedHeaders.setContentType(MediaType.APPLICATION_JSON);

    ResponseEntity<String> mockedResponse =
        new ResponseEntity<>(jsonReturnValue, mockedHeaders, HttpStatusCode.valueOf(200));

    when(aiService.improveDescription(new UserStory("1", "TEST", "TEST", "3", true) , List.of("test")))
        .thenReturn(mockedResponse);

    ResponseEntity<String> returnResponse =
        aiService.improveDescription(new UserStory("1", "TEST", "TEST", "3", true) , List.of("test"));

    assertEquals("{'improvedTitle' : 'test'}", returnResponse.getBody());
  }

  @Test
  public void improveDescription() {
    aiService = Mockito.mock(AiService.class);
    String jsonReturnValue =
        "{'improved_description' : 'test', 'improved_acceptance_criteria' : '* TEST \n"
            + ", * TEST 2 \n"
            + "'}";
    HttpHeaders mockedHeaders = new HttpHeaders();
    mockedHeaders.setContentType(MediaType.APPLICATION_JSON);

    ResponseEntity<String> mockedResponse =
        new ResponseEntity<>(jsonReturnValue, mockedHeaders, HttpStatusCode.valueOf(200));

    when(aiService.improveDescription(new UserStory("1", "TEST", "TEST", "3", true), List.of("test")))
        .thenReturn(mockedResponse);

    ResponseEntity<String> returnResponse =
        aiService.improveDescription(new UserStory("1", "TEST", "TEST", "3", true), List.of("test"));

    assertEquals(
        "{'improved_description' : 'test', 'improved_acceptance_criteria' : '* TEST \n"
            + ", * TEST 2 \n"
            + "'}",
        returnResponse.getBody());
  }
}
