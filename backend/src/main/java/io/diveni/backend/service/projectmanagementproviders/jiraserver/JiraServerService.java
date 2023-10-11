/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 Diveni Team, AUME-Team 21/22, HTWG Konstanz
*/
package io.diveni.backend.service.projectmanagementproviders.jiraserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.api.client.auth.oauth.OAuthAuthorizeTemporaryTokenUrl;
import com.google.api.client.auth.oauth.OAuthParameters;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.gson.GsonFactory;

import io.diveni.backend.Utils;
import io.diveni.backend.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import io.diveni.backend.controller.ErrorMessages;
import io.diveni.backend.service.projectmanagementproviders.ProjectManagementProviderOAuth1;
import lombok.Getter;
import lombok.val;

import javax.annotation.PostConstruct;

@Service
public class JiraServerService implements ProjectManagementProviderOAuth1 {
  private static final Logger LOGGER = LoggerFactory.getLogger(JiraServerService.class);
  private static final String USER_STORY_ID = "10002";
  private static final int JIRA_SERVER_API_VERSION = 2;
  private boolean serviceEnabled = false;
  @Getter private final Map<String, String> accessTokens = new HashMap<>();

  @Value("${JIRA_SERVER_JIRAHOME:#{null}}")
  private String JIRA_HOME;

  @Value("${JIRA_SERVER_CONSUMERKEY:OauthKey}")
  private String CONSUMER_KEY;

  @Value("${JIRA_SERVER_PRIVATEKEY:#{null}}")
  private String PRIVATE_KEY;

  @Value("${JIRA_SERVER_ESTIMATIONFIELD:customfield_10111}")
  private String ESTIMATION_FIELD;

  @Value("${JIRA_SERVER_RANKNAME:RANK}")
  private String RANK_NAME;

  @PostConstruct
  public void setupAndLogConfig() {
    if (JIRA_HOME != null
        && CONSUMER_KEY != null
        && PRIVATE_KEY != null
        && ESTIMATION_FIELD != null
        && RANK_NAME != null) {
      serviceEnabled = true;
    }

    LOGGER.info("Jira-Server Service: (enabled:" + serviceEnabled + ")");

    LOGGER.info("    JIRA_SERVER_JIRAHOME={}", JIRA_HOME);
    LOGGER.info("    JIRA_SERVER_CONSUMERKEY={}", CONSUMER_KEY);
    LOGGER.info("    JIRA_SERVER_PRIVATEKEY={}", PRIVATE_KEY == null ? "null" : "********");
    LOGGER.info("    JIRA_SERVER_ESTIMATIONFIELD={}", ESTIMATION_FIELD);
    LOGGER.info("    JIRA_SERVER_RANKNAME={}", RANK_NAME);
  }

  @Override
  public boolean serviceEnabled() {
    return serviceEnabled;
  }

  /**
   * Authenticates to JIRA with given OAuthParameters and makes request to url
   *
   * @param parameters
   * @param jiraUrl
   * @return
   * @throws IOException
   */
  private static HttpResponse getResponseFromUrl(
      OAuthParameters parameters, GenericUrl jiraUrl, String requestMethod, HttpContent content)
      throws IOException {
    if (content != null) {
      content.writeTo(System.out);
    }
    HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory(parameters);
    HttpRequest request = requestFactory.buildRequest(requestMethod, jiraUrl, content);
    if (request.getContent() != null) {
      request.getContent().writeTo(System.out);
    }
    return request.execute();
  }

