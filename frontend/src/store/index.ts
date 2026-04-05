import j2m from "jira2md";
import Constants from "../constants";
import { defineStore } from "pinia";
import Project from "@/model/Project";
import UserStory from "@/model/UserStory";
import { Notification } from "@/model/Notification";
import Member from "@/model/Member";
import AdminVote from "@/model/AdminVote";
import { webSocketService } from "@/services/WebSocketService";

export const useDiveniStore = defineStore("diveni-store", {
  state: () => ({
    memberUpdates: [] as string[],
    userStories: [] as UserStory[],
    members: [] as Member[],
    notifications: [] as Notification[],
    highlightedMembers: [],
    timerTimestamp: undefined as string | undefined,
    tokenId: undefined,
    projects: [],
    selectedProject: undefined as Project | undefined,
    selectedUserStoryIndex: null as number | null,
    hostEstimation: undefined as AdminVote | undefined,
    hostVoting: false,
    autoReveal: false,
    isJiraSelected: false,
  }),
  actions: {
    setMembers(members) {
      this.members = members;
    },
    connectToBackendWS(url: string) {
      webSocketService.connect(url);
    },
    async disconnectFromBackendWS() {
      await webSocketService.disconnect();
    },
    subscribeOnBackendWSMemberUpdates(): () => void {
      return webSocketService.subscribe(Constants.webSocketMemberListenRoute, (body) => {
        this.memberUpdates = this.memberUpdates.concat([body]);
      });
    },
    subscribeOnBackendWSMemberUpdatesWithAutoReveal(): () => void {
      return webSocketService.subscribe(Constants.webSocketMemberAutoRevealListenRoute, (body) => {
        const splittedFrame = body.split(" ");
        this.autoReveal = splittedFrame[1] === "true";
        this.memberUpdates = this.memberUpdates.concat([splittedFrame[0]]);
      });
    },
    subscribeOnBackendWSStoriesUpdated(): () => void {
      return webSocketService.subscribe(Constants.webSocketMemberListenUserStoriesRoute, (body) => {
        this.userStories = JSON.parse(body);
      });
    },
    subscribeOnBackendWSStorySelected(): () => void {
      return webSocketService.subscribe(Constants.webSocketSelectedUserStoryRoute, (body) => {
        this.selectedUserStoryIndex = +body;
      });
    },
    subscribeOnBackendWSAdminUpdate(): () => void {
      return webSocketService.subscribe(Constants.webSocketMembersUpdatedRoute, (body) => {
        const parsed = JSON.parse(body);
        this.members = parsed.members;
        this.highlightedMembers = parsed.highlightedMembers;
      });
    },
    subscribeOnBackendWSHostVoting(): () => void {
      return webSocketService.subscribe(Constants.webSocketMemberListenHostVotingRoute, (body) => {
        this.hostVoting = JSON.parse(body);
      });
    },
    subscribeOnBackendWSHostEstimation(): () => void {
      return webSocketService.subscribe(Constants.webSocketMembersUpdatedHostEstimation, (body) => {
        this.hostEstimation = JSON.parse(body);
      });
    },
    subscribeOnBackendWSTimerStart(): () => void {
      return webSocketService.subscribe(Constants.webSocketTimerStartRoute, (body) => {
        this.timerTimestamp = body;
      });
    },
    subscribeOnBackendWSNotify(): () => void {
      return webSocketService.subscribe(Constants.websocketNotification, (body) => {
        this.notifications = this.notifications.concat([JSON.parse(body)]);
      });
    },
    sendViaBackendWS(endPoint: string, data?: string | undefined) {
      webSocketService.publish(endPoint, data);
    },
    async clearStore() {
      this.members = [];
      this.userStories = [];
      this.memberUpdates = [];
      this.notifications = [];
      await webSocketService.disconnect();
    },
    async clearStoreWithoutUserStories() {
      this.members = [];
      this.memberUpdates = [];
      this.notifications = [];
      await webSocketService.disconnect();
    },
    setSelectedProject(project) {
      this.selectedProject = project;
    },
    setProjects(projects) {
      this.projects = projects;
    },
    setUserStories({ stories }) {
      if (this.isJiraSelected) {
        stories
          .filter((us) => us.description)
          .map((us) => (us.description = j2m.to_markdown(us.description)));
      }
      this.userStories = stories;
    },
    setTokenId(tokenId) {
      this.tokenId = tokenId;
    },
    clearTokenId() {
      this.tokenId = undefined;
    },
  },
});
