import constants from "@/constants";
import { JiraRequestTokenDto, JiraResponseCodeDto, PullRequestDto } from "@/types";
import axios, { AxiosResponse } from "axios";
import UserStory from "@/model/UserStory";

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

  public async sendGithubOauth2AuthorizaionCode(token: string): Promise<JiraResponseCodeDto> {
    const response = await axios.post(
      `${constants.backendURL}/issue-tracker/github/oauth2/accessToken`,
      {
        code: token,
      }
    );
    return response.data;
  }

  public async sendGitlabOauth2AuthorizationCode(patToken: string): Promise<JiraResponseCodeDto> {
    const response = await axios.post<JiraResponseCodeDto>(
      `${constants.backendURL}/issue-tracker/gitlab/oauth2/authorizationCode`,
      {
        code: patToken,
      }
    );
    return response.data;
  }

  public async getAllProjects(): Promise<unknown> {
    const response = await axios.get(`${constants.backendURL}/issue-tracker/projects`);
    return response.data;
  }

  public async getUserStoriesFromProject(project): Promise<unknown> {
    const response = await axios.get(
      `${constants.backendURL}/issue-tracker/projects/${project}/issues`
    );
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
    const response = (await axios.get(constants.backendURL + constants.getDiveniAnalytics))
      .data as {
      amountOfAttendees: number;
      amountOfSessions: number;
      amountOfAttendeesCurrently: number;
      amountOfSessionsCurrently: number;
    };
    return response;
  }

  public async getPullRequests(
    state,
    sort,
    direction,
    isMerged,
    page,
    pageSize
  ): Promise<PullRequestDto[]> {
    const queryParams = {
      state: state,
      is_merged: isMerged,
      per_page: pageSize,
      page: page,
      sort: sort,
      direction: direction,
    };
    const response = await axios.get(`${constants.backendURL}/news/pull-requests`, {
      headers: {
        "Content-Type": "application/json",
      },
      params: queryParams,
    });
    return response.data;
  }

  public async improveTitle(userStory: UserStory) {
    const response = await axios.post(`${constants.backendURL}/ai/improve-title`, {
      id: userStory.id,
      title: userStory.title,
      description: userStory.description,
      estimation: userStory.estimation,
      isActive: userStory.isActive,
    });
    return response;
  }

  public async retryImproveTitle(userStory: UserStory, oldTitle: string) {
    const response = await axios.post(`${constants.backendURL}/ai/improve-title`, {
      id: userStory.id,
      title: oldTitle,
      description: userStory.description,
      estimation: userStory.estimation,
      isActive: userStory.isActive,
    });
    return response;
  }
  public async improveDescription(userStory: UserStory, description: string) {
    const response = await axios.post(`${constants.backendURL}/ai/improve-description`, {
      id: userStory.id,
      title: userStory.title,
      description: description,
      estimation: userStory.estimation,
      isActive: userStory.isActive,
    });
    return {
      description: response.data.improved_description,
      acceptance_criteria: response.data.improved_acceptance_criteria,
    };

  }
  public async grammarCheck(userStory: UserStory, description: string) {

    const response = await axios.post(`${constants.backendURL}/ai/grammar-check`, {
      id: userStory.id,
      title: userStory.title,
      description: description,
      estimation: userStory.estimation,
      isActive: userStory.isActive,
    });
    return {
      description: response.data.improved_description,
      acceptance_criteria: response.data.improved_acceptance_criteria,
    };
  }
}

export default new ApiService();
