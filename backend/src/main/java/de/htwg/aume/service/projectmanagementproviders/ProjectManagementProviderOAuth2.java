package de.htwg.aume.service.projectmanagementproviders;

import de.htwg.aume.model.TokenIdentifier;

public interface ProjectManagementProviderOAuth2 extends ProjectManagementProvider {
    public TokenIdentifier getAccessToken(String authorizationCode);
}
