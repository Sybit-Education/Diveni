package de.htwg.aume.service.projectmanagementproviders;

import de.htwg.aume.model.JiraRequestToken;
import de.htwg.aume.model.TokenIdentifier;

public interface ProjectManagementProviderOAuth1 extends ProjectManagementProvider {
    public JiraRequestToken getRequestToken();
    public TokenIdentifier getAccessToken(String verificationCode, String requestToken);
}
