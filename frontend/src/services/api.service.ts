import constants from "@/constants";
import { JiraRequestTokenDto, JiraResponseCodeDto } from "@/types";
import axios from "axios";

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

  public async getAllProjects(): Promise<any> {
    const response = await axios.get(`${constants.backendURL}/jira/projects`);
    return response.data;
  }

  public async getUserStoriesFromProject(project): Promise<any> {
    const response = await axios.get(`${constants.backendURL}/jira/projects/${project}/issues`);
    return response.data;
  }

  public async updateUserStory(story): Promise<any> {
    const response = await axios.put(`${constants.backendURL}/jira/issue`, story, {
      headers: {
        "Content-Type": "application/json",
      },
    });
    return response;
  }

  public async deleteUserStory(jiraId): Promise<any> {
    const response = await axios.delete(`${constants.backendURL}/jira/issue/${jiraId}`, {
      headers: {
        "Content-Type": "application/json",
      },
    });
    return response;
  }
}

export default new ApiService();
