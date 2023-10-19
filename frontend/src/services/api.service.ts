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

  public async getJiraOauth1RequestToken(): Promise<JiraRequestTokenDto> {
    const response = await axios.get<JiraRequestTokenDto>(
      `${constants.backendURL}/issue-tracker/jira/oauth1/requestToken`
    );
    return response.data;
  }

  public async sendJiraOauth1VerificationCode(
    code: string,
    token: string
  ): Promise<JiraResponseCodeDto> {
    const response = await axios.post<JiraResponseCodeDto>(
      `${constants.backendURL}/issue-tracker/jira/oauth1/verificationCode`,
      {
        code,
        token,
      }
    );
    return response.data;
  }

  public async sendJiraOauth2AuthorizationCode(code: string): Promise<JiraResponseCodeDto> {
    const response = await axios.post<JiraResponseCodeDto>(
      `${constants.backendURL}/issue-tracker/jira/oauth2/authorizationCode`,
      {
        code,
      }
    );
    return response.data;
  }

  public async sendAzureOauth2AuthorizationCode(): Promise<JiraResponseCodeDto> {
    const response = await axios.post<JiraResponseCodeDto>(
      `${constants.backendURL}/issue-tracker/azure/oauth2/authorizationCode`
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

  public async getAllDiveniData() {
    const response = (await axios.get(constants.backendURL + constants.getDiveniAnalytics)).data as {
      amountOfAttendees: number;
      amountOfSessions: number;
      amountofAttendeesLastMonth: number;
      amountOfSessionsLastMonth: number;
      amountOfAttendeesCurrently: number;
      amountOfSessionsCurrently: number;
    };
    return response;
  }
}

export default new ApiService();
