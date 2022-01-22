package de.htwg.aume.service.projectmanagementproviders.jiracloud;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.util.Base64;

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

    private final String clientId = "0XeKG8gq0JWWAShLLUv6Y27Zh8B9pPt8";
    private final String clientSecret = "EJmAzc3yi1mXhlHEUx7kNDxa8VrbrH6NE79_839izauaWHGoBGnT9iRs90h9N1t9";

    @Getter
    private final Map<String, String> accessTokens = new HashMap<>();

    @Override
    public TokenIdentifier getAccessToken(String authorizationCode) {
        RestTemplate restTemplate = new RestTemplate();
        String credentials = clientId + ":" + clientSecret;
		String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));

        HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.add("Authorization", "Basic " + encodedCredentials);

		HttpEntity<String> request = new HttpEntity<String>(headers);

		String access_token_url = "https://auth.atlassian.com/oauth/token";
		access_token_url += "?code=" + authorizationCode;
		access_token_url += "&grant_type=authorization_code";
		access_token_url += "&redirect_uri=http://localhost:8080/#/jiraCallback";

		ResponseEntity<String> response = restTemplate.exchange(access_token_url, HttpMethod.POST, request, String.class);

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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<UserStory> getIssues(String tokenIdentifier, String projectName) {
        // TODO Auto-generated method stub
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

}
