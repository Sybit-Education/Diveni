package de.htwg.aume.service.jira;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.google.api.client.auth.oauth.OAuthAuthorizeTemporaryTokenUrl;
import com.google.api.client.auth.oauth.OAuthCredentialsResponse;
import com.google.api.client.auth.oauth.OAuthGetTemporaryToken;
import com.google.api.client.auth.oauth.OAuthHmacSigner;
import com.google.api.client.http.javanet.NetHttpTransport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.htwg.aume.model.JiraRequestToken;
import de.htwg.aume.model.Session;
import de.htwg.aume.repository.SessionRepository;




@Service
public class JiraServerService {
    private final String JIRA_HOME = "http://5.189.185.210";
    private final String CONSUMER_KEY = "OauthKey";
    private final String PRIVATE_KEY = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAOMj4i7WeH+0OYkgr7+fhc2qJbdaOUh4JmnHBnVqQMwpZDotsyr7HCWZXlvTjbQCr2NQxq424yEbFitcK1ZuNfS8zTp8vm4Ch4u/FTQBm5nPP3fM4FkLYzZQmQqnStirzUFrGSK4jtqS5wh+lfgXCPOp3mSoDp7KVizHSBQTBtXbAgMBAAECgYEAqx83og3WTm+bASJtBbLK/Xz4WUBR87UBS6Ozy/W2x5lPdz1CxFjWhcUb/5ZMJZf4RpxucoXLa/+aHiScSctSPZo7GgOUL1rEVi/POdht8w67N/KWA57VQy9n087MaMCOWf3ezfxcUMv4fzolgF3TxIMM+cYnFULydl8yzMGnBRECQQDzw1m1al/qYCfBtUeuQVd98i+QPLRvFcTCvo0848NT/Cl81iUCymr2LY/dvJEx2xJ3eC4xPw1UlIwt9RN7JQpFAkEA7oromA0e3igxyeQ7zUBBEdZLk7RbrNZ7FJ1ShdRqMcD3Ln+nwkLr0g9o/2XGkh2+EexyKEk4P4iwrIXoddlxnwJAYQ5Q86itE/bBHaF+LuWZXm5FfdqNxQUX2KpiNfJB3XizVB83kUrjF63AcHsaHI2rZqIVUkpWlmym+81uukNfOQJBALZejKRyo37EzAvF6dJppVW1t+IcqVniQAbqoASg+O9Az7lE70SdVR0rmuJnNQDQrFeXpU8Xa2FnZ2r+lVJEA5ECQQC6fPxR9WBpaJM2hfEBDFxaggbTNwoPA3uOLOGSTYkHXBZ98idjesfu3ZOq06IsVaLZEuIz/hIvvrdzjYRnugFA";
            

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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public String getAccessToken(String verificationCode, String requestToken){
        try {
            JiraOAuthClient jiraOAuthClient = new JiraOAuthClient(JIRA_HOME);
            String accessToken = jiraOAuthClient.getAccessToken(requestToken, verificationCode, CONSUMER_KEY, PRIVATE_KEY);
            return accessToken;
        } catch (Exception e) {
            return "";
        }
    }

}
