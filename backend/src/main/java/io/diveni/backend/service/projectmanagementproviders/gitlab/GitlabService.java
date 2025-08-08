package io.diveni.backend.service.projectmanagementproviders.gitlab;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import io.diveni.backend.Utils;
import io.diveni.backend.controller.ErrorMessages;
import io.diveni.backend.model.Project;
import io.diveni.backend.model.TokenIdentifier;
import io.diveni.backend.model.UserStory;
import io.diveni.backend.service.projectmanagementproviders.ProjectManagementProviderOAuth2;
import jakarta.annotation.PostConstruct;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

@Service
public class GitlabService implements ProjectManagementProviderOAuth2 {

  private static final Logger LOGGER = LoggerFactory.getLogger(GitlabService.class);

  private boolean serviceEnabled = false;

  private final Map<String, String> accessTokens = new HashMap<>();

  private final Map<String, String> userNames = new HashMap<>();

  private final Map<String, String> projectIDs = new HashMap<>();

  private final Map<String, String> projectIDTokenID = new HashMap<>();

  private final String gitlabAPIString = "https://gitlab.com/api/v4/";

  @PostConstruct
  public void logConfig() {
    serviceEnabled = true;
    LOGGER.info("Gitlab Service: (enabled:" + serviceEnabled + ")");
  }

  @Override
  public boolean serviceEnabled() {
    return serviceEnabled;
  }

  public ResponseEntity<String> executeRequest(
      String url, HttpMethod method, String token, Object body) {
    LOGGER.debug("--> executeRequest()");
    // Create a RestTemplate object
    RestTemplate restTemplate = new RestTemplate();
    // Set the headers for the request
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(List.of(MediaType.APPLICATION_JSON));
    headers.setContentType(MediaType.APPLICATION_JSON);
    if (!token.equals("")) {
      headers.add("Authorization", "Bearer " + token);
    }
    HttpEntity<Object> request = new HttpEntity<>(body, headers);
    LOGGER.debug("<-- executeRequest()");
    return restTemplate.exchange(url, method, request, String.class);
  }

