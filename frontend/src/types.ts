import { Client } from "webstomp-client";

export interface StoreState {
  stompClient: Client | undefined;
  webSocketConnected: boolean;
  memberUpdates: string[];
  userStories: Record<string, unknown>[];
  members: Record<string, unknown>[];
  highlightedMembers: Record<string, unknown>[];
}
