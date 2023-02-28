/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 Diveni Team, AUME-Team 21/22, HTWG Konstanz
*/
package io.diveni.backend.service.projectmanagementproviders.jiracloud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.api.client.util.Base64;

import io.diveni.backend.Utils;
import io.diveni.backend.model.Project;
import io.diveni.backend.model.TokenIdentifier;
import io.diveni.backend.model.UserStory;
import io.diveni.backend.service.projectmanagementproviders.ProjectManagementProviderOAuth2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import io.diveni.backend.controller.ErrorMessages;
import lombok.Getter;
import lombok.val;

import javax.annotation.PostConstruct;

@Service
public class JiraCloudService implements ProjectManagementProviderOAuth2 {

  private static final Logger LOGGER = LoggerFactory.getLogger(JiraCloudService.class);
  private static final int JIRA_CLOUD_API_VERSION = 2;
  private static final String JIRA_OAUTH_URL = "https://auth.atlassian.com/oauth";
  private static final String JIRA_HOME = "https://api.atlassian.com/ex/jira/%s/rest/api/";
  private boolean serviceEnabled = false;
  @Getter private final Map<String, String> accessTokens = new HashMap<>();

  @Value("${JIRA_CLOUD_CLIENTID:#{null}}")
  private String CLIENT_ID;

  @Value("${JIRA_CLOUD_CLIENTSECRET:#{null}}")
  private String CLIENT_SECRET;

  @Value("${JIRA_CLOUD_ESTIMATIONFIELD:customfield_10016}")
  private String ESTIMATION_FIELD;

  @Value("${JIRA_CLOUD_AUTHORIZE_URL:#{null}}")
  private String JIRA_CLOUD_AUTHORIZE_URL;

  @PostConstruct
  public void setupAndLogConfig() {
    if (CLIENT_ID != null && CLIENT_SECRET != null && ESTIMATION_FIELD != null && JIRA_CLOUD_AUTHORIZE_URL != null) {
      serviceEnabled = true;
    }

    LOGGER.info("Jira-Cloud Service: (enabled:" + serviceEnabled + ")");

    LOGGER.info("    JIRA_CLOUD_CLIENTID={}", CLIENT_ID == null ? "null" : "********");
    LOGGER.info("    JIRA_CLOUD_CLIENTSECRET={}", CLIENT_SECRET == null ? "null" : "********");
    LOGGER.info("    JIRA_SERVER_ESTIMATIONFIELD={}", ESTIMATION_FIELD);
    LOGGER.info("    JIRA_CLOUD_AUTHORIZE_URL={}", JIRA_CLOUD_AUTHORIZE_URL);
  }

  @Override
  public boolean serviceEnabled() {
    return serviceEnabled;
  }

  public String getJiraCloudAuthorizeUrl() {
    return JIRA_CLOUD_AUTHORIZE_URL;
  }

