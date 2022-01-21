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
    const response = await axios.post(`${constants.backendURL}/projects`);
    return response.data;
  }
  public async getUserStoriesFromProject(project): Promise<any> {
    const response = await axios.post(`${constants.backendURL}/projects/${project}/issues`);
    return response.data;
  }
}

export default new ApiService();
