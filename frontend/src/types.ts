import { Client } from "webstomp-client";

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
