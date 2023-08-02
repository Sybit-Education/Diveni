package io.diveni.backend.service.projectmanagementproviders.jira.cloud;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JiraCloudService implements ProjectManagementProvider {
  private static final Logger LOGGER = LoggerFactory.getLogger(JiraCloudService.class);
  private boolean serviceEnabled = false;
  @Getter
  private final Map<String, String> accessTokens = new HashMap<>();
  @Getter
  private final Map<String, String> jiraUrls = new HashMap<>();

  @Value("${JIRA_CLOUD_CONSUMERKEY:OauthKey}")
  private String CONSUMER_KEY;

  @Value("${JIRA_CLOUD_PRIVATEKEY:#{null}}")
  private String PRIVATE_KEY;

  private final int JIRA_API_VERSION = 2;

  private final String JIRA_API_URL = "%s/rest/api/%d";

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

  /**
   * Authenticates to JIRA with given OAuthParameters and makes request to url
   *
   * @param parameters
   * @param jiraUrl
   * @return
   * @throws IOException
   */
  private HttpResponse getResponseFromUrl(
    OAuthParameters parameters, GenericUrl jiraUrl, String requestMethod, HttpContent content)
    throws IOException {
    HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory(parameters);
    HttpRequest request = requestFactory.buildRequest(requestMethod, jiraUrl, content);
    if (content == null)
      request.getHeaders().set("Content-Type", "application/json;charset=UTF-8");
    return request.execute();
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
      String accessToken =
        jiraOAuthClient.getAccessToken(requestToken, verificationCode, CONSUMER_KEY, PRIVATE_KEY);
      String id = Utils.generateRandomID();
      getAccessTokens().put(id, accessToken);
      getJiraUrls().put(id, jiraBaseUrl);
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
    LOGGER.debug("--> getProjects()");
    try {
      List<Project> projects = new ArrayList<>();
      String accessToken = accessTokens.get(tokenIdentifier);
      String jiraUrl = getJiraUrls().get(tokenIdentifier);
      JiraOAuthClient jiraOAuthClient = new JiraOAuthClient(jiraUrl);
      OAuthParameters parameters =
        jiraOAuthClient.getParameters(accessToken, CONSUMER_KEY, PRIVATE_KEY);
      HttpResponse response =
        getResponseFromUrl(parameters, new GenericUrl(getJiraUrl(jiraUrl) + "/project"), "GET", null);
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
      String accessToken = accessTokens.get(tokenIdentifier);
      String jiraUrl = jiraUrls.get(tokenIdentifier);
      JiraOAuthClient jiraOAuthClient = new JiraOAuthClient(jiraUrl);
      OAuthParameters parameters =
        jiraOAuthClient.getParameters(accessToken, CONSUMER_KEY, PRIVATE_KEY);
      HttpResponse response =
        getResponseFromUrl(
          parameters,
          new GenericUrl(
            getJiraUrl(jiraUrl)
              + "/search?jql=project='"
              + projectName
              + "' AND resolution = Unresolved "
              + "ORDER BY RANK ASC, updated DESC"
              + "&fields=summary,description,"
              + ESTIMATION_FIELD
              + "&maxResults=1000"),
          "GET",
          null);

      JsonNode node = new ObjectMapper().readTree(response.parseAsString());

      for (JsonNode jsonNode : node.path("issues")) {
        JsonNode fields = jsonNode.get("fields");
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
    Map<String, Map<String, Object>> content = new HashMap<>();
    Map<String, Object> fields = new HashMap<>();
    fields.put("reporter", Map.of("accountId", getCurrentUsername(tokenIdentifier)));
    fields.put("issuetype", Map.of("name", "Story"));
    fields.put("project", Map.of("id", projectID));
    fields.put("summary", story.getTitle());
    fields.put("description", story.getDescription());
    content.put("fields", fields);
    try {
      String jiraUrl = getJiraUrls().get(tokenIdentifier);
      JiraOAuthClient jiraOAuthClient = new JiraOAuthClient(jiraUrl);
      OAuthParameters parameters =
        jiraOAuthClient.getParameters(
          accessTokens.get(tokenIdentifier), CONSUMER_KEY, PRIVATE_KEY);
      HttpResponse response =
        getResponseFromUrl(
          parameters,
          new GenericUrl(getJiraUrl(jiraUrl) + "/issue"),
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
  public void updateIssue(String tokenIdentifier, UserStory story) {
    LOGGER.debug("--> updateIssue(), storyID={}", story.getId());
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
      String jiraUrl = getJiraUrls().get(tokenIdentifier);
      JiraOAuthClient jiraOAuthClient = new JiraOAuthClient(jiraUrl);
      OAuthParameters parameters =
        jiraOAuthClient.getParameters(
          accessTokens.get(tokenIdentifier), CONSUMER_KEY, PRIVATE_KEY);
      HttpResponse response =
        getResponseFromUrl(
          parameters,
          new GenericUrl(getJiraUrl(jiraUrl) + "/issue/" + story.getId()),
          "PUT",
          new JsonHttpContent(GsonFactory.getDefaultInstance(), content));

      LOGGER.debug("<-- updateIssue() {}", response.parseAsString());
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
      String jiraUrl = getJiraUrls().get(tokenIdentifier);
      JiraOAuthClient jiraOAuthClient = new JiraOAuthClient(jiraUrl);
      OAuthParameters parameters =
        jiraOAuthClient.getParameters(
          accessTokens.get(tokenIdentifier), CONSUMER_KEY, PRIVATE_KEY);
      HttpResponse response =
        getResponseFromUrl(
          parameters, new GenericUrl(getJiraUrl(jiraUrl) + "/issue/" + issueID), "DELETE", null);

      LOGGER.debug("<-- deleteIssue() {}", response.parseAsString());
    } catch (Exception e) {
      LOGGER.error("Deletion failed", e);
      throw new ResponseStatusException(
        HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.failedToDeleteIssueErrorMessage);
    }
  }

  @Override
  public boolean containsToken(String token) {
    return getAccessTokens().containsKey(token);
  }

  @Override
  public String getCurrentUsername(String tokenIdentifier) {
    LOGGER.debug("--> getCurrentUsername(), tokenIdentifier={}", tokenIdentifier);
    try {
      String jiraUrl = getJiraUrls().get(tokenIdentifier);
      JiraOAuthClient jiraOAuthClient = new JiraOAuthClient(jiraUrl);
      OAuthParameters parameters =
        jiraOAuthClient.getParameters(
          accessTokens.get(tokenIdentifier), CONSUMER_KEY, PRIVATE_KEY);
      HttpResponse response =
        getResponseFromUrl(
          parameters, new GenericUrl(getJiraUrl(jiraUrl) + "/myself"), "GET", null);
      String res = response.parseAsString();
      JsonNode node = new ObjectMapper().readTree(res);
      LOGGER.debug("<-- getCurrentUsername()");
      return node.path("accountId").asText();
    } catch (Exception e) {
      LOGGER.error("Failed to get current username!", e);
      throw new ResponseStatusException(
        HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.failedToRetrieveUsernameErrorMessage);
    }
  }

  private String getJiraUrl(String jiraUrl) {
    return String.format(JIRA_API_URL, jiraUrl, JIRA_API_VERSION);
  }
}
