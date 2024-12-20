package io.diveni.backend.service.projectmanagementproviders.azuredevops;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import io.diveni.backend.Utils;
import io.diveni.backend.controller.ErrorMessages;
import io.diveni.backend.model.Project;
import io.diveni.backend.model.TokenIdentifier;
import io.diveni.backend.model.UserStory;
import io.diveni.backend.service.projectmanagementproviders.ProjectManagementProviderOAuth2;
import lombok.Getter;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Base64;

@Service
public class AzureDevOpsService implements ProjectManagementProviderOAuth2 {

  private static final Logger LOGGER = LoggerFactory.getLogger(AzureDevOpsService.class);
  private static final String AZURE_DEVOPS_API = "https://dev.azure.com/%s/_apis/";
  @Getter private final Map<String, String> accessTokens = new HashMap<>();
  private final Map<String, String> accessTokenToProjectId = new HashMap<>();
  private boolean serviceEnabled = false;

  private static final String API_FIELD_TITLE = "System.Title";
  private static final String API_FIELD_DESCRIPTION = "System.Description";
  private static final String API_FIELD_ESTIMATION = "Microsoft.VSTS.Scheduling.StoryPoints";

  @Value("${AZURE_ORGANIZATION:#{null}}")
  private String ORGANIZATION;

  @Value("${AZURE_CLIENTPAT:#{null}}")
  private String CLIENT_PAT;

  @PostConstruct
  public void logConfig() {
    if (ORGANIZATION != null && CLIENT_PAT != null) {
      serviceEnabled = true;
    }

    LOGGER.info("Azure-DevOps Service: (enabled:" + serviceEnabled + ")");

    LOGGER.info("    AZURE_ORGANIZATION={}", ORGANIZATION);
    LOGGER.info("    AZURE_PERSONAL_ACCESS_TOKEN={}", CLIENT_PAT == null ? "null" : "********");
  }

  @Override
  public boolean serviceEnabled() {
    return serviceEnabled;
  }

  public ResponseEntity<String> executeRequest(
      String url, HttpMethod method, String accessToken, Object body) {
    return executeRequest(url, method, accessToken, body, MediaType.APPLICATION_JSON);
  }

  public ResponseEntity<String> executeRequest(
      String url, HttpMethod method, String accessToken, Object body, MediaType contentType) {
    LOGGER.debug("--> executeRequest()");
    RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(List.of(MediaType.APPLICATION_JSON));
    headers.setContentType(contentType);
    String token = ":" + accessToken;
    headers.add(
        "Authorization", "Basic " + new String(Base64.getEncoder().encode(token.getBytes())));
    HttpEntity<Object> request = new HttpEntity<>(body, headers);
    LOGGER.debug("<-- executeRequest()");
    return restTemplate.exchange(url, method, request, String.class);
  }

