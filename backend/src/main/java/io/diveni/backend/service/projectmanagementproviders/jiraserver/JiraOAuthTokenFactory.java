/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 Diveni Team, AUME-Team 21/22, HTWG Konstanz
*/
package io.diveni.backend.service.projectmanagementproviders.jiraserver;

import com.google.api.client.auth.oauth.OAuthRsaSigner;
import com.google.api.client.http.javanet.NetHttpTransport;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import java.util.Base64;


public class JiraOAuthTokenFactory {
  protected final String accessTokenUrl;
  protected final String requestTokenUrl;

  public JiraOAuthTokenFactory(String jiraBaseUrl) {
    this.accessTokenUrl = jiraBaseUrl + "/plugins/servlet/oauth/access-token";
    requestTokenUrl = jiraBaseUrl + "/plugins/servlet/oauth/request-token";
  }

  /**
   * Initialize JiraOAuthGetAccessToken by setting it to use POST method, secret, request token and
   * setting consumer and private keys.
   *
   * @param tmpToken request token
   * @param secret secret (verification code provided by JIRA after request token authorization)
   * @param consumerKey consumer ey
   * @param privateKey private key in PKCS8 format
   * @return JiraOAuthGetAccessToken request
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeySpecException
   */
  public JiraOAuthGetAccessToken getJiraOAuthGetAccessToken(
      String tmpToken, String secret, String consumerKey, String privateKey)
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    JiraOAuthGetAccessToken accessToken = new JiraOAuthGetAccessToken(accessTokenUrl);
    accessToken.consumerKey = consumerKey;
    accessToken.signer = getOAuthRsaSigner(privateKey);
    accessToken.transport = new NetHttpTransport();
    accessToken.verifier = secret;
    accessToken.temporaryToken = tmpToken;
    return accessToken;
  }

  /**
   * Initialize JiraOAuthGetAccessToken by setting it to use POST method, secret, request token and
   * setting consumer and private keys.
   *
   * @param tmpToken request token
   * @param consumerKey consumer ey
   * @param privateKey private key in PKCS8 format
   * @return JiraOAuthGetAccessToken request
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeySpecException
   */
  public JiraOAuthGetAccessToken getJiraOAuthGetAccessToken(
      String tmpToken, String consumerKey, String privateKey)
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    JiraOAuthGetAccessToken accessToken = new JiraOAuthGetAccessToken(accessTokenUrl);
    accessToken.consumerKey = consumerKey;
    accessToken.signer = getOAuthRsaSigner(privateKey);
    accessToken.transport = new NetHttpTransport();
    accessToken.temporaryToken = tmpToken;
    return accessToken;
  }

  /**
   * Initialize JiraOAuthGetTemporaryToken by setting it to use POST method, oob (Out of Band)
   * callback and setting consumer and private keys.
   *
   * @param consumerKey consumer key
   * @param privateKey private key in PKCS8 format
   * @return JiraOAuthGetTemporaryToken request
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeySpecException
   */
  public JiraOAuthGetTemporaryToken getTemporaryToken(String consumerKey, String privateKey)
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    JiraOAuthGetTemporaryToken oAuthGetTemporaryToken =
        new JiraOAuthGetTemporaryToken(requestTokenUrl);
    oAuthGetTemporaryToken.consumerKey = consumerKey;
    oAuthGetTemporaryToken.signer = getOAuthRsaSigner(privateKey);
    oAuthGetTemporaryToken.transport = new NetHttpTransport();
    oAuthGetTemporaryToken.callback = "oob";
    return oAuthGetTemporaryToken;
  }

  /**
   * @param privateKey private key in PKCS8 format
   * @return OAuthRsaSigner
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeySpecException
   */
  private OAuthRsaSigner getOAuthRsaSigner(String privateKey)
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    OAuthRsaSigner oAuthRsaSigner = new OAuthRsaSigner();
    oAuthRsaSigner.privateKey = getPrivateKey(privateKey);
    return oAuthRsaSigner;
  }

  /**
   * Creates PrivateKey from string
   *
   * @param privateKey private key in PKCS8 format
   * @return private key
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeySpecException
   */
  private PrivateKey getPrivateKey(String privateKey)
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    byte[] privateBytes = Base64.getDecoder().decode(privateKey);
    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateBytes);
    KeyFactory kf = KeyFactory.getInstance("RSA");
    return kf.generatePrivate(keySpec);
  }
}
