package io.diveni.backend.service.projectmanagementproviders.jira;

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
import io.diveni.backend.model.JiraConfig;
import io.diveni.backend.model.JiraRequestToken;
import io.diveni.backend.model.Project;
import io.diveni.backend.model.UserStory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JiraApiClient {

  private static final int JIRA_API_VERSION = 2;

  private static final String JIRA_API_URL = "%s/rest/api/%d";

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

  public JiraRequestToken getRequestToken(String jiraBaseUrl, String consumerKey, String privateKey) throws Exception {
    JiraRequestToken jiraRequestToken = new JiraRequestToken();

    JiraOAuthTokenFactory oAuthGetAccessTokenFactory = new JiraOAuthTokenFactory(jiraBaseUrl);

    JiraOAuthGetTemporaryToken temporaryToken =
      oAuthGetAccessTokenFactory.getTemporaryToken(consumerKey, privateKey);
    String token = temporaryToken.execute().token;

    String authorizationUrl = jiraBaseUrl + "/plugins/servlet/oauth/authorize";
    OAuthAuthorizeTemporaryTokenUrl authorizationURL =
      new OAuthAuthorizeTemporaryTokenUrl(authorizationUrl);
    authorizationURL.temporaryToken = token;

    jiraRequestToken.setToken(token);
    jiraRequestToken.setUrl(authorizationURL.toString());
    return jiraRequestToken;
  }

  public String getAccessToken(String verificationCode, String requestToken, String jiraBaseUrl, String consumerKey, String privateKey) throws Exception {
    JiraOAuthClient jiraOAuthClient = new JiraOAuthClient(jiraBaseUrl);
    return jiraOAuthClient.getAccessToken(requestToken, verificationCode, consumerKey, privateKey);
  }

  public List<Project> getProjects(JiraConfig config) throws Exception {
    List<Project> projects = new ArrayList<>();
    JiraOAuthClient jiraOAuthClient = new JiraOAuthClient(config.getJiraUrl());
    OAuthParameters parameters =
      jiraOAuthClient.getParameters(config.getAccessToken(), config.getConsumerKey(), config.getPrivateKey());
    HttpResponse response =
      getResponseFromUrl(parameters, new GenericUrl(getJiraUrl(config.getJiraUrl()) + "/project"), "GET", null);
    ObjectNode[] node =
      new ObjectMapper().readValue(response.parseAsString(), ObjectNode[].class);

    for (ObjectNode objectNode : node) {
      projects.add(new Project(objectNode.get("name").asText(), objectNode.get("id").asText()));
    }
    return projects;
  }

  public List<UserStory> getIssues(JiraConfig config, String projectName, String estimationField, String rank) throws Exception {
    List<UserStory> userStories = new ArrayList<>();
    JiraOAuthClient jiraOAuthClient = new JiraOAuthClient(config.getJiraUrl());
    OAuthParameters parameters =
      jiraOAuthClient.getParameters(config.getAccessToken(), config.getConsumerKey(), config.getPrivateKey());
    HttpResponse response =
      getResponseFromUrl(
        parameters,
        new GenericUrl(
          getJiraUrl(config.getJiraUrl())
            + "/search?jql=project='"
            + projectName
            + "' AND resolution = Unresolved "
            + "ORDER BY " + rank + " ASC, updated DESC"
            + "&fields=summary,description,"
            + estimationField
            + "&maxResults=1000"),
        "GET",
        null);

    JsonNode node = new ObjectMapper().readTree(response.parseAsString());

    for (JsonNode jsonNode : node.path("issues")) {
      JsonNode fields = jsonNode.get("fields");
      String estimation =
        (fields.get(estimationField) == null || fields.get(estimationField).isNull())
          ? null
          : String.valueOf(fields.get(estimationField).asDouble());
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

    return userStories;
  }

  public String createIssue(JiraConfig config, String projectID, UserStory story) throws Exception {
    Map<String, Map<String, Object>> content = new HashMap<>();
    Map<String, Object> fields = new HashMap<>();
    fields.put("reporter", Map.of("accountId", getCurrentUsername(config)));
    fields.put("issuetype", Map.of("name", "Story"));
    fields.put("project", Map.of("id", projectID));
    fields.put("summary", story.getTitle());
    fields.put("description", story.getDescription());
    content.put("fields", fields);
    JiraOAuthClient jiraOAuthClient = new JiraOAuthClient(config.getJiraUrl());
    OAuthParameters parameters =
      jiraOAuthClient.getParameters(
        config.getAccessToken(), config.getConsumerKey(), config.getPrivateKey());
    HttpResponse response =
      getResponseFromUrl(
        parameters,
        new GenericUrl(getJiraUrl(config.getJiraUrl()) + "/issue"),
        "POST",
        new JsonHttpContent(GsonFactory.getDefaultInstance(), content));

    JsonNode node = new ObjectMapper().readTree(response.parseAsString());
    return node.path("id").asText();
  }

  public HttpResponse updateIssue(JiraConfig config, String estimationField, UserStory story) throws Exception {
    Map<String, Map<String, Object>> content = new HashMap<>();
    Map<String, Object> fields = new HashMap<>();
    fields.put("summary", story.getTitle());
    fields.put("description", story.getDescription());
    if (story.getEstimation() != null) {
      fields.put(estimationField, Double.parseDouble(story.getEstimation()));
    }
    content.put("fields", fields);
    JiraOAuthClient jiraOAuthClient = new JiraOAuthClient(config.getJiraUrl());
    OAuthParameters parameters =
      jiraOAuthClient.getParameters(
        config.getAccessToken(), config.getConsumerKey(), config.getPrivateKey());
    return getResponseFromUrl(
      parameters,
      new GenericUrl(getJiraUrl(config.getJiraUrl()) + "/issue/" + story.getId()),
      "PUT",
      new JsonHttpContent(GsonFactory.getDefaultInstance(), content));
  }

  public HttpResponse deleteIssue(JiraConfig config, String issueID) throws Exception {
    JiraOAuthClient jiraOAuthClient = new JiraOAuthClient(config.getJiraUrl());
    OAuthParameters parameters =
      jiraOAuthClient.getParameters(
        config.getAccessToken(), config.getConsumerKey(), config.getPrivateKey());
    return getResponseFromUrl(
      parameters, new GenericUrl(getJiraUrl(config.getJiraUrl()) + "/issue/" + issueID), "DELETE", null);
  }

  public String getCurrentUsername(JiraConfig config) throws Exception {
    JiraOAuthClient jiraOAuthClient = new JiraOAuthClient(config.getJiraUrl());
    OAuthParameters parameters =
      jiraOAuthClient.getParameters(
        config.getAccessToken(), config.getConsumerKey(), config.getPrivateKey());
    HttpResponse response =
      getResponseFromUrl(
        parameters, new GenericUrl(getJiraUrl(config.getJiraUrl()) + "/myself"), "GET", null);
    String res = response.parseAsString();
    JsonNode node = new ObjectMapper().readTree(res);
    return node.path("accountId").asText();
  }

  private String getJiraUrl(String jiraUrl) {
    return String.format(JIRA_API_URL, jiraUrl, JIRA_API_VERSION);
  }
}
