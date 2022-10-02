/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 Diveni Team, AUME-Team 21/22, HTWG Konstanz
*/
package io.diveni.backend.service.projectmanagementproviders;

import io.diveni.backend.model.TokenIdentifier;

public interface ProjectManagementProviderOAuth2 extends ProjectManagementProvider {
  TokenIdentifier getAccessToken(String authorizationCode, String origin);
}