  static String getCloudID(String accessToken) {
    LOGGER.debug("--> getCloudID()");
    String accessibleResourcesURL = "https://api.atlassian.com/oauth/token/accessible-resources";
    ResponseEntity<String> response =
        executeRequest(accessibleResourcesURL, HttpMethod.GET, accessToken, null);
    try {
      ObjectNode[] node = new ObjectMapper().readValue(response.getBody(), ObjectNode[].class);
      for (ObjectNode objectNode : node) {
        if (objectNode.has("id")) {
          LOGGER.debug("<-- getCloudID()");
          return objectNode.get("id").asText();
        }
      }
      LOGGER.debug("<-- getCloudID(), CloudID not found");
      return null;
    } catch (Exception e) {
      LOGGER.error("Failed to get cloud id!", e);
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.failedToRetrieveAccessTokenErrorMessage);
    }
  }

  static ResponseEntity<String> executeRequest(
      String url, HttpMethod method, String accessToken, Object body) {
    LOGGER.debug("--> executeRequest()");
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(List.of(MediaType.APPLICATION_JSON));
    headers.add("Authorization", "Bearer " + accessToken);
    HttpEntity<Object> request = new HttpEntity<>(body, headers);
    LOGGER.debug("<-- executeRequest()");
    return restTemplate.exchange(url, method, request, String.class);
  }

  @Override
  public TokenIdentifier getAccessToken(String authorizationCode, String origin) {
    LOGGER.debug("--> getAccessToken()");
    RestTemplate restTemplate = new RestTemplate();
    String credentials = CLIENT_ID + ":" + CLIENT_SECRET;
    String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));

    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(List.of(MediaType.APPLICATION_JSON));
    headers.add("Authorization", "Basic " + encodedCredentials);

    HttpEntity<String> request = new HttpEntity<>(headers);

    String accessTokenURL = JIRA_OAUTH_URL + "/token";
    accessTokenURL += "?code=" + authorizationCode;
    accessTokenURL += "&grant_type=authorization_code";
    accessTokenURL += "&redirect_uri=" + origin + "/#/jiraCallback";

    ResponseEntity<String> response =
        restTemplate.exchange(accessTokenURL, HttpMethod.POST, request, String.class);

    ObjectMapper mapper = new ObjectMapper();
    JsonNode node;
    try {
      node = mapper.readTree(response.getBody());
      String accessToken = node.path("access_token").asText();
      val id = Utils.generateRandomID();
      accessTokens.put(id, accessToken);
      LOGGER.debug("<-- getAccessToken()");
      return new TokenIdentifier(id);
    } catch (Exception e) {
      LOGGER.error("Failed to get access token!", e);
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.failedToRetrieveAccessTokenErrorMessage);
    }
  }

  @Override
  public List<Project> getProjects(String tokenIdentifier) {
    LOGGER.debug("--> getProjects()");
    val accessToken = accessTokens.get(tokenIdentifier);
    String cloudID = getCloudID(accessToken);
    try {
      List<Project> projects = new ArrayList<>();
      ResponseEntity<String> response =
          executeRequest(
              String.format(getJiraUrl(), cloudID) + "/project/search",
              HttpMethod.GET,
              accessToken,
              null);
      JsonNode node = new ObjectMapper().readTree(response.getBody());

      for (JsonNode projectNode : node.path("values")) {
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
    String cloudID = getCloudID(accessTokens.get(tokenIdentifier));
    ResponseEntity<String> response =
        executeRequest(
            String.format(getJiraUrl(), cloudID)
                + "/search?jql=project='"
                + projectName
                + "' order by rank&fields=summary,description,"
                + ESTIMATION_FIELD,
            HttpMethod.GET,
            accessTokens.get(tokenIdentifier),
            null);
    try {
      List<UserStory> userStories = new ArrayList<>();
      JsonNode node = new ObjectMapper().readTree(response.getBody());
      for (JsonNode issue : node.path("issues")) {
        val fields = issue.get("fields");
        String estimation =
            fields.get(ESTIMATION_FIELD).isNull()
                ? null
                : String.valueOf(fields.get(ESTIMATION_FIELD).asDouble());
        if (estimation != null && estimation.endsWith(".0")) {
          estimation = estimation.substring(0, estimation.length() - 2);
        }
        userStories.add(
            new UserStory(
                issue.get("id").textValue(),
                fields.get("summary").textValue(),
                fields.get("description").textValue(),
                estimation,
                false));
      }
      LOGGER.debug("<-- getIssues()");
      return userStories;
    } catch (Exception e) {
      LOGGER.error("Failed to get issues!", e);
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.failedToRetrieveProjectsErrorMessage);
    }
  }

  @Override
  public void updateIssue(String tokenIdentifier, UserStory story) {
    LOGGER.debug("--> updateIssue(), storyID={}", story.getJiraId());
    String cloudID = getCloudID(accessTokens.get(tokenIdentifier));
    Map<String, Map<String, Object>> content = new HashMap<>();
    Map<String, Object> fields = new HashMap<>();
    fields.put("summary", story.getTitle());
    fields.put("description", story.getDescription());
    if (story.getEstimation() != null) {
      try {
        fields.put(ESTIMATION_FIELD, Double.parseDouble(story.getEstimation()));
      } catch (NumberFormatException e) {
        LOGGER.error("Failed to parse estimation into double!");
        throw new ResponseStatusException(
            HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.failedToEditIssueErrorMessage);
      }
    }
    content.put("fields", fields);
    try {
      executeRequest(
          String.format(getJiraUrl(), cloudID) + "/issue/" + story.getJiraId(),
          HttpMethod.PUT,
          accessTokens.get(tokenIdentifier),
          content);
      LOGGER.debug("<-- updateIssue()");
    } catch (Exception e) {
      LOGGER.error("Failed to update issue!", e);
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.failedToEditIssueErrorMessage);
    }
  }

  @Override
  public void deleteIssue(String tokenIdentifier, String jiraID) {
    LOGGER.debug("--> deleteIssue(), jiraID={}", jiraID);
    try {
      String cloudID = getCloudID(accessTokens.get(tokenIdentifier));
      executeRequest(
          String.format(getJiraUrl(), cloudID) + "/issue/" + jiraID,
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
  public String getCurrentUsername(String tokenIdentifier) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean containsToken(String token) {
    return accessTokens.containsKey(token);
  }

  @Override
  public String createIssue(String tokenIdentifier, String projectID, UserStory story) {
    // TODO Auto-generated method stub
    return null;
  }

  private String getJiraUrl() {
    return JIRA_HOME + JIRA_CLOUD_API_VERSION;
  }
}
