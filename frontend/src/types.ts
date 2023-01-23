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
  team: string | undefined;
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
