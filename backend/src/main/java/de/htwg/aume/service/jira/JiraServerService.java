package de.htwg.aume.service.jira;


import java.util.Map;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.api.client.auth.oauth.OAuthAuthorizeTemporaryTokenUrl;
import com.google.api.client.auth.oauth.OAuthParameters;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import de.htwg.aume.controller.ErrorMessages;
import de.htwg.aume.model.JiraRequestToken;
import de.htwg.aume.model.Project;
import de.htwg.aume.model.TokenIdentifier;
import de.htwg.aume.Utils;

import lombok.val;
import lombok.Getter;

@Service
public class JiraServerService {
    private final String JIRA_HOME = "http://5.189.185.210";
    private final String CONSUMER_KEY = "OauthKey";
    private final String PRIVATE_KEY = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAOMj4i7WeH+0OYkgr7+fhc2qJbdaOUh4JmnHBnVqQMwpZDotsyr7HCWZXlvTjbQCr2NQxq424yEbFitcK1ZuNfS8zTp8vm4Ch4u/FTQBm5nPP3fM4FkLYzZQmQqnStirzUFrGSK4jtqS5wh+lfgXCPOp3mSoDp7KVizHSBQTBtXbAgMBAAECgYEAqx83og3WTm+bASJtBbLK/Xz4WUBR87UBS6Ozy/W2x5lPdz1CxFjWhcUb/5ZMJZf4RpxucoXLa/+aHiScSctSPZo7GgOUL1rEVi/POdht8w67N/KWA57VQy9n087MaMCOWf3ezfxcUMv4fzolgF3TxIMM+cYnFULydl8yzMGnBRECQQDzw1m1al/qYCfBtUeuQVd98i+QPLRvFcTCvo0848NT/Cl81iUCymr2LY/dvJEx2xJ3eC4xPw1UlIwt9RN7JQpFAkEA7oromA0e3igxyeQ7zUBBEdZLk7RbrNZ7FJ1ShdRqMcD3Ln+nwkLr0g9o/2XGkh2+EexyKEk4P4iwrIXoddlxnwJAYQ5Q86itE/bBHaF+LuWZXm5FfdqNxQUX2KpiNfJB3XizVB83kUrjF63AcHsaHI2rZqIVUkpWlmym+81uukNfOQJBALZejKRyo37EzAvF6dJppVW1t+IcqVniQAbqoASg+O9Az7lE70SdVR0rmuJnNQDQrFeXpU8Xa2FnZ2r+lVJEA5ECQQC6fPxR9WBpaJM2hfEBDFxaggbTNwoPA3uOLOGSTYkHXBZ98idjesfu3ZOq06IsVaLZEuIz/hIvvrdzjYRnugFA";
    
    @Getter
    private final Map<String, String> accessTokens = new HashMap<>();

    public JiraRequestToken getRequestToken(){
        JiraRequestToken jiraRequestToken = new JiraRequestToken();

        try {
            JiraOAuthTokenFactory oAuthGetAccessTokenFactory=new JiraOAuthTokenFactory(JIRA_HOME);

            JiraOAuthGetTemporaryToken temporaryToken = oAuthGetAccessTokenFactory.getTemporaryToken(CONSUMER_KEY, PRIVATE_KEY);
            String token = temporaryToken.execute().token;

            String authorizationUrl = JIRA_HOME + "/plugins/servlet/oauth/authorize";
            OAuthAuthorizeTemporaryTokenUrl authorizationURL = new OAuthAuthorizeTemporaryTokenUrl(authorizationUrl);
            authorizationURL.temporaryToken = token;

            jiraRequestToken.setToken(token);
            jiraRequestToken.setUrl(authorizationURL.toString());
            return jiraRequestToken;
        } catch (Exception e) {
            e.printStackTrace();
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.failedToRetrieveRequestTokenErrorMessage);
        }
    }

    public TokenIdentifier getAccessToken(String verificationCode, String requestToken){
        try {
            JiraOAuthClient jiraOAuthClient = new JiraOAuthClient(JIRA_HOME);
            val accessToken = jiraOAuthClient.getAccessToken(requestToken, verificationCode, CONSUMER_KEY, PRIVATE_KEY);
            val id = Utils.generateRandomID();
            accessTokens.put(id, accessToken);
            return new TokenIdentifier(accessToken);
        } catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.failedToRetrieveAccessTokenErrorMessage);
        }
    }

    public List<Project> getProjects(String tokenIdentifier){
        try {
            List<Project> projects = new ArrayList<>();
            //val accessToken = accessTokens.get(tokenIdentifier);
            JiraOAuthClient jiraOAuthClient = new JiraOAuthClient(JIRA_HOME);
            OAuthParameters parameters = jiraOAuthClient.getParameters(tokenIdentifier, CONSUMER_KEY, PRIVATE_KEY);
            HttpResponse response = getResponseFromUrl(parameters, new GenericUrl(JIRA_HOME + "/rest/api/latest/project"));
            ObjectNode[] node = new ObjectMapper().readValue(response.parseAsString(), ObjectNode[].class);

            for (ObjectNode objectNode : node) {
                if (objectNode.has("name")){
                    projects.add(new Project(objectNode.get("name").asText()));
                }
            }
       
            return projects;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.failedToRetrieveProjectsErrorMessage);
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
    private static HttpResponse getResponseFromUrl(OAuthParameters parameters, GenericUrl jiraUrl) throws IOException {
        HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory(parameters);
        HttpRequest request = requestFactory.buildGetRequest(jiraUrl);
        return request.execute();
    }

}
