package de.htwg.aume.service.projectmanagementproviders.jiraserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import de.htwg.aume.Utils;
import de.htwg.aume.controller.ErrorMessages;
import de.htwg.aume.model.JiraRequestToken;
import de.htwg.aume.model.TokenIdentifier;
import de.htwg.aume.model.UserStory;
import de.htwg.aume.service.projectmanagementproviders.ProjectManagementProviderOAuth1;
import lombok.Getter;
import lombok.val;

@Service
public class JiraServerService implements ProjectManagementProviderOAuth1 {
    @Value("${jira.server.jiraHome}")
    private String JIRA_HOME;
    @Value("${jira.server.consumerKey:OauthKey}")
    private String CONSUMER_KEY;
    @Value("${jira.server.privateKey}")
    private String PRIVATE_KEY;
    @Value("${jira.server.estimationField:customfield_10111}")
    private String ESTIMATION_FIELD;
    @Value("${jira.server.apiVersion:2}")
    private int JIRA_SERVER_API_VERSION;
    @Value("${jira.server.rankName:RANK}")
    private String RANK_NAME;

    @Getter
    private final Map<String, String> accessTokens = new HashMap<>();

    @Override
    public JiraRequestToken getRequestToken() {
        JiraRequestToken jiraRequestToken = new JiraRequestToken();

        try {
            JiraOAuthTokenFactory oAuthGetAccessTokenFactory = new JiraOAuthTokenFactory(JIRA_HOME);

            JiraOAuthGetTemporaryToken temporaryToken = oAuthGetAccessTokenFactory.getTemporaryToken(CONSUMER_KEY,
                    PRIVATE_KEY);
            String token = temporaryToken.execute().token;

            String authorizationUrl = JIRA_HOME + "/plugins/servlet/oauth/authorize";
            OAuthAuthorizeTemporaryTokenUrl authorizationURL = new OAuthAuthorizeTemporaryTokenUrl(authorizationUrl);
            authorizationURL.temporaryToken = token;

            jiraRequestToken.setToken(token);
            jiraRequestToken.setUrl(authorizationURL.toString());
            return jiraRequestToken;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    ErrorMessages.failedToRetrieveRequestTokenErrorMessage);
        }
    }

    @Override
    public TokenIdentifier getAccessToken(String verificationCode, String requestToken) {
        try {
            JiraOAuthClient jiraOAuthClient = new JiraOAuthClient(JIRA_HOME);
            val accessToken = jiraOAuthClient.getAccessToken(requestToken, verificationCode, CONSUMER_KEY, PRIVATE_KEY);
            val id = Utils.generateRandomID();
            accessTokens.put(id, accessToken);
            return new TokenIdentifier(id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    ErrorMessages.failedToRetrieveAccessTokenErrorMessage);
        }
    }

    @Override
    public List<String> getProjects(String tokenIdentifier) {
        try {
            List<String> projects = new ArrayList<>();
            val accessToken = accessTokens.get(tokenIdentifier);
            JiraOAuthClient jiraOAuthClient = new JiraOAuthClient(JIRA_HOME);
            OAuthParameters parameters = jiraOAuthClient.getParameters(accessToken, CONSUMER_KEY, PRIVATE_KEY);
            HttpResponse response = getResponseFromUrl(parameters,
                    new GenericUrl(getJiraUrl() + "/project"), "GET", null);
            ObjectNode[] node = new ObjectMapper().readValue(response.parseAsString(), ObjectNode[].class);

            for (ObjectNode objectNode : node) {
                if (objectNode.has("name")) {
                    projects.add(objectNode.get("name").asText());
                }
            }
            return projects;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    ErrorMessages.failedToRetrieveProjectsErrorMessage);
        }
    }

    @Override
    public List<UserStory> getIssues(String tokenIdentifier, String projectName) {
        try {
            List<UserStory> userStories = new ArrayList<>();
            val accessToken = accessTokens.get(tokenIdentifier);
            JiraOAuthClient jiraOAuthClient = new JiraOAuthClient(JIRA_HOME);
            OAuthParameters parameters = jiraOAuthClient.getParameters(accessToken, CONSUMER_KEY, PRIVATE_KEY);
            HttpResponse response = getResponseFromUrl(parameters, new GenericUrl(
                    getJiraUrl() + "/search?jql=project='" + projectName
                            + "' and status != done ORDER BY " + RANK_NAME + "&fields=summary,description," + ESTIMATION_FIELD
                            + "&maxResults=1000"),
                    "GET", null);
            // The reply from the Jira API is no correct JSON, therefore [ and ] have to be
            // added
            val json = "[" + response.parseAsString() + "]";

            ObjectNode[] node = new ObjectMapper().readValue(json, ObjectNode[].class);

            for (ObjectNode objectNode : node) {
                for (JsonNode jsonNode : objectNode.get("issues")) {
                    val fields = jsonNode.get("fields");
                    String estimation = null;
                    if (fields.get(ESTIMATION_FIELD)!= null){
                        estimation = fields.get(ESTIMATION_FIELD).asText();
                    }

                    if (estimation != null && estimation.endsWith(".0")) {
                        estimation = estimation.substring(0, estimation.length() - 2);
                    }
                    userStories.add(new UserStory(jsonNode.get("id").asText(), fields.get("summary").asText(),
                            fields.get("description").asText(), estimation, false));
                }
            }

            return userStories;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    ErrorMessages.failedToRetrieveProjectsErrorMessage);
        }
    }

    @Override
    public void updateIssue(String tokenIdentifier, UserStory story) {
        Map<String, Map<String, Object>> content = new HashMap<>();
        Map<String, Object> fields = new HashMap<>();
        fields.put("summary", story.getTitle());
        fields.put("description", story.getDescription());
        if (story.getEstimation() != null) {
            try {
                fields.put(ESTIMATION_FIELD, Double.parseDouble(story.getEstimation()));
            } catch (NumberFormatException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        ErrorMessages.failedToEditIssueErrorMessage);
            }
        }
        content.put("fields", fields);
        try {
            JiraOAuthClient jiraOAuthClient = new JiraOAuthClient(JIRA_HOME);
            OAuthParameters parameters = jiraOAuthClient.getParameters(accessTokens.get(tokenIdentifier),
                    CONSUMER_KEY, PRIVATE_KEY);
            HttpResponse response = getResponseFromUrl(parameters, new GenericUrl(
                    getJiraUrl() + "/issue/" + story.getJiraId()), "PUT",
                    new JsonHttpContent(GsonFactory.getDefaultInstance(), content));
            System.out.print(response.parseAsString());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    ErrorMessages.failedToEditIssueErrorMessage);
        }
    }

    @Override
    public void deleteIssue(String tokenIdentifier, String jiraID) {
        try {
            JiraOAuthClient jiraOAuthClient = new JiraOAuthClient(JIRA_HOME);
            OAuthParameters parameters = jiraOAuthClient.getParameters(accessTokens.get(tokenIdentifier),
                    CONSUMER_KEY, PRIVATE_KEY);
            HttpResponse response = getResponseFromUrl(parameters, new GenericUrl(
                    getJiraUrl() + "/issue/" + jiraID), "DELETE", null);

            System.out.print(response.parseAsString());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    ErrorMessages.failedToEditIssueErrorMessage);
        }
    }

    /**
     * Authanticates to JIRA with given OAuthParameters and makes request to url
     *
     * @param parameters
     * @param jiraUrl
     * @return
     * @throws IOException
     */
    private static HttpResponse getResponseFromUrl(OAuthParameters parameters, GenericUrl jiraUrl, String requestMethod,
            HttpContent content) throws IOException {
        HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory(parameters);
        HttpRequest request = requestFactory.buildRequest(requestMethod, jiraUrl, content);
        return request.execute();
    }

    @Override
    public boolean containsToken(String token) {
        return accessTokens.containsKey(token);
    }

    private String getJiraUrl() {
        return String.format("%s/rest/api/%d", JIRA_HOME,
                JIRA_SERVER_API_VERSION);
    }

}