  @Override
  public JiraRequestToken getRequestToken() {
    LOGGER.debug("--> getRequestToken()");
    JiraRequestToken jiraRequestToken = new JiraRequestToken();

    try {
      JiraOAuthTokenFactory oAuthGetAccessTokenFactory = new JiraOAuthTokenFactory(JIRA_HOME);

      JiraOAuthGetTemporaryToken temporaryToken =
          oAuthGetAccessTokenFactory.getTemporaryToken(CONSUMER_KEY, PRIVATE_KEY);
      String token = temporaryToken.execute().token;

      String authorizationUrl = JIRA_HOME + "/plugins/servlet/oauth/authorize";
      OAuthAuthorizeTemporaryTokenUrl authorizationURL =
          new OAuthAuthorizeTemporaryTokenUrl(authorizationUrl);
      authorizationURL.temporaryToken = token;

      jiraRequestToken.setToken(token);
      jiraRequestToken.setUrl(authorizationURL.toString());
      LOGGER.debug("<-- getRequestToken()");
      return jiraRequestToken;
    } catch (Exception e) {
      LOGGER.error("Failed to get request token!", e);
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.failedToRetrieveRequestTokenErrorMessage);
    }
  }

  @Override
  public TokenIdentifier getAccessToken(String verificationCode, String requestToken) {
    LOGGER.debug("--> getAccessToken()");
    try {
      JiraOAuthClient jiraOAuthClient = new JiraOAuthClient(JIRA_HOME);
      val accessToken =
          jiraOAuthClient.getAccessToken(requestToken, verificationCode, CONSUMER_KEY, PRIVATE_KEY);
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
    try {
      List<Project> projects = new ArrayList<>();
      val accessToken = accessTokens.get(tokenIdentifier);
      JiraOAuthClient jiraOAuthClient = new JiraOAuthClient(JIRA_HOME);
      OAuthParameters parameters =
          jiraOAuthClient.getParameters(accessToken, CONSUMER_KEY, PRIVATE_KEY);
      HttpResponse response =
          getResponseFromUrl(parameters, new GenericUrl(getJiraUrl() + "/project"), "GET", null);
      ObjectNode[] node =
          new ObjectMapper().readValue(response.parseAsString(), ObjectNode[].class);

      for (ObjectNode objectNode : node) {
        projects.add(new Project(objectNode.get("name").asText(), objectNode.get("id").asText()));
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
    try {
      List<UserStory> userStories = new ArrayList<>();
      val accessToken = accessTokens.get(tokenIdentifier);
      JiraOAuthClient jiraOAuthClient = new JiraOAuthClient(JIRA_HOME);
      OAuthParameters parameters =
          jiraOAuthClient.getParameters(accessToken, CONSUMER_KEY, PRIVATE_KEY);
      HttpResponse response =
          getResponseFromUrl(
              parameters,
              new GenericUrl(
                  getJiraUrl()
                      + "/search?jql=project='"
                      + projectName
                      + "' AND resolution = Unresolved "
                      + " AND type != Sub-Task AND type != Sub-Bug "
                      + "ORDER BY "
                      + RANK_NAME
                      + " ASC, updated DESC"
                      + "&fields=summary,description,"
                      + ESTIMATION_FIELD
                      + "&maxResults=1000"),
              "GET",
              null);
      // The reply from the Jira API is no correct JSON, therefore [ and ] have to be
      // added
      val json = "[" + response.parseAsString() + "]";

      ObjectNode[] node = new ObjectMapper().readValue(json, ObjectNode[].class);

      for (ObjectNode objectNode : node) {
        for (JsonNode jsonNode : objectNode.get("issues")) {
          val fields = jsonNode.get("fields");
          String estimation =
              (fields.get(ESTIMATION_FIELD) == null || fields.get(ESTIMATION_FIELD).isNull())
                  ? null
                  : String.valueOf(fields.get(ESTIMATION_FIELD).asDouble());
          if (estimation != null && estimation.endsWith(".0")) {
            estimation = estimation.substring(0, estimation.length() - 2);
          }
          userStories.add(
              new UserStory(
                  jsonNode.get("id").textValue(),
                  fields.get("summary").textValue(),
                  fields.get("description").textValue(),
                  estimation,
                  false));
        }
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
    LOGGER.debug("--> updateIssue(), storyID={}", story.getId());
    Map<String, Map<String, Object>> content = new HashMap<>();
    Map<String, Object> fields = new HashMap<>();
    fields.put("summary", story.getTitle());
    fields.put("description", story.getDescription());
    if (story.getEstimation() != null) {
      try {
        if (story.getEstimation().equals("-")) {
          fields.put(ESTIMATION_FIELD, null);
        } else {
          fields.put(ESTIMATION_FIELD, Double.parseDouble(story.getEstimation()));
        }
      } catch (NumberFormatException e) {
        LOGGER.error("Failed to parse estimation into double!");
        throw new ResponseStatusException(
            HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.failedToEditIssueErrorMessage);
      }
    }
    content.put("fields", fields);
    try {
      JiraOAuthClient jiraOAuthClient = new JiraOAuthClient(JIRA_HOME);
      OAuthParameters parameters =
          jiraOAuthClient.getParameters(
              accessTokens.get(tokenIdentifier), CONSUMER_KEY, PRIVATE_KEY);
      //System.out.println("Test: " + new JsonHttpContent(GsonFactory.getDefaultInstance(), content).getData().toString());
      // still has the ESTIMATION FIELD KEY with its null VALUE
      for (Object o : content.entrySet()) {
        System.out.println(" --> " + o);
      }
      HttpResponse response =
          getResponseFromUrl(
              parameters,
              new GenericUrl(getJiraUrl() + "/issue/" + story.getId()),
              "PUT",
              new CustomJsonHttpContent(GsonFactory.getDefaultInstance(), content));

      LOGGER.debug("<-- updateIssue() {}", response.parseAsString());
    } catch (Exception e) {
      LOGGER.error("Failed to update issue!", e);
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.failedToEditIssueErrorMessage);
    }
  }

  public String createIssue(String tokenIdentifier, String projectID, UserStory story) {
    LOGGER.debug("--> createIssue(), projectID={}", projectID);
    Map<String, Map<String, Object>> content = new HashMap<>();
    Map<String, Object> fields = new HashMap<>();
    fields.put("reporter", Map.of("name", getCurrentUsername(tokenIdentifier)));
    fields.put("issuetype", Map.of("id", USER_STORY_ID));
    fields.put("project", Map.of("id", projectID));
    fields.put("summary", story.getTitle());
    content.put("fields", fields);
    try {
      JiraOAuthClient jiraOAuthClient = new JiraOAuthClient(JIRA_HOME);
      OAuthParameters parameters =
          jiraOAuthClient.getParameters(
              accessTokens.get(tokenIdentifier), CONSUMER_KEY, PRIVATE_KEY);
      HttpResponse response =
          getResponseFromUrl(
              parameters,
              new GenericUrl(getJiraUrl() + "/issue"),
              "POST",
              new JsonHttpContent(GsonFactory.getDefaultInstance(), content));

      JsonNode node = new ObjectMapper().readTree(response.parseAsString());
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
      JiraOAuthClient jiraOAuthClient = new JiraOAuthClient(JIRA_HOME);
      OAuthParameters parameters =
          jiraOAuthClient.getParameters(
              accessTokens.get(tokenIdentifier), CONSUMER_KEY, PRIVATE_KEY);
      HttpResponse response =
          getResponseFromUrl(
              parameters, new GenericUrl(getJiraUrl() + "/issue/" + issueID), "DELETE", null);

      LOGGER.debug("<-- deleteIssue() {}", response.parseAsString());
    } catch (Exception e) {
      LOGGER.error("Deletion failed", e);
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.failedToDeleteIssueErrorMessage);
    }
  }

  @Override
  public String getCurrentUsername(String tokenIdentifier) {
    LOGGER.debug("--> getCurrentUsername(), tokenIdentifier={}", tokenIdentifier);
    try {
      JiraOAuthClient jiraOAuthClient = new JiraOAuthClient(JIRA_HOME);
      OAuthParameters parameters =
          jiraOAuthClient.getParameters(
              accessTokens.get(tokenIdentifier), CONSUMER_KEY, PRIVATE_KEY);
      HttpResponse response =
          getResponseFromUrl(
              parameters, new GenericUrl(JIRA_HOME + "/rest/auth/latest/session"), "GET", null);
      String res = response.parseAsString();
      JsonNode node = new ObjectMapper().readTree(res);
      LOGGER.debug("<-- getCurrentUsername()");
      return node.path("name").asText();
    } catch (Exception e) {
      LOGGER.error("Failed to get current username!", e);
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.failedToRetrieveUsernameErrorMessage);
    }
  }

  @Override
  public boolean containsToken(String token) {
    return accessTokens.containsKey(token);
  }

  private String getJiraUrl() {
    return String.format("%s/rest/api/%d", JIRA_HOME, JIRA_SERVER_API_VERSION);
  }
}
