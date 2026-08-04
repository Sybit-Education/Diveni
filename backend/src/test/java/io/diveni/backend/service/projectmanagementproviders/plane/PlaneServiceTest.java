package io.diveni.backend.service.projectmanagementproviders.plane;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import io.diveni.backend.model.Project;
import io.diveni.backend.model.TokenIdentifier;
import io.diveni.backend.model.UserStory;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class PlaneServiceTest {

  @Spy private PlaneService planeService = new PlaneService();

  @BeforeEach
  void configureService() {
    ReflectionTestUtils.setField(planeService, "planeBaseUrl", "https://plane.example.com/");
    ReflectionTestUtils.setField(planeService, "workspaceSlug", "engineering");
    ReflectionTestUtils.setField(planeService, "apiKey", "plane_api_test");
    ReflectionTestUtils.setField(planeService, "estimateValuesConfig", "0,1,2,3,5,8,13,21");
    ReflectionTestUtils.setField(planeService, "allowDelete", false);
    planeService.logConfig();
  }

  @Test
  void connectsAndLoadsProjectsAndOpenWorkItems() {
    ResponseEntity<String> userResponse =
        ResponseEntity.ok("{\"display_name\":\"Plane Test User\"}");
    ResponseEntity<String> projectResponse =
        ResponseEntity.ok(
            "{\"next_page_results\":false,\"results\":[{\"id\":\"project-1\",\"name\":\"Engineering\"}]}");
    ResponseEntity<String> workItemResponse =
        ResponseEntity.ok(
            "{\"next_page_results\":false,\"results\":["
                + "{\"id\":\"work-1\",\"name\":\"Add Plane\",\"description_stripped\":\"Build connector\",\"estimate_point\":4,\"archived_at\":null,\"completed_at\":null},"
                + "{\"id\":\"work-2\",\"name\":\"Completed\",\"description_stripped\":\"Done\",\"estimate_point\":2,\"completed_at\":\"2026-08-01T00:00:00Z\"}"
                + "]}");

    doReturn(userResponse, projectResponse, workItemResponse)
        .when(planeService)
        .executeRequest(anyString(), any(HttpMethod.class), any());

    TokenIdentifier token = planeService.connect();
    List<Project> projects = planeService.getProjects(token.getTokenId());
    List<UserStory> stories = planeService.getIssues(token.getTokenId(), "Engineering");

    assertTrue(planeService.containsToken(token.getTokenId()));
    assertEquals("Plane Test User", planeService.getCurrentUsername(token.getTokenId()));
    assertEquals(1, projects.size());
    assertEquals("project-1", projects.get(0).getId());
    assertEquals(1, stories.size());
    assertEquals("work-1", stories.get(0).getId());
    assertEquals("5", stories.get(0).getEstimation());
    assertEquals("Build connector", stories.get(0).getDescription());
  }

  @Test
  @SuppressWarnings("unchecked")
  void writesDiveniEstimateAsPlaneEstimateSlot() {
    ResponseEntity<String> userResponse = ResponseEntity.ok("{\"email\":\"test@example.com\"}");
    ResponseEntity<String> projectResponse =
        ResponseEntity.ok("[{\"id\":\"project-1\",\"name\":\"Engineering\"}]");
    ResponseEntity<String> workItemResponse = ResponseEntity.ok("[]");
    ResponseEntity<String> updateResponse = ResponseEntity.ok("{}");

    doReturn(userResponse, projectResponse, workItemResponse, updateResponse)
        .when(planeService)
        .executeRequest(anyString(), any(HttpMethod.class), any());

    TokenIdentifier token = planeService.connect();
    planeService.getProjects(token.getTokenId());
    planeService.getIssues(token.getTokenId(), "Engineering");
    planeService.updateIssue(
        token.getTokenId(),
        new UserStory("work-1", "Updated", "Line one\nLine two", "13", true, null));

    ArgumentCaptor<Object> bodyCaptor = ArgumentCaptor.forClass(Object.class);
    verify(planeService)
        .executeRequest(anyString(), eq(HttpMethod.PATCH), bodyCaptor.capture());

    Map<String, Object> body = (Map<String, Object>) bodyCaptor.getValue();
    assertEquals(6, body.get("estimate_point"));
    assertEquals("<p>Line one<br>Line two</p>", body.get("description_html"));
  }

  @Test
  void connectorIsDisabledWithAnInvalidEstimateMapping() {
    ReflectionTestUtils.setField(planeService, "estimateValuesConfig", "1,2,3");
    planeService.logConfig();

    assertFalse(planeService.serviceEnabled());
  }
}
