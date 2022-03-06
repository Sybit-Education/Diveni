package io.diveni.backend.service.projectmanagementproviders;

import io.diveni.backend.model.TokenIdentifier;

public interface ProjectManagementProviderOAuth2 extends ProjectManagementProvider {
    public TokenIdentifier getAccessToken(String authorizationCode, String origin);
}
