package io.diveni.backend.service.projectmanagementproviders;

import io.diveni.backend.model.JiraRequestToken;
import io.diveni.backend.model.TokenIdentifier;

public interface ProjectManagementProviderOAuth1 extends ProjectManagementProvider {
    public JiraRequestToken getRequestToken();

    public TokenIdentifier getAccessToken(String verificationCode, String requestToken);
}
