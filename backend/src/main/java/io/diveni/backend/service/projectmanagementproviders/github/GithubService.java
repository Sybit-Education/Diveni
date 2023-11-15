package io.diveni.backend.service.projectmanagementproviders.github;

import io.diveni.backend.model.Project;
import io.diveni.backend.model.TokenIdentifier;
import io.diveni.backend.model.UserStory;
import io.diveni.backend.service.projectmanagementproviders.ProjectManagementProviderOAuth2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import io.diveni.backend.Utils;
import io.diveni.backend.controller.ErrorMessages;
import lombok.Getter;
import lombok.val;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Service
public class GithubService implements ProjectManagementProviderOAuth2 {

  private static final Logger LOGGER = LoggerFactory.getLogger(GithubService.class);

  @Getter private final Map<String, String> accessTokens = new HashMap<>();

  @Getter private final Map<String, String> userNames = new HashMap<>();

  @Getter private final Map<String, String> projectNames = new HashMap<>();

  @Getter private final Map<String, String> selectedProject = new HashMap<>();

  @Value("${GITHUB_TOKEN:#{null}}")
  private String githubToken;

  private boolean serviceEnabled = false;

  @PostConstruct
  public void logConfig() {
    if (githubToken != null) {
      serviceEnabled = true;
    }
    LOGGER.info("Github Service: (enabled:" + serviceEnabled + ")");
    LOGGER.info("    GITHUB_PERSONAL_ACCESS_TOKEN={}", githubToken == null ? "null" : "********");
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

  public void executeRequestForPatch(String url, HttpMethod method, String token, Object body)
      throws IOException {
    LOGGER.debug("--> executeRequest()");
    CloseableHttpClient httpClient = HttpClients.createDefault();
    HttpUriRequest request =
        RequestBuilder.create(method.toString())
            .setUri(url)
            .setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setHeader("Authorization", "Bearer " + token)
            .setEntity(new StringEntity(body.toString()))
            .build();
    CloseableHttpResponse response = httpClient.execute(request);
    httpClient.close();
    LOGGER.debug("<-- executeRequest()");
  }

  @Override
  public boolean serviceEnabled() {
    return true;
  }

  @Override
  public List<Project> getProjects(String tokenIdentifier) {
    LOGGER.debug("--> getProjects()");
    try {
      List<Project> projects = new ArrayList<>();
      ResponseEntity<String> response =
          executeRequest(
              "https://api.github.com/user",
              HttpMethod.GET,
              accessTokens.get(tokenIdentifier),
              null);
      JsonNode node = new ObjectMapper().readTree(response.getBody());
      userNames.put(tokenIdentifier, node.get("login").asText());
      ResponseEntity<String> repoResponse =
          executeRequest(
              node.get("repos_url").asText(),
              HttpMethod.GET,
              accessTokens.get(tokenIdentifier),
              null);
      for (JsonNode projectNode : new ObjectMapper().readTree(repoResponse.getBody())) {
        projects.add(new Project(projectNode.get("name").asText(), projectNode.get("id").asText()));
        projectNames.put(projectNode.get("id").asText(), projectNode.get("name").asText());
      }
      LOGGER.debug("<-- getProjects()");
      return projects;
    } catch (Exception e) {
      LOGGER.debug("Error trying to get Projects!");
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.failedToRetrieveProjectsErrorMessage + e);
    }
  }