  @Override
  public List<Project> getProjects(String tokenIdentifier) {
    LOGGER.debug("--> getProjects()");
    try {
      List<Project> projects = new ArrayList<>();
      ResponseEntity<String> response =
          executeRequest(
              String.format(AZURE_DEVOPS_API, ORGANIZATION) + "projects?api-version=7.0",
              HttpMethod.GET,
              accessTokens.get(tokenIdentifier),
              null);
      JsonNode node = new ObjectMapper().readTree(response.getBody());

      for (JsonNode projectNode : node.path("value")) {
        projects.add(new Project(projectNode.get("name").asText(), projectNode.get("id").asText()));
      }
      LOGGER.debug("<-- getProjects()");
      return projects;
    } catch (Exception e) {
      LOGGER.error("Failed to get projects!", e);
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.failedToRetrieveProjectsErrorMessage);
    }
  }

  @Override
  public List<UserStory> getIssues(String tokenIdentifier, String projectName) {
    LOGGER.debug("--> getIssues(), projectName={}", projectName);
    Map<String, String> content = new HashMap<>();
    String query =
        "SELECT [System.Id] FROM WorkItems WHERE [System.TeamProject] = @project AND"
            + " [System.WorkItemType] = 'User Story' AND [System.State] <> 'Closed' ORDER BY"
            + " [System.CreatedDate] ASC";
    content.put("query", query);
    ResponseEntity<String> responseList =
        executeRequest(
            String.format(AZURE_DEVOPS_API, ORGANIZATION + "/" + projectName)
                + "wit/wiql?api-version=5.1",
            HttpMethod.POST,
            accessTokens.get(tokenIdentifier),
            content);

    try {
      JsonNode node = new ObjectMapper().readTree(responseList.getBody());
      List<String> workItemIds = new ArrayList<>();
      for (JsonNode issue : node.path("workItems")) {
        workItemIds.add(issue.get("id").asText());
      }
      List<String> fieldList = new ArrayList<>();
      Collections.addAll(fieldList, API_FIELD_TITLE, API_FIELD_DESCRIPTION, API_FIELD_ESTIMATION);
      Map<String, List<String>> contentDetailed = new HashMap<>();

      contentDetailed.put("ids", workItemIds);
      contentDetailed.put("fields", fieldList);
      ResponseEntity<String> responseDetailedList =
          executeRequest(
              String.format(AZURE_DEVOPS_API, ORGANIZATION + "/" + projectName)
                  + "wit/workitemsbatch?api-version=5.1",
              HttpMethod.POST,
              accessTokens.get(tokenIdentifier),
              contentDetailed);
      JsonNode detailedNode = new ObjectMapper().readTree(responseDetailedList.getBody());
      List<UserStory> userStories = new ArrayList<>();
      for (JsonNode detailedIssue : detailedNode.path("value")) {
        JsonNode fields = detailedIssue.get("fields");

        String estimation =
            fields.hasNonNull(API_FIELD_ESTIMATION)
                ? String.valueOf(fields.get(API_FIELD_ESTIMATION).asDouble())
                : null;
        if (estimation != null && estimation.endsWith(".0")) {
          estimation = estimation.substring(0, estimation.length() - 2);
        }
        String description =
            fields.hasNonNull(API_FIELD_DESCRIPTION)
                ? fields.get(API_FIELD_DESCRIPTION).textValue().replaceAll("\\<[^>]*>", "")
                : "";
        description =
            description.length() > 0
                ? description.substring(0, description.length() - 1)
                : description;
        userStories.add(
            new UserStory(
                detailedIssue.get("id").asText(),
                fields.get(API_FIELD_TITLE).textValue(),
                description,
                estimation,
                false));
      }
      accessTokenToProjectId.put(tokenIdentifier, projectName);
      LOGGER.debug("<-- getIssues()");
      return userStories;
    } catch (Exception e) {
      LOGGER.error("Failed to get projects!", e);
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.failedToRetrieveProjectsErrorMessage);
    }
  }

  @Override
  public void updateIssue(String tokenIdentifier, UserStory story) {
    LOGGER.debug("--> updateIssue(), storyID={}", story.getId());
    List<Map<String, Object>> content = new ArrayList<>();
    String fieldPrefix = "/fields/";

    Map<String, Object> updateTitle = new HashMap<>();
    updateTitle.put("op", "replace");
    updateTitle.put("path", fieldPrefix + API_FIELD_TITLE);
    updateTitle.put("value", story.getTitle());
    content.add(updateTitle);

    Map<String, Object> updateDescription = new HashMap<>();
    updateDescription.put("op", "replace");
    updateDescription.put("path", fieldPrefix + API_FIELD_DESCRIPTION);
    updateDescription.put("value", "<div>" + story.getDescription() + " </div>");
    content.add(updateDescription);

    if (story.getEstimation() != null) {
      try {
        Map<String, Object> updateEstimation = new HashMap<>();
        updateEstimation.put("op", "replace");
        updateEstimation.put("path", fieldPrefix + API_FIELD_ESTIMATION);
        updateEstimation.put("value", Double.parseDouble(story.getEstimation()));
        content.add(updateEstimation);
      } catch (NumberFormatException e) {
        LOGGER.error("Failed to parse estimation into double!");
        throw new ResponseStatusException(
            HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.failedToEditIssueErrorMessage);
      }
    }
    try {
      ResponseEntity<String> response =
          executeRequest(
              String.format(AZURE_DEVOPS_API, ORGANIZATION)
                  + "wit/workItems/"
                  + story.getId()
                  + "?api-version=7.0",
              HttpMethod.GET,
              accessTokens.get(tokenIdentifier),
              null);
      JsonNode node = new ObjectMapper().readTree(response.getBody());
      Map<String, Object> testRev = new HashMap<>();
      testRev.put("op", "test");
      testRev.put("path", "/rev");
      testRev.put("value", node.get("rev").asText());
      content.add(testRev);

      executeRequest(
          String.format(
                  AZURE_DEVOPS_API,
                  ORGANIZATION + "/" + accessTokenToProjectId.get(tokenIdentifier))
              + "wit/workitems/"
              + story.getId()
              + "?api-version=7.0",
          HttpMethod.PATCH,
          accessTokens.get(tokenIdentifier),
          new Gson().toJson(content),
          MediaType.valueOf("application/json-patch+json"));
      LOGGER.debug("<-- updateIssue()");
    } catch (Exception e) {
      LOGGER.error("Failed to update issue!", e);
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.failedToEditIssueErrorMessage);
    }
  }

  @Override
  public String createIssue(String tokenIdentifier, String projectID, UserStory story) {
    LOGGER.debug("--> createIssue(), projectID={}", projectID);
    List<Map<String, String>> content = new ArrayList<>();
    String fieldPrefix = "/fields/";

    Map<String, String> title = new HashMap<>();
    title.put("op", "add");
    title.put("path", fieldPrefix + API_FIELD_TITLE);
    title.put("value", story.getTitle());
    content.add(title);

    Map<String, String> description = new HashMap<>();
    description.put("op", "add");
    description.put("path", fieldPrefix + API_FIELD_DESCRIPTION);
    description.put("value", "<div>" + story.getDescription() + " </div>");
    content.add(description);

    try {
      ResponseEntity<String> response =
          executeRequest(
              String.format(AZURE_DEVOPS_API, ORGANIZATION + "/" + projectID)
                  + "wit/workitems/$User Story?api-version=7.0",
              HttpMethod.POST,
              accessTokens.get(tokenIdentifier),
              new Gson().toJson(content),
              MediaType.valueOf("application/json-patch+json"));
      JsonNode node = new ObjectMapper().readTree(response.getBody());
      LOGGER.debug("<-- createIssue()");
      return node.path("id").asText();
    } catch (Exception e) {
      LOGGER.error("Failed to create issue!", e);
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.failedToDeleteIssueErrorMessage);
    }
  }

  @Override
  public void deleteIssue(String tokenIdentifier, String issueID) {
    LOGGER.debug("--> deleteIssue(), issueID={}", issueID);
    try {
      executeRequest(
          String.format(
                  AZURE_DEVOPS_API,
                  ORGANIZATION + "/" + accessTokenToProjectId.get(tokenIdentifier))
              + "wit/workitems/"
              + issueID
              + "?api-version=7.0",
          HttpMethod.DELETE,
          accessTokens.get(tokenIdentifier),
          null);
      LOGGER.debug("<-- deleteIssue()");
    } catch (Exception e) {
      LOGGER.error("Failed to delete issue!", e);
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.failedToEditIssueErrorMessage);
    }
  }

  @Override
  public boolean containsToken(String token) {
    return accessTokens.containsKey(token);
  }

  @Override
  public String getCurrentUsername(String tokenIdentifier) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public TokenIdentifier getAccessToken(String authorizationCode, String origin) {
    LOGGER.debug("--> getAccessToken()");
    val id = Utils.generateRandomID();
    accessTokens.put(id, CLIENT_PAT);
    LOGGER.debug("<-- getAccessToken()");
    return new TokenIdentifier(id);
  }
}
