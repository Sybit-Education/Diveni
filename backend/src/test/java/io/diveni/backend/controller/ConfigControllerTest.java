package io.diveni.backend.controller;

import io.diveni.backend.service.projectmanagementproviders.azuredevops.AzureDevOpsService;
import io.diveni.backend.service.projectmanagementproviders.github.GithubService;
import io.diveni.backend.service.projectmanagementproviders.jiracloud.JiraCloudService;
import io.diveni.backend.service.projectmanagementproviders.jiraserver.JiraServerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ConfigControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private JiraServerService jiraServerService;

  @Autowired private JiraCloudService jiraCloudService;

  @Autowired private AzureDevOpsService azureDevOpsService;

  @Autowired private GithubService githubService;

  @Test
  public void getLocale_returnsDE() throws Exception {
    this.mockMvc
        .perform(get("/config/locale"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.locale").value("de"));
  }

  @Test
  public void getIssueTrackerConfig_returnsConfig() throws Exception {
    this.mockMvc
        .perform(get("/config/issueTracker"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.isJiraServerEnabled")
                .value(Boolean.valueOf(jiraServerService.serviceEnabled()).toString()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.isJiraCloudEnabled")
                .value(Boolean.valueOf(jiraCloudService.serviceEnabled()).toString()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.jiraCloudAuthorizeUrl")
                .value(jiraCloudService.getJiraCloudAuthorizeUrl()))
        .andExpect(
            (MockMvcResultMatchers.jsonPath("$.isAzureDevOpsEnabled")
                .value(Boolean.valueOf(azureDevOpsService.serviceEnabled()).toString())))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.isGithubEnabled")
                .value(Boolean.valueOf(githubService.serviceEnabled()).toString()));
  }
}