  @Override
  public List<UserStory> getIssues(String tokenIdentifier, String projectName) {
    LOGGER.debug("--> getIssues(), projectName={}", projectName);
    List<UserStory> userStories = new ArrayList<>();
    try {
      ResponseEntity<String> response =
          executeRequest(
              "https://api.github.com/repos/"
                  + getCurrentUsername(tokenIdentifier)
                  + "/"
                  + projectName
                  + "/issues",
              HttpMethod.GET,
              accessTokens.get(tokenIdentifier),
              null);
      for (JsonNode node : new ObjectMapper().readTree(response.getBody())) {
        String estimation = null;
        int endIndex = node.get("body").asText().lastIndexOf("**");
        int startIndex = 0;
        if (node.get("body").asText().contains("Story Points voted on Diveni.io:")) {
          for (int i = endIndex - 1; node.get("body").asText().charAt(i) != '*'; i--) {
            startIndex = i;
          }
          estimation = node.get("body").asText().substring(startIndex, endIndex);
          if (estimation.equals("null")) {
            estimation = null;
          }
        }
        String description;
        if (node.get("body").asText().contains("Story Points voted on Diveni.io:")) {
          description =
              node.get("body")
                  .asText()
                  .replace("\n Story Points voted on Diveni.io: **" + estimation + "**", "");
        } else {
          description = node.get("body").asText();
        }
        userStories.add(
            new UserStory(
                node.get("number").asText(),
                node.get("title").asText(),
                description,
                estimation,
                false));
      }
      selectedProject.put(accessTokens.get(tokenIdentifier), projectName);
      LOGGER.debug("<-- getIssues()");
      return userStories;
    } catch (Exception e) {
      LOGGER.debug("Error trying to receive the issues!");
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.failedToRetrieveProjectsErrorMessage);
    }
  }

  @Override
  public void updateIssue(String tokenIdentifier, UserStory story) {
    LOGGER.debug("--> updateIssue(), story={}", story.getId());
    Map<String, Object> content = new HashMap<>();
    content.put("title", story.getTitle());
    content.put(
        "body",
        story.getDescription() + " Story Points voted on Diveni.io: " + story.getEstimation());
    String description = story.getDescription();
    if (story.getDescription().contains("Story Points voted on Diveni.io:")) {
      description =
          story
              .getDescription()
              .replace("\n Story Points voted on Diveni.io: **" + story.getEstimation() + "**", "");
    }
    content.put(
        "body",
        description + "\n Story Points voted on Diveni.io: **" + story.getEstimation() + "**");
    try {
      executeRequestForPatch(
          "https://api.github.com/repos/"
              + getCurrentUsername(tokenIdentifier)
              + "/"
              + selectedProject.get(accessTokens.get(tokenIdentifier))
              + "/issues/"
              + story.getId(),
          HttpMethod.PATCH,
          accessTokens.get(tokenIdentifier),
          new Gson().toJson(content));
      LOGGER.debug("<-- updateIssue()");
    } catch (Exception e) {
      LOGGER.debug("Error trying to update the Issue!");
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.failedToEditIssueErrorMessage);
    }
  }

  @Override
  public String createIssue(String tokenIdentifier, String projectID, UserStory story) {
    LOGGER.debug("--> createIssue(), projectID={}, story={}", projectID, story);
    Map<String, Object> content = new HashMap<>();
    content.put("title", story.getTitle());
    content.put("body", story.getDescription());
    try {
      ResponseEntity<String> response =
          executeRequest(
              "https://api.github.com/repos/"
                  + getCurrentUsername(tokenIdentifier)
                  + "/"
                  + projectNames.get(projectID)
                  + "/issues",
              HttpMethod.POST,
              accessTokens.get(tokenIdentifier),
              new Gson().toJson(content));
      JsonNode node = new ObjectMapper().readTree(response.getBody());
      LOGGER.debug("<-- createIssue()");
      return node.path("number").asText();
    } catch (Exception e) {
      LOGGER.debug("Error trying to create an Issue with Github!");
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.failedToEditIssueErrorMessage);
    }
  }

  @Override
  public void deleteIssue(String tokenIdentifier, String issueID) {
    LOGGER.debug("--> deleteIssue(), issueID={}", issueID);
    Map<String, Object> content = new HashMap<>();
    content.put("state", "closed");
    try {
      executeRequestForPatch(
          "https://api.github.com/repos/"
              + getCurrentUsername(tokenIdentifier)
              + "/"
              + selectedProject.get(accessTokens.get(tokenIdentifier))
              + "/issues/"
              + issueID,
          HttpMethod.PATCH,
          accessTokens.get(tokenIdentifier),
          new Gson().toJson(content));
      LOGGER.debug("<-- deleteIssue()");
    } catch (Exception e) {
      LOGGER.debug("Error trying to delete the Issue!");
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.failedToDeleteIssueErrorMessage);
    }
  }

  @Override
  public boolean containsToken(String token) {
    return this.accessTokens.containsKey(token);
  }

  @Override
  public String getCurrentUsername(String tokenIdentifier) {
    return userNames.get(tokenIdentifier);
  }

  @Override
  public TokenIdentifier getAccessToken(String authorizationCode, String origin) {
    LOGGER.debug("--> getAccessToken(), origin={},", origin);
    val id = Utils.generateRandomID();
    accessTokens.put(id, this.githubToken);
    LOGGER.debug("<-- getAccessToken()");
    return new TokenIdentifier(id);
  }
}
