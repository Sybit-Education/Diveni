/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 Diveni Team, AUME-Team 21/22, HTWG Konstanz
*/
package io.diveni.backend.service.projectmanagementproviders.jira;

import com.google.api.client.auth.oauth.OAuthGetTemporaryToken;

public class JiraOAuthGetTemporaryToken extends OAuthGetTemporaryToken {

  /**
   * @param authorizationServerUrl encoded authorization server URL
   */
  public JiraOAuthGetTemporaryToken(String authorizationServerUrl) {
    super(authorizationServerUrl);
    this.usePost = true;
  }
}
