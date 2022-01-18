import { JiraRequestTokenDto } from "@/types";
import axios from "axios";

class ApiService {
  public async getJiraRequestToken(): Promise<JiraRequestTokenDto> {
    const response = await axios.get<JiraRequestTokenDto>(
      `${process.env.VUE_APP_SERVER_API_URL}/jira/oauth1/requestToken`
    );
    return response.data;
  }

  public async sendJiraVerificationCode(code: string): Promise<void> {
    await axios.post<void>(`${process.env.VUE_APP_SERVER_API_URL}/jira/oauth1/verificationCode`, {
      code,
    });
  }
}

export default new ApiService();
