import constants from "@/constants";
import { JiraRequestTokenDto } from "@/types";
import axios from "axios";

class ApiService {
  public async getJiraRequestToken(): Promise<JiraRequestTokenDto> {
    const response = await axios.get<JiraRequestTokenDto>(
      `${constants.backendURL}/jira/oauth1/requestToken`
    );
    return response.data;
  }

  public async sendJiraVerificationCode(code: string, token: string): Promise<void> {
    await axios.post<void>(`${constants.backendURL}/jira/oauth1/verificationCode`, {
      code,
      token,
    });
  }
}

export default new ApiService();
