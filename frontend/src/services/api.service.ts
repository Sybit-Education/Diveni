import constants from "@/constants";
import { JiraRequestTokenDto, JiraResponseCodeDto } from "@/types";
import axios, { AxiosResponse } from "axios";

class ApiService {
  public async getJiraOauth1RequestToken(): Promise<JiraRequestTokenDto> {
    const response = await axios.get<JiraRequestTokenDto>(
      `${constants.backendURL}/jira/oauth1/requestToken`
    );
    return response.data;
  }

  public async sendJiraOauth1VerificationCode(
    code: string,
    token: string
  ): Promise<JiraResponseCodeDto> {
    const response = await axios.post<JiraResponseCodeDto>(
      `${constants.backendURL}/jira/oauth1/verificationCode`,
      {
        code,
        token,
      }
    );
    return response.data;
  }

  public async sendJiraOauth2AuthorizationCode(code: string): Promise<JiraResponseCodeDto> {
    const response = await axios.post<JiraResponseCodeDto>(
      `${constants.backendURL}/jira/oauth2/authorizationCode`,
      {
        code,
      }
    );
    return response.data;
  }

  public async getAllProjects(): Promise<unknown> {
    const response = await axios.get(`${constants.backendURL}/jira/projects`);
    return response.data;
  }

  public async getUserStoriesFromProject(project): Promise<unknown> {
    const response = await axios.get(`${constants.backendURL}/jira/projects/${project}/issues`);
    return response.data;
  }

  public async updateUserStory(story): Promise<unknown> {
    const response = await axios.put(`${constants.backendURL}/jira/issue`, story, {
      headers: {
        "Content-Type": "application/json",
      },
    });
    return response;
  }

  public async createUserStory(story, projectID): Promise<AxiosResponse> {
    const response = await axios.post(
      `${constants.backendURL}/jira/issue?projectID=${projectID}`,
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
    const response = await axios.delete(`${constants.backendURL}/jira/issue/${jiraId}`, {
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
