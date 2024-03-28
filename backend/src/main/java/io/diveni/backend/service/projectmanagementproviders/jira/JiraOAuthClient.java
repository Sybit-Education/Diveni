/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 Diveni Team, AUME-Team 21/22, HTWG Konstanz
*/
package io.diveni.backend.service.projectmanagementproviders.jira;

import com.google.api.client.auth.oauth.OAuthCredentialsResponse;
import com.google.api.client.auth.oauth.OAuthParameters;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class JiraOAuthClient {

  public final String jiraBaseUrl;
  private final JiraOAuthTokenFactory oAuthGetAccessTokenFactory;

  public JiraOAuthClient(String jiraBaseUrl) throws Exception {
    this.jiraBaseUrl = jiraBaseUrl;
    this.oAuthGetAccessTokenFactory = new JiraOAuthTokenFactory(this.jiraBaseUrl);
  }

  /**
   * Gets access token from JIRA
   *
   * @param tmpToken temporary request token
   * @param secret secret (verification code provided by JIRA after request token authorization)
   * @param consumerKey consumer ey
   * @param privateKey private key in PKCS8 format
   * @return access token value
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeySpecException
   * @throws IOException
   */
  public String getAccessToken(
      String tmpToken, String secret, String consumerKey, String privateKey)
      throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
    JiraOAuthGetAccessToken oAuthAccessToken =
        oAuthGetAccessTokenFactory.getJiraOAuthGetAccessToken(
            tmpToken, secret, consumerKey, privateKey);
    OAuthCredentialsResponse response = oAuthAccessToken.execute();

    return response.token;
  }

  /**
   * Creates OAuthParameters used to make authorized request to JIRA
   *
   * @param tmpToken
   * @param consumerKey
   * @param privateKey
   * @return
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeySpecException
   */
  public OAuthParameters getParameters(String tmpToken, String consumerKey, String privateKey)
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    JiraOAuthGetAccessToken oAuthAccessToken =
        oAuthGetAccessTokenFactory.getJiraOAuthGetAccessToken(tmpToken, consumerKey, privateKey);
    return oAuthAccessToken.createParameters();
  }
}
