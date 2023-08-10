/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 Diveni Team, AUME-Team 21/22, HTWG Konstanz
*/
package io.diveni.backend.service.projectmanagementproviders.jira.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.api.client.http.HttpResponse;

import io.diveni.backend.Utils;
import io.diveni.backend.model.JiraConfig;
import io.diveni.backend.model.JiraRequestToken;
import io.diveni.backend.model.Project;
import io.diveni.backend.model.TokenIdentifier;
import io.diveni.backend.model.UserStory;
import io.diveni.backend.service.projectmanagementproviders.jira.JiraApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import io.diveni.backend.controller.ErrorMessages;
import io.diveni.backend.service.projectmanagementproviders.ProjectManagementProviderOAuth1;
import lombok.Getter;

import javax.annotation.PostConstruct;

@Service
public class JiraServerService implements ProjectManagementProviderOAuth1 {
  private static final Logger LOGGER = LoggerFactory.getLogger(JiraServerService.class);
  private boolean serviceEnabled = false;
  @Getter private final Map<String, JiraConfig> jiraConfigs = new HashMap<>();

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

  private JiraApiClient jiraApiClient;

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

  @Override
  public JiraRequestToken getRequestToken(Optional<String> url) {
    LOGGER.debug("--> getRequestToken()");
    try {
      JiraRequestToken jiraRequestToken = jiraApiClient.getRequestToken(JIRA_HOME, CONSUMER_KEY, PRIVATE_KEY);
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
      String accessToken = jiraApiClient.getAccessToken(verificationCode, requestToken, JIRA_HOME, CONSUMER_KEY, PRIVATE_KEY);
      String id = Utils.generateRandomID();
      getJiraConfigs().put(id, new JiraConfig(JIRA_HOME, accessToken, CONSUMER_KEY, PRIVATE_KEY));
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
      List<Project> projects = jiraApiClient.getProjects(getJiraConfigs().get(tokenIdentifier));
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
      String[] forbiddenTypes = {"Sub-Task", "Sub-Bug"};
      List<UserStory> userStories = jiraApiClient.getIssues(getJiraConfigs().get(tokenIdentifier), projectName, ESTIMATION_FIELD, RANK_NAME, forbiddenTypes);
      LOGGER.debug("<-- getIssues()");
      return userStories;
    } catch (Exception e) {
      LOGGER.error("Failed to get issues!", e);
      throw new ResponseStatusException(
        HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.failedToRetrieveProjectsErrorMessage);
    }
  }

  @Override
  public String createIssue(String tokenIdentifier, String projectID, UserStory story) {
    LOGGER.debug("--> createIssue(), projectID={}", projectID);
    try {
      String id = jiraApiClient.createIssue(getJiraConfigs().get(tokenIdentifier), projectID, story);
      LOGGER.debug("<-- createIssue()");
      return id;
    } catch (Exception e) {
      LOGGER.error("Failed to create issue!", e);
      throw new ResponseStatusException(
        HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.failedToDeleteIssueErrorMessage);
    }
  }

  @Override
  public void updateIssue(String tokenIdentifier, UserStory story) {
    LOGGER.debug("--> updateIssue(), storyID={}", story.getId());
    try {
      HttpResponse response = jiraApiClient.updateIssue(getJiraConfigs().get(tokenIdentifier), ESTIMATION_FIELD, story);
      LOGGER.debug("<-- updateIssue() {}", response.parseAsString());
    } catch (NumberFormatException e) {
      LOGGER.error("Failed to parse estimation into double!");
      throw new ResponseStatusException(
        HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.failedToEditIssueErrorMessage);
    } catch (Exception e) {
      LOGGER.error("Failed to update issue!", e);
      throw new ResponseStatusException(
        HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.failedToEditIssueErrorMessage);
    }
  }

  @Override
  public void deleteIssue(String tokenIdentifier, String issueID) {
    LOGGER.debug("--> deleteIssue(), issueID={}", issueID);
    try {
      HttpResponse response = jiraApiClient.deleteIssue(getJiraConfigs().get(tokenIdentifier), issueID);
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
      String accountId = jiraApiClient.getCurrentUsername(getJiraConfigs().get(tokenIdentifier));
      LOGGER.debug("<-- getCurrentUsername()");
      return accountId;
    } catch (Exception e) {
      LOGGER.error("Failed to get current username!", e);
      throw new ResponseStatusException(
        HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.failedToRetrieveUsernameErrorMessage);
    }
  }

  @Override
  public boolean containsToken(String token) {
    return jiraConfigs.containsKey(token);
  }

  @Autowired
  public void setJiraApiClient(JiraApiClient jiraApiClient) {
    this.jiraApiClient = jiraApiClient;
  }
}
