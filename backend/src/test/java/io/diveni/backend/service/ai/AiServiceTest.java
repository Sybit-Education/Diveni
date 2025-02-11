package io.diveni.backend.service.ai;

import io.diveni.backend.dto.GptConfidentialData;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    Map<String, String> content = new HashMap<>();
    content.put("test-company", "company");
    JSONObject testObject = new JSONObject(content);
    GptConfidentialData data =
        new GptConfidentialData(
            "1", "TEST", "TEST", "3", true, testObject, "english", List.of("1", "2", "3", "4"));

    when(aiService.improveDescription(data)).thenReturn(mockedResponse);

    ResponseEntity<String> returnResponse = aiService.improveDescription(data);

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

    Map<String, String> content = new HashMap<>();
    content.put("test-company", "company");
    JSONObject testObject = new JSONObject(content);
    GptConfidentialData data =
        new GptConfidentialData(
            "1", "TEST", "TEST", "3", true, testObject, "english", List.of("1", "2", "3", "4"));

    when(aiService.improveDescription(data)).thenReturn(mockedResponse);

    ResponseEntity<String> returnResponse = aiService.improveDescription(data);

    assertEquals(
        "{'improved_description' : 'test', 'improved_acceptance_criteria' : '* TEST \n"
            + ", * TEST 2 \n"
            + "'}",
        returnResponse.getBody());
  }
}
