package de.htwg.aume.service.projectmanagementproviders.jiracloud;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.api.client.util.Base64;

import org.apache.catalina.connector.Response;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import de.htwg.aume.Utils;
import de.htwg.aume.controller.ErrorMessages;
import de.htwg.aume.model.TokenIdentifier;
import de.htwg.aume.model.UserStory;
import de.htwg.aume.service.projectmanagementproviders.ProjectManagementProviderOAuth2;
import lombok.Getter;
import lombok.val;

@Service
public class JiraCloudService implements ProjectManagementProviderOAuth2 {

    private static final String clientId = "0XeKG8gq0JWWAShLLUv6Y27Zh8B9pPt8";
    private static final String clientSecret = "EJmAzc3yi1mXhlHEUx7kNDxa8VrbrH6NE79_839izauaWHGoBGnT9iRs90h9N1t9";

    private static final String JIRA_OAUTH_URL = "https://auth.atlassian.com/oauth";
    private static final int JIRA_CLOUD_API_VERSION = 2;
    private static final String JIRA_HOME = "https://api.atlassian.com/ex/jira/%s/rest/api/" + JIRA_CLOUD_API_VERSION;
    private static final String ESTIMATION_FIELD = "customfield_10016";

    @Getter
    private final Map<String, String> accessTokens = new HashMap<>();

    @Override
    public TokenIdentifier getAccessToken(String authorizationCode, String origin) {
        RestTemplate restTemplate = new RestTemplate();
        String credentials = clientId + ":" + clientSecret;
        String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.add("Authorization", "Basic " + encodedCredentials);

        HttpEntity<String> request = new HttpEntity<String>(headers);

        String accessTokenURL = JIRA_OAUTH_URL + "/token";
        accessTokenURL += "?code=" + authorizationCode;
        accessTokenURL += "&grant_type=authorization_code";
        accessTokenURL += "&redirect_uri=" + origin + "/#/jiraCallback";

        ResponseEntity<String> response = restTemplate.exchange(accessTokenURL, HttpMethod.POST, request, String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node;
        try {
            node = mapper.readTree(response.getBody());
            String accessToken = node.path("access_token").asText();

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
        val accessToken = accessTokens.get(tokenIdentifier);
        String cloudID = getCloudID(accessToken);
        try {
            List<String> projects = new ArrayList<>();
            ResponseEntity<String> response = executeRequest(String.format(JIRA_HOME, cloudID) + "/project/search",
                    HttpMethod.GET, accessToken);
            JsonNode node = new ObjectMapper().readTree(response.getBody());

            for (JsonNode projectNode : node.path("values")) {
                projects.add(projectNode.get("name").asText());
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
        String cloudID = getCloudID(accessTokens.get(tokenIdentifier));
        ResponseEntity<String> response = executeRequest(String.format(JIRA_HOME, cloudID) + "/search", HttpMethod.GET,
                accessTokens.get(tokenIdentifier));
        try {
            Object node = new ObjectMapper().readValue(response.getBody(), Object.class);
            // String json = new
            // ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(response.getBody());
            System.out.println(
                    new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(node));
        } catch (Exception e) {
            e.printStackTrace();
        }
        // System.out.println(response.getBody());
        return null;
    }

    @Override
    public void updateIssue(String tokenIdentifier, UserStory story) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteIssue(String tokenIdentifier, String jiraID) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean containsToken(String token) {
        return accessTokens.containsKey(token);
    }

    static String getCloudID(String accessToken) {
        String accessibleResourcesURL = "https://api.atlassian.com/oauth/token/accessible-resources";
        ResponseEntity<String> response = executeRequest(accessibleResourcesURL, HttpMethod.GET, accessToken);
        try {
            ObjectNode[] node = new ObjectMapper().readValue(response.getBody(), ObjectNode[].class);
            for (ObjectNode objectNode : node) {
                if (objectNode.has("id")) {
                    return objectNode.get("id").asText();
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    ErrorMessages.failedToRetrieveAccessTokenErrorMessage);
        }
    }

    static ResponseEntity<String> executeRequest(String url, HttpMethod method, String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.add("Authorization", "Bearer " + accessToken);
        HttpEntity<String> request = new HttpEntity<String>(headers);
        return restTemplate.exchange(url, method, request, String.class);
    }

}
