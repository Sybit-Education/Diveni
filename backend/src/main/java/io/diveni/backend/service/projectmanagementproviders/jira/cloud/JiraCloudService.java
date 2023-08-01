package io.diveni.backend.service.projectmanagementproviders.jira.cloud;

import com.google.api.client.auth.oauth.OAuthAuthorizeTemporaryTokenUrl;
import io.diveni.backend.Utils;
import io.diveni.backend.controller.ErrorMessages;
import io.diveni.backend.model.JiraRequestToken;
import io.diveni.backend.model.Project;
import io.diveni.backend.model.TokenIdentifier;
import io.diveni.backend.model.UserStory;
import io.diveni.backend.service.projectmanagementproviders.ProjectManagementProvider;
import io.diveni.backend.service.projectmanagementproviders.jira.JiraOAuthClient;
import io.diveni.backend.service.projectmanagementproviders.jira.JiraOAuthGetTemporaryToken;
import io.diveni.backend.service.projectmanagementproviders.jira.JiraOAuthTokenFactory;
import lombok.Getter;
import lombok.val;
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

@Service
public class JiraCloudService implements ProjectManagementProvider {
  private static final Logger LOGGER = LoggerFactory.getLogger(JiraCloudService.class);
  private boolean serviceEnabled = false;
  @Getter
  private final Map<String, String> accessTokens = new HashMap<>();

  @Value("${JIRA_CLOUD_CONSUMERKEY:OauthKey}")
  private String CONSUMER_KEY;

  @Value("${JIRA_CLOUD_PRIVATEKEY:#{null}}")
  private String PRIVATE_KEY;

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

  public JiraRequestToken getRequestToken(String jiraBaseUrl) {
    LOGGER.debug("--> getRequestToken()");
    JiraRequestToken jiraRequestToken = new JiraRequestToken();

    try {
      JiraOAuthTokenFactory oAuthGetAccessTokenFactory = new JiraOAuthTokenFactory(jiraBaseUrl);

      JiraOAuthGetTemporaryToken temporaryToken =
        oAuthGetAccessTokenFactory.getTemporaryToken(CONSUMER_KEY, PRIVATE_KEY);
      String token = temporaryToken.execute().token;

      String authorizationUrl = jiraBaseUrl + "/plugins/servlet/oauth/authorize";
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

  public TokenIdentifier getAccessToken(String verificationCode, String requestToken, String jiraBaseUrl) {
    LOGGER.debug("--> getAccessToken(), verificationCode={}, requestToken={}", verificationCode, requestToken);
    try {
      JiraOAuthClient jiraOAuthClient = new JiraOAuthClient(jiraBaseUrl);
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
  public boolean serviceEnabled() {
    return serviceEnabled;
  }

  @Override
  public List<Project> getProjects(String tokenIdentifier) {
    return null;
  }

  @Override
  public List<UserStory> getIssues(String tokenIdentifier, String projectName) {
    return null;
  }

  @Override
  public void updateIssue(String tokenIdentifier, UserStory story) {

  }

  @Override
  public String createIssue(String tokenIdentifier, String projectID, UserStory story) {
    return null;
  }

  @Override
  public void deleteIssue(String tokenIdentifier, String issueID) {

  }

  @Override
  public boolean containsToken(String token) {
    return false;
  }

  @Override
  public String getCurrentUsername(String tokenIdentifier) {
    return null;
  }
}
