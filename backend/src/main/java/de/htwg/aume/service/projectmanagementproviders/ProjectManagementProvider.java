package de.htwg.aume.service.projectmanagementproviders;

import java.util.List;

import de.htwg.aume.model.Project;
import de.htwg.aume.model.UserStory;

public interface ProjectManagementProvider {
    public List<Project> getProjects(String tokenIdentifier);

    public List<UserStory> getIssues(String tokenIdentifier, String projectName);

    public void updateIssue(String tokenIdentifier, UserStory story);

    public String createIssue(String tokenIdentifier, String projectID, UserStory story);

    public void deleteIssue(String tokenIdentifier, String jiraID);

    public boolean containsToken(String token);

    public String getCurrentUsername(String tokenIdentifier);
}
