/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 Diveni Team, AUME-Team 21/22, HTWG Konstanz
*/
package io.diveni.backend.controller;

import java.util.List;

import io.diveni.backend.model.JiraRequestToken;
import io.diveni.backend.model.Project;
import io.diveni.backend.model.TokenIdentifier;
import io.diveni.backend.model.UserStory;
import io.diveni.backend.model.VerificationCode;
import io.diveni.backend.service.DatabaseService;
import io.diveni.backend.service.projectmanagementproviders.ProjectManagementProvider;
import io.diveni.backend.service.projectmanagementproviders.jiracloud.JiraCloudService;
import io.diveni.backend.service.projectmanagementproviders.jiraserver.JiraServerService;
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
@RequestMapping("/jira")
public class ProjectManagementController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProjectManagementController.class);

  @Autowired DatabaseService databaseService;

  @Autowired JiraServerService jiraServerService;

  @Autowired JiraCloudService jiraCloudService;

  private final String PROVIDER_NOT_ENABLED_MESSAGE = "The selected issue tracker is not enabled. Make sure to set all required parameters.";

  @GetMapping(value = "/oauth1/requestToken")
  public ResponseEntity<JiraRequestToken> getRequestToken() {
    LOGGER.debug("--> getRequestToken()");
    if (!jiraServerService.serviceEnabled()) {
      LOGGER.warn("Jira Server is not configured!");
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, PROVIDER_NOT_ENABLED_MESSAGE);
    }
    ResponseEntity<JiraRequestToken> response =
        new ResponseEntity<>(jiraServerService.getRequestToken(), HttpStatus.OK);
    LOGGER.debug("<-- getRequestToken()");
    return response;
  }

  @PostMapping(value = "/oauth1/verificationCode")
  public ResponseEntity<TokenIdentifier> getOauth1AccessToken(
      @RequestBody VerificationCode verificationCode) {
    LOGGER.debug("--> getOauth1AccessToken()");
    if (!jiraServerService.serviceEnabled()) {
      LOGGER.warn("Jira Server is not configured!");
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, PROVIDER_NOT_ENABLED_MESSAGE);
    }
    ResponseEntity<TokenIdentifier> response =
        new ResponseEntity<>(
            jiraServerService.getAccessToken(
                verificationCode.getCode(), verificationCode.getToken()),
            HttpStatus.OK);
    LOGGER.debug("<-- getOauth1AccessToken()");
    return response;
  }

  @PostMapping(value = "/oauth2/authorizationCode")
  public ResponseEntity<TokenIdentifier> getOAuth2AccessToken(
      @RequestHeader("Origin") String origin, @RequestBody VerificationCode authorizationCode) {
    LOGGER.debug("--> getOAuth2AccessToken(), origin={}", origin);
    if (!jiraCloudService.serviceEnabled()) {
      LOGGER.warn("Jira Cloud is not configured!");
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, PROVIDER_NOT_ENABLED_MESSAGE);
    }
    ResponseEntity<TokenIdentifier> response =
        new ResponseEntity<>(
            jiraCloudService.getAccessToken(authorizationCode.getCode(), origin), HttpStatus.OK);
    LOGGER.debug("<-- getOAuth2AccessToken()");
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
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, PROVIDER_NOT_ENABLED_MESSAGE);
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
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, PROVIDER_NOT_ENABLED_MESSAGE);
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
    LOGGER.debug("--> updateIssue(), userStoryId={}", userStory.getJiraId());
    val projectManagementProvider = getProjectManagementProvider(tokenIdentifier);

    if (projectManagementProvider == null) {
      LOGGER.error("Could not update issue!");
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update issue");
    } else if (!projectManagementProvider.serviceEnabled()) {
      LOGGER.warn("projectManagementProvider is not configured!");
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, PROVIDER_NOT_ENABLED_MESSAGE);
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
    LOGGER.debug(
        "--> createIssue(), projectID={}, userStoryId={}", projectID, userStory.getJiraId());

    val projectManagementProvider = getProjectManagementProvider(tokenIdentifier);

    if (projectManagementProvider == null) {
      LOGGER.error("Failed to create issue!");
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create issue");
    } else if (!projectManagementProvider.serviceEnabled()) {
      LOGGER.warn("projectManagementProvider is not configured!");
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, PROVIDER_NOT_ENABLED_MESSAGE);
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
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, PROVIDER_NOT_ENABLED_MESSAGE);
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
    }
    // If a new project management provider should be implemented, it can just be
    // added here

    return null;
  }
}
