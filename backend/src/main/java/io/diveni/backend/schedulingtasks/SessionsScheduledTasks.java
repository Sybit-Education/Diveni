/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 Diveni Team, AUME-Team 21/22, HTWG Konstanz
*/
package io.diveni.backend.schedulingtasks;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.diveni.backend.service.DatabaseService;
import io.diveni.backend.service.projectmanagementproviders.azuredevops.AzureDevOpsService;
import io.diveni.backend.service.projectmanagementproviders.jira.cloud.JiraCloudService;
import io.diveni.backend.service.projectmanagementproviders.jira.server.JiraServerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SessionsScheduledTasks {

  private static final Logger LOGGER = LoggerFactory.getLogger(SessionsScheduledTasks.class);

  final DatabaseService databaseService;

  private JiraCloudService jiraCloudService;

  private JiraServerService jiraServerService;

  private AzureDevOpsService azureDevOpsService;

  public SessionsScheduledTasks(DatabaseService databaseService) {
    this.databaseService = databaseService;
  }

  @Scheduled(fixedDelay = 60, timeUnit = TimeUnit.MINUTES)
  public void deleteOldSessions() {
    LOGGER.info("--> deleteOldSessions()");
    Calendar c = Calendar.getInstance();
    c.setTime(new Date());
    c.add(Calendar.DATE, -2);

    databaseService.getSessions().stream()
        .filter(
            s ->
                s.getLastModified() != null
                    && s.getLastModified().getTime() < c.getTime().getTime())
        .forEach(session -> {
          removeSessionToken(session.getAccessToken());
          databaseService.deleteSession(session);
        });
    LOGGER.info("<-- deleteOldSessions()");
  }

  private void removeSessionToken(String token) {
    if (jiraCloudService.containsToken(token)) {
      jiraCloudService.getJiraConfigs().remove(token);
    } else if (jiraServerService.containsToken(token)) {
      jiraServerService.getJiraConfigs().remove(token);
    } else if (azureDevOpsService.containsToken(token)) {
      azureDevOpsService.getAccessTokens().remove(token);
      azureDevOpsService.getAccessTokenToProjectId().remove(token);
    } else {
      LOGGER.warn("Session with token={} was not found!", token);
    }
  }

  @Autowired
  public void setJiraCloudService(JiraCloudService jiraCloudService) {
    this.jiraCloudService = jiraCloudService;
  }

  @Autowired
  public void setJiraServerService(JiraServerService jiraServerService) {
    this.jiraServerService = jiraServerService;
  }

  @Autowired
  public void setAzureDevOpsService(AzureDevOpsService azureDevOpsService) {
    this.azureDevOpsService = azureDevOpsService;
  }
}
