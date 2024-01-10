/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 Diveni Team, AUME-Team 21/22, HTWG Konstanz
*/
package io.diveni.backend.controller;

import java.util.List;
import java.util.Optional;

import io.diveni.backend.model.*;
import io.diveni.backend.service.DatabaseService;
import io.diveni.backend.service.projectmanagementproviders.ProjectManagementProvider;
import io.diveni.backend.service.projectmanagementproviders.azuredevops.AzureDevOpsService;
import io.diveni.backend.service.projectmanagementproviders.jira.cloud.JiraCloudService;
import io.diveni.backend.service.projectmanagementproviders.jira.server.JiraServerService;
import io.diveni.backend.service.projectmanagementproviders.github.GithubService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import lombok.val;

@RestController
@RequestMapping("/issue-tracker")
public class ProjectManagementController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProjectManagementController.class);

  @Autowired DatabaseService databaseService;

  @Autowired JiraServerService jiraServerService;

  @Autowired JiraCloudService jiraCloudService;

  @Autowired AzureDevOpsService azureDevOpsService;

  @Autowired GithubService githubService;

  private final String PROVIDER_NOT_ENABLED_MESSAGE =
      "The selected issue tracker is not enabled. Make sure to set all required parameters.";

  @GetMapping(value = "/jira/server/request-token")
  public ResponseEntity<JiraRequestToken> getJiraServerRequestToken() {
    LOGGER.debug("--> getJiraServerRequestToken()");
    if (!jiraServerService.serviceEnabled()) {
      LOGGER.warn("Jira Server is not configured!");
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, PROVIDER_NOT_ENABLED_MESSAGE);
    }
    ResponseEntity<JiraRequestToken> response =
        new ResponseEntity<>(jiraServerService.getRequestToken(Optional.empty()), HttpStatus.OK);
    LOGGER.debug("<-- getRequestToken()");
    return response;
  }

  @PostMapping(value = "/jira/server/verification-code")
  public ResponseEntity<TokenIdentifier> getJiraServerAccessToken(
      @RequestBody VerificationCode verificationCode) {
    LOGGER.debug("--> getJiraServerAccessToken()");
    if (!jiraServerService.serviceEnabled()) {
      LOGGER.warn("Jira Server is not configured!");
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, PROVIDER_NOT_ENABLED_MESSAGE);
    }
    ResponseEntity<TokenIdentifier> response =
        new ResponseEntity<>(
            jiraServerService.getAccessToken(
                verificationCode.getCode(), verificationCode.getToken()),
            HttpStatus.OK);
    LOGGER.debug("<-- getOauth1AccessToken()");
    return response;
  }

  @PostMapping(value = "/jira/cloud/request-token")
  public ResponseEntity<JiraRequestToken> getJiraCloudAccessToken(
      @RequestParam("jira-url") String jiraUrl, @RequestHeader("Origin") String origin) {
    LOGGER.debug("--> getJiraCloudAccessToken(), origin={}", origin);
    if (!jiraCloudService.serviceEnabled()) {
      LOGGER.warn("Jira Cloud is not configured!");
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, PROVIDER_NOT_ENABLED_MESSAGE);
    }
    jiraUrl = checkJiraUrl(jiraUrl);
    ResponseEntity<JiraRequestToken> response =
        new ResponseEntity<>(jiraCloudService.getRequestToken(Optional.of(jiraUrl)), HttpStatus.OK);
    LOGGER.debug("<-- getOAuth2AccessToken()");
    return response;
  }

  @PostMapping(value = "/jira/cloud/verification-code")
  public ResponseEntity<TokenIdentifier> getJiraCloudAccessToken(
      @RequestBody VerificationCode verificationCode) {
    LOGGER.debug("--> getJiraCloudAccessToken()");
    if (!jiraCloudService.serviceEnabled()) {
      LOGGER.warn("Jira Cloud is not configured!");
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, PROVIDER_NOT_ENABLED_MESSAGE);
    }
    ResponseEntity<TokenIdentifier> response =
        new ResponseEntity<>(
            jiraCloudService.getAccessToken(
                verificationCode.getCode(), verificationCode.getToken()),
            HttpStatus.OK);
    LOGGER.debug("<-- getOauth1AccessToken()");
    return response;
  }

  @PostMapping("/azure/cloud/authorization-code")
  public ResponseEntity<TokenIdentifier> getAzureOAuth2AccessToken(
      @RequestHeader("Origin") String origin) {
    LOGGER.debug("--> getOAuth2AccessToken(), origin={}", origin);
    if (!azureDevOpsService.serviceEnabled()) {
      LOGGER.warn("Azure DevOps is not configured!");
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, PROVIDER_NOT_ENABLED_MESSAGE);
    }
    ResponseEntity<TokenIdentifier> response =
        new ResponseEntity<>(azureDevOpsService.getAccessToken("", origin), HttpStatus.OK);
    LOGGER.debug("<-- getOAuth2AccessToken()");
    return response;
  }

  @PostMapping("/github/oauth2/accessToken")
  public ResponseEntity<TokenIdentifier> getGithubOAuth2AccessToken(
      @RequestHeader("Origin") String origin, @RequestBody PersonalAccessToken pat) {
    LOGGER.debug("--> getOAuth2GithubAccessToken() , origin={}", origin);
    ResponseEntity<TokenIdentifier> response =
        new ResponseEntity<>(
            githubService.getAccessTokenForGithub(origin, pat.getCode()), HttpStatus.OK);
    LOGGER.debug("<-- getGithubOAuth2AccessToken()");
    return response;
  }

  @GetMapping(value = "/projects")
  public ResponseEntity<List<Project>> getProjects(
      @RequestHeader("X-Token-ID") String tokenIdentifier) {
    LOGGER.debug("--> getProjects(), tokenIdentifier={}", tokenIdentifier);
    val projectManagementProvider = getProjectManagementProvider(tokenIdentifier);

    ResponseEntity<List<Project>> response;
    if (projectManagementProvider == null) {
      LOGGER.warn("Bad Request: projectManagementProvider is null!");
      response = new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    } else if (!projectManagementProvider.serviceEnabled()) {
      LOGGER.warn("projectManagementProvider is not configured!");
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, PROVIDER_NOT_ENABLED_MESSAGE);
    } else {
      response =
          new ResponseEntity<>(
              projectManagementProvider.getProjects(tokenIdentifier), HttpStatus.OK);
    }
    LOGGER.debug("<-- getProjects()");
    return response;
  }

  @GetMapping(value = "/projects/{projectName}/issues")
  public ResponseEntity<List<UserStory>> getIssues(
      @RequestHeader("X-Token-ID") String tokenIdentifier, @PathVariable String projectName) {
    LOGGER.debug("--> getIssues(), projectName={}", projectName);
    val projectManagementProvider = getProjectManagementProvider(tokenIdentifier);

    ResponseEntity<List<UserStory>> response;
    if (projectManagementProvider == null) {
      LOGGER.warn("Bad Request: projectManagementProvider is null!");
      response = new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    } else if (!projectManagementProvider.serviceEnabled()) {
      LOGGER.warn("projectManagementProvider is not configured!");
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, PROVIDER_NOT_ENABLED_MESSAGE);
    } else {
      response =
          new ResponseEntity<>(
              projectManagementProvider.getIssues(tokenIdentifier, projectName), HttpStatus.OK);
    }
    LOGGER.debug("<-- getIssues()");
    return response;
  }

  @PutMapping(value = "/issue")
  public void updateIssue(
      @RequestHeader("X-Token-ID") String tokenIdentifier, @RequestBody UserStory userStory) {
    LOGGER.debug("--> updateIssue(), userStoryId={}", userStory.getId());
    val projectManagementProvider = getProjectManagementProvider(tokenIdentifier);

    if (projectManagementProvider == null) {
      LOGGER.error("Could not update issue!");
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update issue");
    } else if (!projectManagementProvider.serviceEnabled()) {
      LOGGER.warn("projectManagementProvider is not configured!");
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, PROVIDER_NOT_ENABLED_MESSAGE);
    } else {
      projectManagementProvider.updateIssue(tokenIdentifier, userStory);
      LOGGER.debug("<-- updateIssue()");
    }
  }

  @PostMapping(value = "issue")
  public ResponseEntity<String> createIssue(
      @RequestHeader("X-Token-ID") String tokenIdentifier,
      @RequestParam("projectID") String projectID,
      @RequestBody UserStory userStory) {
    LOGGER.debug("--> createIssue(), projectID={}, userStoryId={}", projectID, userStory.getId());

    val projectManagementProvider = getProjectManagementProvider(tokenIdentifier);

    if (projectManagementProvider == null) {
      LOGGER.error("Failed to create issue!");
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create issue");
    } else if (!projectManagementProvider.serviceEnabled()) {
      LOGGER.warn("projectManagementProvider is not configured!");
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, PROVIDER_NOT_ENABLED_MESSAGE);
    }

    ResponseEntity<String> response =
        new ResponseEntity<>(
            projectManagementProvider.createIssue(tokenIdentifier, projectID, userStory),
            HttpStatus.OK);
    LOGGER.debug("<-- createIssue()");
    return response;
  }

  @DeleteMapping(value = "/issue/{jiraID}")
  public void deleteIssue(
      @RequestHeader("X-Token-ID") String tokenIdentifier, @PathVariable String jiraID) {
    LOGGER.debug("--> deleteIssue(), jiraID={}", jiraID);
    val projectManagementProvider = getProjectManagementProvider(tokenIdentifier);

    if (projectManagementProvider == null) {
      LOGGER.error("Could not delete issue!");
    } else if (!projectManagementProvider.serviceEnabled()) {
      LOGGER.warn("projectManagementProvider is not configured!");
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, PROVIDER_NOT_ENABLED_MESSAGE);
    } else {
      projectManagementProvider.deleteIssue(tokenIdentifier, jiraID);
    }
    LOGGER.debug("<-- deleteIssue()");
  }

  private ProjectManagementProvider getProjectManagementProvider(String tokenIdentifier) {
    if (jiraServerService.containsToken(tokenIdentifier)) {
      return jiraServerService;
    } else if (jiraCloudService.containsToken(tokenIdentifier)) {
      return jiraCloudService;
    } else if (azureDevOpsService.containsToken(tokenIdentifier)) {
      return azureDevOpsService;
    } else if (githubService.containsToken(tokenIdentifier)) {
      return githubService;
    }
    // If a new project management provider should be implemented, it can just be
    // added here

    return null;
  }

  private String checkJiraUrl(String jiraUrl) {
    String atlassianSuffix = ".atlassian.net";
    if (!jiraUrl.contains(atlassianSuffix) || jiraUrl.startsWith(atlassianSuffix)) {
      LOGGER.warn("{} is not a valid Atlassian URL!", jiraUrl);
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, jiraUrl + " is not a valid Atlassian URL!");
    }
    if (jiraUrl.substring(0, 7).equalsIgnoreCase("http://")) jiraUrl = jiraUrl.substring(7);
    if (jiraUrl.endsWith("/")) jiraUrl = jiraUrl.substring(0, jiraUrl.length() - 1);
    if (!jiraUrl.contains("https://")) jiraUrl = "https://" + jiraUrl;
    return jiraUrl;
  }
}
