import constants from "@/constants";
import { JiraRequestTokenDto } from "@/types";
import axios from "axios";

class ApiService {
  public async getJiraOauth1RequestToken(): Promise<JiraRequestTokenDto> {
    const response = await axios.get<JiraRequestTokenDto>(
      `${constants.backendURL}/jira/oauth1/requestToken`
    );
    return response.data;
  }

  public async sendJiraOauth1VerificationCode(code: string, token: string): Promise<void> {
    await axios.post<void>(`${constants.backendURL}/jira/oauth1/verificationCode`, {
      code,
      token,
    });
  }

  public async sendJiraOauth2AuthorizationCode(code: string): Promise<void> {
    await axios.post<void>(`${constants.backendURL}/jira/oauth2/authorizationCode`, {
      code,
    });
  }
}

export default new ApiService();
