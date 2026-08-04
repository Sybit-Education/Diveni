/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 Diveni Team, AUME-Team 21/22, HTWG Konstanz
*/
package io.diveni.backend.service.projectmanagementproviders.plane;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.diveni.backend.Utils;
import io.diveni.backend.controller.ErrorMessages;
import io.diveni.backend.model.Project;
import io.diveni.backend.model.TokenIdentifier;
import io.diveni.backend.model.UserStory;
import io.diveni.backend.service.projectmanagementproviders.ProjectManagementProvider;
import jakarta.annotation.PostConstruct;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PlaneService implements ProjectManagementProvider {

  private static final Logger LOGGER = LoggerFactory.getLogger(PlaneService.class);

  @Value("${PLANE_BASE_URL:}")
  private String planeBaseUrl;

  @Value("${PLANE_WORKSPACE_SLUG:}")
  private String workspaceSlug;

  @Value("${PLANE_API_KEY:}")
  private String apiKey;

  @Value("${PLANE_ALLOW_DELETE:false}")
  private boolean allowDelete;

  private boolean serviceEnabled;

  private final RestTemplate restTemplate =
      new RestTemplate(
          new JdkClientHttpRequestFactory(
              HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build()));

  private final Set<String> tokenIdentifiers = ConcurrentHashMap.newKeySet();
  private final Map<String, String> userNames = new ConcurrentHashMap<>();
  private final Map<String, Map<String, String>> projectIds = new ConcurrentHashMap<>();
  private final Map<String, String> projectEstimateIds = new ConcurrentHashMap<>();
  private final Map<String, Map<String, String>> estimatePointIdsByValue =
      new ConcurrentHashMap<>();
  private final Map<String, Map<String, Integer>> estimatePointKeysByValue =
      new ConcurrentHashMap<>();
  private final Map<String, Map<String, String>> estimateValuesByPointId =
      new ConcurrentHashMap<>();
  private final Map<String, Map<Integer, String>> estimateValuesByPointKey =
      new ConcurrentHashMap<>();
  private final Map<String, String> selectedProjectIds = new ConcurrentHashMap<>();

  @PostConstruct
  public void logConfig() {
    planeBaseUrl = stripTrailingSlash(planeBaseUrl);
    serviceEnabled =
        !planeBaseUrl.isBlank() && !workspaceSlug.isBlank() && !apiKey.isBlank();
    LOGGER.info("Plane Service: (enabled:{})", serviceEnabled);
  }

  @Override
  public boolean serviceEnabled() {
    return serviceEnabled;
  }

  public TokenIdentifier connect() {
    ensureEnabled();
    try {
      JsonNode user =
          new ObjectMapper()
              .readTree(executeRequest(apiUrl("/users/me/"), HttpMethod.GET, null).getBody());
      String userName = firstNonBlank(user.path("display_name"), user.path("email"));
      String tokenIdentifier = Utils.generateRandomID();
      tokenIdentifiers.add(tokenIdentifier);
      userNames.put(tokenIdentifier, userName);
      return new TokenIdentifier(tokenIdentifier);
    } catch (Exception exception) {
      LOGGER.warn("Failed to connect to Plane", exception);
      throw new ResponseStatusException(
          HttpStatus.BAD_GATEWAY, "Failed to authenticate with the configured Plane instance");
    }
  }

  public ResponseEntity<String> executeRequest(String url, HttpMethod method, Object body) {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(List.of(MediaType.APPLICATION_JSON));
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.add("X-API-Key", apiKey);
    return restTemplate.exchange(url, method, new HttpEntity<>(body, headers), String.class);
  }

  @Override
  public List<Project> getProjects(String tokenIdentifier) {
    requireToken(tokenIdentifier);
    try {
      List<Project> projects = new ArrayList<>();
      Map<String, String> projectsByName = new HashMap<>();
      for (JsonNode node : getPaginated(apiUrl(workspacePath() + "/projects/"))) {
        String name = node.path("name").asText();
        String id = node.path("id").asText();
        if (!name.isBlank() && !id.isBlank()) {
          projects.add(new Project(name, id));
          projectsByName.put(name, id);

          if (node.hasNonNull("estimate")) {
            projectEstimateIds.put(id, node.path("estimate").asText());
          } else {
            projectEstimateIds.remove(id);
            clearEstimatePointMappings(id);
          }
        }
      }
      projectIds.put(tokenIdentifier, projectsByName);
      return projects;
    } catch (Exception exception) {
      LOGGER.warn("Failed to retrieve Plane projects", exception);
      throw new ResponseStatusException(
          HttpStatus.BAD_GATEWAY, ErrorMessages.failedToRetrieveProjectsErrorMessage);
    }
  }

  @Override
  public List<UserStory> getIssues(String tokenIdentifier, String projectName) {
    requireToken(tokenIdentifier);
    String projectId = resolveProjectId(tokenIdentifier, projectName);
    selectedProjectIds.put(tokenIdentifier, projectId);

    try {
      loadEstimatePoints(projectId);

      List<UserStory> stories = new ArrayList<>();
      String path = workspacePath() + "/projects/" + projectId + "/work-items/";
      for (JsonNode node : getPaginated(apiUrl(path))) {
        if (node.hasNonNull("archived_at") || node.hasNonNull("completed_at")) {
          continue;
        }
        String description = node.path("description_stripped").asText("");
        if (description.isBlank()) {
          description = stripHtml(node.path("description_html").asText(""));
        }
        stories.add(
            new UserStory(
                node.path("id").asText(),
                node.path("name").asText(),
                description,
                fromPlaneEstimate(
                    projectId, node.path("estimate_point"), node.path("point")),
                true,
                null));
      }
      return stories;
    } catch (Exception exception) {
      LOGGER.warn("Failed to retrieve Plane work items", exception);
      throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "failed to retrieve issues");
    }
  }

  @Override
  public void updateIssue(String tokenIdentifier, UserStory story) {
    requireToken(tokenIdentifier);
    String projectId = requireSelectedProject(tokenIdentifier);
    try {
      Map<String, Object> content = storyPayload(projectId, story);
      executeRequest(
          apiUrl(
              workspacePath()
                  + "/projects/"
                  + projectId
                  + "/work-items/"
                  + story.getId()
                  + "/"),
          HttpMethod.PATCH,
          content);
      LOGGER.info(
          "Updated Plane work item {} with estimate {} (estimate point {}, slot {})",
          story.getId(),
          story.getEstimation(),
          content.get("estimate_point"),
          content.get("point"));
    } catch (ResponseStatusException exception) {
      throw exception;
    } catch (Exception exception) {
      LOGGER.warn("Failed to update Plane work item {}", story.getId(), exception);
      throw new ResponseStatusException(
          HttpStatus.BAD_GATEWAY, ErrorMessages.failedToEditIssueErrorMessage);
    }
  }

  @Override
  public String createIssue(String tokenIdentifier, String projectId, UserStory story) {
    requireToken(tokenIdentifier);
    selectedProjectIds.put(tokenIdentifier, projectId);
    try {
      ResponseEntity<String> response =
          executeRequest(
              apiUrl(workspacePath() + "/projects/" + projectId + "/work-items/"),
              HttpMethod.POST,
              storyPayload(projectId, story));
      return new ObjectMapper().readTree(response.getBody()).path("id").asText();
    } catch (ResponseStatusException exception) {
      throw exception;
    } catch (Exception exception) {
      LOGGER.warn("Failed to create Plane work item", exception);
      throw new ResponseStatusException(
          HttpStatus.BAD_GATEWAY, ErrorMessages.failedToEditIssueErrorMessage);
    }
  }

  @Override
  public void deleteIssue(String tokenIdentifier, String issueId) {
    requireToken(tokenIdentifier);
    if (!allowDelete) {
      throw new ResponseStatusException(
          HttpStatus.METHOD_NOT_ALLOWED,
          "Deleting Plane work items is disabled. Set PLANE_ALLOW_DELETE=true to enable it.");
    }

    String projectId = requireSelectedProject(tokenIdentifier);
    try {
      executeRequest(
          apiUrl(workspacePath() + "/projects/" + projectId + "/work-items/" + issueId + "/"),
          HttpMethod.DELETE,
          null);
    } catch (Exception exception) {
      LOGGER.warn("Failed to delete Plane work item {}", issueId, exception);
      throw new ResponseStatusException(
          HttpStatus.BAD_GATEWAY, ErrorMessages.failedToDeleteIssueErrorMessage);
    }
  }

  @Override
  public boolean containsToken(String token) {
    return tokenIdentifiers.contains(token);
  }

  @Override
  public String getCurrentUsername(String tokenIdentifier) {
    return userNames.get(tokenIdentifier);
  }

  private void loadEstimatePoints(String projectId) throws Exception {
    String estimateId = projectEstimateIds.get(projectId);
    if (estimateId == null || estimateId.isBlank()) {
      clearEstimatePointMappings(projectId);
      return;
    }

    String path =
        workspacePath()
            + "/projects/"
            + projectId
            + "/estimates/"
            + estimateId
            + "/estimate-points/";

    Map<String, String> pointIdsByValue = new HashMap<>();
    Map<String, Integer> pointKeysByValue = new HashMap<>();
    Map<String, String> valuesByPointId = new HashMap<>();
    Map<Integer, String> valuesByPointKey = new HashMap<>();

    for (JsonNode point : getPaginated(apiUrl(path))) {
      String id = point.path("id").asText();
      String value = point.path("value").asText();
      JsonNode keyNode = point.path("key");
      if (!id.isBlank() && !value.isBlank() && keyNode.canConvertToInt()) {
        int key = keyNode.asInt();
        pointIdsByValue.put(value, id);
        pointKeysByValue.put(value, key);
        valuesByPointId.put(id, value);
        valuesByPointKey.put(key, value);
      }
    }

    estimatePointIdsByValue.put(projectId, pointIdsByValue);
    estimatePointKeysByValue.put(projectId, pointKeysByValue);
    estimateValuesByPointId.put(projectId, valuesByPointId);
    estimateValuesByPointKey.put(projectId, valuesByPointKey);
    LOGGER.info(
        "Loaded {} Plane estimate points for project {}", pointIdsByValue.size(), projectId);
  }

  private void clearEstimatePointMappings(String projectId) {
    estimatePointIdsByValue.remove(projectId);
    estimatePointKeysByValue.remove(projectId);
    estimateValuesByPointId.remove(projectId);
    estimateValuesByPointKey.remove(projectId);
  }

  private void ensureEstimatePointsLoaded(String projectId) throws Exception {
    if (!estimatePointIdsByValue.containsKey(projectId)
        || !estimatePointKeysByValue.containsKey(projectId)) {
      loadEstimatePoints(projectId);
    }
  }

  private List<JsonNode> getPaginated(String baseUrl) throws Exception {
    List<JsonNode> results = new ArrayList<>();
    String cursor = null;

    do {
      String url = baseUrl + "?per_page=100";
      if (cursor != null) {
        url += "&cursor=" + URLEncoder.encode(cursor, StandardCharsets.UTF_8);
      }
      JsonNode root =
          new ObjectMapper().readTree(executeRequest(url, HttpMethod.GET, null).getBody());

      JsonNode page = root.isArray() ? root : root.path("results");
      if (page.isArray()) {
        page.forEach(results::add);
      }

      if (root.isArray() || !root.path("next_page_results").asBoolean(false)) {
        cursor = null;
      } else {
        cursor = root.path("next_cursor").asText(null);
      }
    } while (cursor != null && !cursor.isBlank());

    return results;
  }

  private Map<String, Object> storyPayload(String projectId, UserStory story) throws Exception {
    Map<String, Object> content = new HashMap<>();
    content.put("name", story.getTitle());
    content.put("description_html", toHtml(story.getDescription()));
    if (story.getEstimation() != null && !story.getEstimation().isBlank()) {
      ensureEstimatePointsLoaded(projectId);
      PlaneEstimatePoint estimatePoint =
          resolvePlaneEstimatePoint(projectId, story.getEstimation());
      content.put("estimate_point", estimatePoint.id());
      content.put("point", estimatePoint.key() + 1);
    }
    return content;
  }

  private String fromPlaneEstimate(
      String projectId, JsonNode estimatePoint, JsonNode pointSlot) {
    if (estimatePoint != null && !estimatePoint.isNull()) {
      String estimatePointId =
          estimatePoint.isObject() ? estimatePoint.path("id").asText() : estimatePoint.asText();
      if (!estimatePointId.isBlank()) {
        String value =
            estimateValuesByPointId.getOrDefault(projectId, Map.of()).get(estimatePointId);
        if (value != null) {
          return value;
        }
      }
    }

    if (pointSlot != null && !pointSlot.isNull() && pointSlot.canConvertToInt()) {
      return estimateValuesByPointKey
          .getOrDefault(projectId, Map.of())
          .get(pointSlot.asInt() - 1);
    }

    return null;
  }

  private PlaneEstimatePoint resolvePlaneEstimatePoint(String projectId, String estimation) {
    String value = estimation.trim();
    Map<String, String> pointIdsByValue =
        estimatePointIdsByValue.getOrDefault(projectId, Map.of());
    Map<String, Integer> pointKeysByValue =
        estimatePointKeysByValue.getOrDefault(projectId, Map.of());
    String estimatePointId = pointIdsByValue.get(value);
    Integer estimatePointKey = pointKeysByValue.get(value);

    if (estimatePointId == null || estimatePointKey == null) {
      if (!projectEstimateIds.containsKey(projectId)) {
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "The selected Plane project does not have an active estimate system");
      }
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          "Estimate '"
              + estimation
              + "' is not configured in the selected Plane project. Available values: "
              + pointIdsByValue.keySet());
    }

    return new PlaneEstimatePoint(estimatePointId, estimatePointKey);
  }

  private String resolveProjectId(String tokenIdentifier, String projectName) {
    String projectId = projectIds.getOrDefault(tokenIdentifier, Map.of()).get(projectName);
    if (projectId == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Unknown Plane project: " + projectName);
    }
    return projectId;
  }

  private String requireSelectedProject(String tokenIdentifier) {
    String projectId = selectedProjectIds.get(tokenIdentifier);
    if (projectId == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Plane project is selected");
    }
    return projectId;
  }

  private void requireToken(String tokenIdentifier) {
    ensureEnabled();
    if (!containsToken(tokenIdentifier)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Plane token identifier");
    }
  }

  private void ensureEnabled() {
    if (!serviceEnabled) {
      throw new ResponseStatusException(
          HttpStatus.SERVICE_UNAVAILABLE, "Plane connector is not configured");
    }
  }

  private String apiUrl(String path) {
    return planeBaseUrl + "/api/v1" + path;
  }

  private String workspacePath() {
    return "/workspaces/" + workspaceSlug;
  }

  private static String stripTrailingSlash(String value) {
    if (value == null) {
      return "";
    }
    return value.replaceAll("/+$", "");
  }

  private static String firstNonBlank(JsonNode first, JsonNode second) {
    String firstValue = first.asText("");
    return firstValue.isBlank() ? second.asText("Plane user") : firstValue;
  }

  private static String stripHtml(String value) {
    return value.replaceAll("<[^>]*>", "").replace("&nbsp;", " ").trim();
  }

  private static String toHtml(String value) {
    if (value == null || value.isBlank()) {
      return "<p></p>";
    }
    String escaped =
        value
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;");
    return "<p>" + escaped.replace("\n", "<br>") + "</p>";
  }

  private record PlaneEstimatePoint(String id, int key) {}
}