  @Override
  public List<Project> getProjects(String tokenIdentifier) {
    LOGGER.debug("--> getProjects()");
    List<Project> projects = new ArrayList<>();
    try {
      ResponseEntity<String> responseUser =
          executeRequest(
              this.gitlabAPIString + "user",
              HttpMethod.GET,
              accessTokens.get(tokenIdentifier),
              null);
      JsonNode node = new ObjectMapper().readTree(responseUser.getBody());
      String userName = node.get("username").asText();

      userNames.put(tokenIdentifier, userName);

      ResponseEntity<String> response =
          executeRequest(
              this.gitlabAPIString + "users/" + userName + "/projects",
              HttpMethod.GET,
              accessTokens.get(tokenIdentifier),
              null);
      JsonNode nodeProjects = new ObjectMapper().readTree(response.getBody());
      for (JsonNode jsonNode : nodeProjects) {
        projects.add(new Project(jsonNode.get("name").asText(), jsonNode.get("id").asText()));
        projectIDs.put(jsonNode.get("name").asText(), jsonNode.get("id").asText());
      }

      // get Projects of a group
      ResponseEntity<String> responseGroups =
          executeRequest(
              this.gitlabAPIString + "groups",
              HttpMethod.GET,
              accessTokens.get(tokenIdentifier),
              null);
      for (JsonNode groupProject : new ObjectMapper().readTree(responseGroups.getBody())) {
        ResponseEntity<String> responseGroupRepo =
            executeRequest(
                this.gitlabAPIString + "groups/" + groupProject.get("id").asText() + "/projects",
                HttpMethod.GET,
                accessTokens.get(tokenIdentifier),
                null);
        for (JsonNode jsonNode : new ObjectMapper().readTree(responseGroupRepo.getBody())) {
          projects.add(new Project(jsonNode.get("name").asText(), jsonNode.get("id").asText()));
          projectIDs.put(jsonNode.get("name").asText(), jsonNode.get("id").asText());
        }
      }
      LOGGER.debug("<-- getProjects()");
      return projects;
    } catch (Exception e) {
      LOGGER.debug("Error trying to get the Projects");
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.failedToRetrieveProjectsErrorMessage);
    }
  }

  @Override
  public List<UserStory> getIssues(String tokenIdentifier, String projectName) {
    LOGGER.debug("--> getIssues(), projectName{}", projectName);
    List<UserStory> userStories = new ArrayList<>();
    try {
      ResponseEntity<String> response =
          executeRequest(
              this.gitlabAPIString
                  + "projects/"
                  + projectIDs.get(projectName)
                  + "/issues?state=opened",
              HttpMethod.GET,
              accessTokens.get(tokenIdentifier),
              null);
      JsonNode nodeProjects = new ObjectMapper().readTree(response.getBody());
      for (JsonNode node : nodeProjects) {
        String estimation =
            node.get("time_stats").get("human_time_estimate").asText().replaceAll("[^\\d.]", "");
        if (estimation.isEmpty()) {
          estimation = null;
        }
        userStories.add(
            new UserStory(
                node.get("iid").asText(),
                node.get("title").asText(),
                node.get("description").asText(),
                estimation,
                null,
              null));
      }
      projectIDTokenID.put(tokenIdentifier, projectIDs.get(projectName));
      LOGGER.debug("<-- getIssues()");
      return userStories;
    } catch (Exception e) {
      LOGGER.debug("Error trying to get the issues!");
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.failedToEditIssueErrorMessage);
    }
  }

  @Override
  public void updateIssue(String tokenIdentifier, UserStory story) {
    LOGGER.debug("--> updateIssue()");
    Map<String, String> content = new HashMap<>();
    content.put("title", story.getTitle());
    content.put("description", story.getDescription());
    try {
      executeRequest(
          this.gitlabAPIString
              + "projects/"
              + projectIDTokenID.get(tokenIdentifier)
              + "/issues/"
              + story.getId(),
          HttpMethod.PUT,
          accessTokens.get(tokenIdentifier),
          new Gson().toJson(content));
      if (!isNull(story.getEstimation())) {
        updateEstimationOfIssue(tokenIdentifier, story);
      }
      LOGGER.debug("<-- updateIssue()");
    } catch (Exception e) {
      LOGGER.debug("Error trying to update the issue");
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.failedToEditIssueErrorMessage);
    }
  }

  private void updateEstimationOfIssue(String tokenIdentifier, UserStory story) {
    LOGGER.debug("--> updateEstimationOfIssue()");
    Map<String, String> content = new HashMap<>();
    content.put("duration", story.getEstimation() + "h");
    try {
      executeRequest(
          this.gitlabAPIString
              + "projects/"
              + projectIDTokenID.get(tokenIdentifier)
              + "/issues/"
              + story.getId()
              + "/time_estimate",
          HttpMethod.POST,
          accessTokens.get(tokenIdentifier),
          content);
      LOGGER.debug("<-- updateEstimationOfIssue()");
    } catch (Exception e) {
      LOGGER.debug("Error trying to update the estimation of the issue");
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.failedToEditIssueErrorMessage);
    }
  }

  @Override
  public String createIssue(String tokenIdentifier, String projectID, UserStory story) {
    LOGGER.debug("--> createIssue(), projectID={}, story={}", projectID, story);
    Map<String, String> content = new HashMap<>();
    content.put("id", story.getId());
    content.put("title", story.getTitle());
    content.put("description", story.getDescription());
    try {
      ResponseEntity<String> response =
          executeRequest(
              this.gitlabAPIString + "projects/" + projectID + "/issues",
              HttpMethod.POST,
              accessTokens.get(tokenIdentifier),
              new Gson().toJson(content));
      JsonNode node = new ObjectMapper().readTree(response.getBody());
      LOGGER.debug("<-- createIssue()");
      return node.path("iid").asText();
    } catch (Exception e) {
      LOGGER.debug("Error trying to create an Issue");
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.failedToEditIssueErrorMessage);
    }
  }

  @Override
  public void deleteIssue(String tokenIdentifier, String issueID) {
    LOGGER.debug("--> deleteIssue(), issueID={}", issueID);
    Map<String, String> content = new HashMap<>();
    content.put("state_event", "close");
    try {
      executeRequest(
          this.gitlabAPIString
              + "projects/"
              + projectIDTokenID.get(tokenIdentifier)
              + "/issues/"
              + issueID,
          HttpMethod.PUT,
          accessTokens.get(tokenIdentifier),
          new Gson().toJson(content));
      LOGGER.debug("<-- deleteIssue()");
    } catch (Exception e) {
      LOGGER.debug("Error trying to delete the issue");
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.failedToDeleteIssueErrorMessage);
    }
  }

  @Override
  public boolean containsToken(String token) {
    return accessTokens.containsKey(token);
  }

  @Override
  public String getCurrentUsername(String tokenIdentifier) {
    return this.userNames.get(tokenIdentifier);
  }

  @Override
  public TokenIdentifier getAccessToken(String authorizationCode, String origin) {
    return null;
  }

  public TokenIdentifier getAccessTokenForGitlab(String origin, String pat) {
    LOGGER.debug("--> getAccessToken(), origin={}", origin);
    val id = Utils.generateRandomID();
    accessTokens.put(id, pat);
    LOGGER.debug("<-- getAccessToken()");
    return new TokenIdentifier(id);
  }
}
