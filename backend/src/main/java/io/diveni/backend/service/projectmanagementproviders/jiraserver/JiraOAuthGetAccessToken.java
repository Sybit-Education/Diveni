package io.diveni.backend.service.projectmanagementproviders.jiraserver;

import com.google.api.client.auth.oauth.OAuthGetAccessToken;

public class JiraOAuthGetAccessToken extends OAuthGetAccessToken {

  /**
   * @param authorizationServerUrl encoded authorization server URL
   */
  public JiraOAuthGetAccessToken(String authorizationServerUrl) {
    super(authorizationServerUrl);
    this.usePost = true;
  }
}
