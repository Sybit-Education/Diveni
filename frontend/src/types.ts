import { Client } from "webstomp-client";
import Project from "./model/Project";

export interface StoreState {
  stompClient: Client | undefined;
  webSocketConnected: boolean;
  memberUpdates: string[];
  userStories: Record<string, unknown>[];
  members: Record<string, unknown>[];
  notifications: Record<string, unknown>[];
  highlightedMembers: Record<string, unknown>[];
  timerTimestamp: string | undefined;
  tokenId: string | undefined;
  projects: Record<string, unknown>[];
  selectedProject: Project | undefined;
  hostVoting: boolean;
  hostEstimation: string | undefined;
  selectedUserStoryIndex: number | undefined;
  autoReveal: boolean;
  isJiraSelected: boolean;
}

export interface JiraRequestTokenDto {
  token: string;
  url: string;
}

export interface JiraResponseCodeDto {
  tokenId: string;
}

export interface JiraRequestTokenDto {
  token: string;
  url: string;
}

export interface JiraRequestTokenDto {
  token: string;
  url: string;
}

export interface JiraRequestTokenDto {
  token: string;
  url: string;
}

export interface PullRequestDto {
  number: number;
  html_url: string;
  title: string;
  merged_at: string;
  user_type: string;
  updated_at: string;
}
