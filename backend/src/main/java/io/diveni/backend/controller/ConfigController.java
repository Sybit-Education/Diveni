package io.diveni.backend.controller;

import io.diveni.backend.service.projectmanagementproviders.azuredevops.AzureDevOpsService;
import io.diveni.backend.service.projectmanagementproviders.github.GithubService;
import io.diveni.backend.service.projectmanagementproviders.jira.cloud.JiraCloudService;
import io.diveni.backend.service.projectmanagementproviders.jira.server.JiraServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/config")
public class ConfigController {

  private JiraServerService jiraServerService;

  private JiraCloudService jiraCloudService;

  private AzureDevOpsService azureDevOpsService;

  @Autowired private GithubService githubService;

  @Value("${LOCALE:en}")
  private String LOCALE;

  @GetMapping("/issue-tracker")
  public Map<String, String> getIssueTrackerConfig() {
    Map<String, String> issueTrackerConfig = new HashMap<>();
    issueTrackerConfig.put(
        "isJiraServerEnabled", Boolean.toString(jiraServerService.serviceEnabled()));
    issueTrackerConfig.put(
        "isJiraCloudEnabled", Boolean.toString(jiraCloudService.serviceEnabled()));
    issueTrackerConfig.put(
        "isAzureDevOpsEnabled", Boolean.toString(azureDevOpsService.serviceEnabled()));
    issueTrackerConfig.put("isGithubEnabled", Boolean.toString(githubService.serviceEnabled()));
    return issueTrackerConfig;
  }

  @GetMapping("/locale")
  public Map<String, String> getLocale() {
    Map<String, String> localeConfig = new HashMap<>();
    localeConfig.put("locale", LOCALE);
    return localeConfig;
  }

  @Autowired
  public void setJiraServerService(JiraServerService jiraServerService) {
    this.jiraServerService = jiraServerService;
  }

  @Autowired
  public void setJiraCloudService(JiraCloudService jiraCloudService) {
    this.jiraCloudService = jiraCloudService;
  }

  @Autowired
  public void setAzureDevOpsService(AzureDevOpsService azureDevOpsService) {
    this.azureDevOpsService = azureDevOpsService;
  }
}
