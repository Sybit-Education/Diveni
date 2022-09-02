/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 Diveni Team, AUME-Team 21/22, HTWG Konstanz
*/
package io.diveni.backend.service.projectmanagementproviders;

import java.util.List;

import io.diveni.backend.model.Project;
import io.diveni.backend.model.UserStory;

public interface ProjectManagementProvider {
  public List<Project> getProjects(String tokenIdentifier);

  public List<UserStory> getIssues(String tokenIdentifier, String projectName);

  public void updateIssue(String tokenIdentifier, UserStory story);

  public String createIssue(String tokenIdentifier, String projectID, UserStory story);

  public void deleteIssue(String tokenIdentifier, String jiraID);

  public boolean containsToken(String token);

  public String getCurrentUsername(String tokenIdentifier);
}
