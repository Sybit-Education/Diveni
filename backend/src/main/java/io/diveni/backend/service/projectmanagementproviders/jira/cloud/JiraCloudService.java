package io.diveni.backend.service.projectmanagementproviders.jira.cloud;

import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import io.diveni.backend.Utils;
import io.diveni.backend.controller.ErrorMessages;
import io.diveni.backend.model.JiraConfig;
import io.diveni.backend.model.JiraRequestToken;
import io.diveni.backend.model.Project;
import io.diveni.backend.model.TokenIdentifier;
import io.diveni.backend.model.UserStory;
import io.diveni.backend.service.projectmanagementproviders.ProjectManagementProviderOAuth1;
import io.diveni.backend.service.projectmanagementproviders.jira.JiraApiClient;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class JiraCloudService implements ProjectManagementProviderOAuth1 {
  private static final Logger LOGGER = LoggerFactory.getLogger(JiraCloudService.class);
  private boolean serviceEnabled = false;
  private final Map<String, String> jiraUrls = new HashMap<>();
  @Getter
  private final Map<String, JiraConfig> jiraConfigs = new HashMap<>();


  @Value("${JIRA_CLOUD_CONSUMERKEY:OauthKey}")
  private String CONSUMER_KEY;

  @Value("${JIRA_CLOUD_PRIVATEKEY:#{null}}")
  private String PRIVATE_KEY;

  private final String ESTIMATION_FIELD = "customfield_10016";

  @PostConstruct
  public void setupAndLogConfig() {
    if (CONSUMER_KEY != null
      && PRIVATE_KEY != null
    ) {
      serviceEnabled = true;
    }

    LOGGER.info("Jira-Server Service: (enabled:" + serviceEnabled + ")");

    LOGGER.info("    JIRA_SERVER_CONSUMERKEY={}", CONSUMER_KEY);
    LOGGER.info("    JIRA_SERVER_PRIVATEKEY={}", PRIVATE_KEY == null ? "null" : "********");
  }

  @Override
  public boolean serviceEnabled() {
    return serviceEnabled;
  }

  @Override
  public JiraRequestToken getRequestToken(Optional<String> url) {
    LOGGER.debug("--> getRequestToken()");
    if (url.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMessages.failedToRetrieveAccessTokenErrorMessage);
    }
    try {
      JiraRequestToken jiraRequestToken = JiraApiClient.getRequestToken(url.get(), CONSUMER_KEY, PRIVATE_KEY);
      jiraUrls.put(jiraRequestToken.getToken(), url.get());
      LOGGER.debug("<-- getRequestToken()");
      return jiraRequestToken;
    } catch (Exception e) {
      if (e instanceof HttpResponseException) {
        HttpResponseException ex = (HttpResponseException) e;
        if (ex.getStatusCode() == 401) {
          LOGGER.warn("Application Link is not set up correctly!");
          throw new ResponseStatusException(HttpStatus.PRECONDITION_REQUIRED, ErrorMessages.failedToAuthorizeToJiraCloud);
        }
      }
      LOGGER.error("Failed to get request token!", e);
      throw new ResponseStatusException(
        HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.failedToRetrieveRequestTokenErrorMessage);
    }
  }

  @Override
  public TokenIdentifier getAccessToken(String verificationCode, String requestToken) {
    LOGGER.debug("--> getAccessToken(), verificationCode={}, requestToken={}", verificationCode, requestToken);
    try {
      String jiraUrl = jiraUrls.remove(requestToken);
      String accessToken = JiraApiClient.getAccessToken(verificationCode, requestToken, jiraUrl, CONSUMER_KEY, PRIVATE_KEY);
      String id = Utils.generateRandomID();
      getJiraConfigs().put(id, new JiraConfig(jiraUrl, accessToken, CONSUMER_KEY, PRIVATE_KEY));
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
      List<Project> projects = JiraApiClient.getProjects(getJiraConfigs().get(tokenIdentifier));
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
      List<UserStory> userStories = JiraApiClient.getIssues(getJiraConfigs().get(tokenIdentifier), projectName, ESTIMATION_FIELD, "RANK");
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
      String id = JiraApiClient.createIssue(getJiraConfigs().get(tokenIdentifier), projectID, story);
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
      HttpResponse response = JiraApiClient.updateIssue(getJiraConfigs().get(tokenIdentifier), ESTIMATION_FIELD, story);
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
      HttpResponse response = JiraApiClient.deleteIssue(getJiraConfigs().get(tokenIdentifier), issueID);
      LOGGER.debug("<-- deleteIssue() {}", response.parseAsString());
    } catch (Exception e) {
      LOGGER.error("Deletion failed", e);
      throw new ResponseStatusException(
        HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.failedToDeleteIssueErrorMessage);
    }
  }

  @Override
  public boolean containsToken(String token) {
    return getJiraConfigs().containsKey(token);
  }

  @Override
  public String getCurrentUsername(String tokenIdentifier) {
    LOGGER.debug("--> getCurrentUsername(), tokenIdentifier={}", tokenIdentifier);
    try {
      String accountId = JiraApiClient.getCurrentUsername(getJiraConfigs().get(tokenIdentifier));
      LOGGER.debug("<-- getCurrentUsername()");
      return accountId;
    } catch (Exception e) {
      LOGGER.error("Failed to get current username!", e);
      throw new ResponseStatusException(
        HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.failedToRetrieveUsernameErrorMessage);
    }
  }
}
