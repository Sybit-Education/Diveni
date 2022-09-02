/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 Diveni Team, AUME-Team 21/22, HTWG Konstanz
*/
package io.diveni.backend.service.projectmanagementproviders;

import io.diveni.backend.model.JiraRequestToken;
import io.diveni.backend.model.TokenIdentifier;

public interface ProjectManagementProviderOAuth1 extends ProjectManagementProvider {
  public JiraRequestToken getRequestToken();

  public TokenIdentifier getAccessToken(String verificationCode, String requestToken);
}
