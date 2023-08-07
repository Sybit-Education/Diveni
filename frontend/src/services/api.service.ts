import constants from "@/constants";
import { JiraRequestTokenDto, JiraResponseCodeDto } from "@/types";
import axios, { AxiosResponse } from "axios";

class ApiService {
  public async getIssueTrackerConfig(): Promise<Record<string, string>> {
    const response = await axios.get<Record<string, string>>(
      `${constants.backendURL}/config/issueTracker`
    );
    return response.data;
  }

  public async getLocaleConfig(): Promise<Record<string, string>> {
    const response = await axios.get<Record<string, string>>(
      `${constants.backendURL}/config/locale`
    );
    return response.data;
  }

  public async getJiraServerRequestToken(): Promise<JiraRequestTokenDto> {
    const response = await axios.get<JiraRequestTokenDto>(
      `${constants.backendURL}/issue-tracker/jira/server/request-token`
    );
    return response.data;
  }

  public async sendJiraServerVerificationCode(
    code: string,
    token: string
  ): Promise<JiraResponseCodeDto> {
    const response = await axios.post<JiraResponseCodeDto>(
      `${constants.backendURL}/issue-tracker/jira/server/verification-code`,
      {
        code,
        token,
      }
    );
    return response.data;
  }

  public async getJiraCloudRequestToken(jiraBaseUrl: string): Promise<JiraRequestTokenDto> {
    const response = await axios.post<JiraRequestTokenDto>(
      `${constants.backendURL}/issue-tracker/jira/cloud/request-token?jira-url=${jiraBaseUrl}`
    );
    return response.data;
  }

  public async sendJiraCloudVerificationCode(
    code: string,
    token: string,
    jiraBaseUrl: string
  ): Promise<JiraResponseCodeDto> {
    const response = await axios.post<JiraResponseCodeDto>(
      `${constants.backendURL}/issue-tracker/jira/cloud/verification-code?jira-url=${jiraBaseUrl}`,
      {
        code,
        token,
      }
    );
    return response.data;
  }

  public async sendAzureOauth2AuthorizationCode(): Promise<JiraResponseCodeDto> {
    const response = await axios.post<JiraResponseCodeDto>(
      `${constants.backendURL}/issue-tracker/azure/cloud/authorization-code`
    );
    return response.data;
  }

  public async getAllProjects(): Promise<unknown> {
    const response = await axios.get(`${constants.backendURL}/issue-tracker/projects`);
    return response.data;
  }

  public async getUserStoriesFromProject(project): Promise<unknown> {
    const response = await axios.get(`${constants.backendURL}/issue-tracker/projects/${project}/issues`);
    return response.data;
  }

  public async updateUserStory(story): Promise<unknown> {
    const response = await axios.put(`${constants.backendURL}/issue-tracker/issue`, story, {
      headers: {
        "Content-Type": "application/json",
      },
    });
    return response;
  }

  public async createUserStory(story, projectID): Promise<AxiosResponse> {
    const response = await axios.post(
      `${constants.backendURL}/issue-tracker/issue?projectID=${projectID}`,
      story,
      {
        headers: {
          "Content-Type": "application/json",
        },
      }
    );
    return response;
  }

  public async deleteUserStory(jiraId): Promise<AxiosResponse> {
    const response = await axios.delete(`${constants.backendURL}/issue-tracker/issue/${jiraId}`, {
      headers: {
        "Content-Type": "application/json",
      },
    });
    return response;
  }
}

export default new ApiService();
