package de.htwg.aume.service.projectmanagementproviders;

import java.util.List;

import de.htwg.aume.model.JiraRequestToken;
import de.htwg.aume.model.TokenIdentifier;
import de.htwg.aume.model.UserStory;

public interface ProjectManagementProvider {
    public List<String> getProjects(String tokenIdentifier);
    public List<UserStory> getIssues(String tokenIdentifier, String projectName);
    public void updateIssue(String tokenIdentifier, UserStory story);
    public void deleteIssue(String tokenIdentifier, String jiraID);
    public boolean containsToken(String token);
}
