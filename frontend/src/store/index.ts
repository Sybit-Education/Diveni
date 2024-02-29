import SockJS from "sockjs-client";
import webstomp, { Client } from "webstomp-client";
import Constants from "../constants";
import { defineStore } from "pinia";
import Project from "@/model/Project";
import UserStory from "@/model/UserStory";
import { Notification } from "@/model/Notification";
import Member from "@/model/Member";
import AdminVote from "@/model/AdminVote";

export const useDiveniStore = defineStore("diveni-store", {
  state: () => {
    return {
      stompClient: undefined as Client | undefined,
      webSocketConnected: false,
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
    };
  },
  persist: {
    storage: localStorage, //Storage where we store our "store-state"
  },
  actions: {
    setMembers(members) {
      this.members = members;
    },
    connectToBackendWS(url) {
      this.stompClient = webstomp.over(new SockJS(url));
      if (process.env.NODE_ENV === "production") {
        // eslint-disable-next-line @typescript-eslint/no-empty-function
        this.stompClient.debug = () => {};
      }
      const connect_callback = () => {
        console.log("WebSocket Connected");
        this.webSocketConnected = true;
      };
      const error_callback = (error) => {
        console.error("Error connecting to the WebSocket");
        console.error(error);
        this.webSocketConnected = false;
      };
      /**
       * client.connect(headers, connectCallback, errorCallback);
       * More information here: https://jmesnil.net/stomp-websocket/doc/
       */
      this.stompClient.connect({}, connect_callback, error_callback);
    },
    disconnectFromBackendWS() {
      this.stompClient?.disconnect(() => {
        console.log("WebSocket Disconnected!");
        this.webSocketConnected = false;
      });
    },
    subscribeOnBackendWSMemberUpdates() {
      this.stompClient?.subscribe(Constants.webSocketMemberListenRoute, (frame) => {
        this.memberUpdates = this.memberUpdates.concat([frame.body]);
      });
    },
    subscribeOnBackendWSMemberUpdatesWithAutoReveal() {
      this.stompClient?.subscribe(Constants.webSocketMemberAutoRevealListenRoute, (frame) => {
        const splittedFrame = frame.body.split(" ");
        this.autoReveal = splittedFrame[1] === "true";
        this.memberUpdates = this.memberUpdates.concat([splittedFrame[0]]);
      });
    },
    subscribeOnBackendWSStoriesUpdated() {
      this.stompClient?.subscribe(Constants.webSocketMemberListenUserStoriesRoute, (frame) => {
        this.userStories = JSON.parse(frame.body);
      });
    },
    subscribeOnBackendWSStorySelected() {
      this.stompClient?.subscribe(Constants.webSocketSelectedUserStoryRoute, (frame) => {
        this.selectedUserStoryIndex = +frame.body;
      });
    },
    subscribeOnBackendWSAdminUpdate() {
      this.stompClient?.subscribe(Constants.webSocketMembersUpdatedRoute, (frame) => {
        console.log(`web socket admin receive update: message ${frame}`);
        this.members = JSON.parse(frame.body).members;
        this.highlightedMembers = JSON.parse(frame.body).highlightedMembers;
      });
    },
    subscribeOnBackendWSHostVoting() {
      this.stompClient?.subscribe(Constants.webSocketMemberListenHostVotingRoute, (frame) => {
        this.hostVoting = JSON.parse(frame.body);
      });
    },
    subscribeOnBackendWSHostEstimation() {
      this.stompClient?.subscribe(Constants.webSocketMembersUpdatedHostEstimation, (frame) => {
        this.hostEstimation = JSON.parse(frame.body);
      });
    },
    subscribeOnBackendWSTimerStart() {
      this.stompClient?.subscribe(Constants.webSocketTimerStartRoute, (frame) => {
        console.log(`Got timer start ${frame.body}`);
        this.timerTimestamp = frame.body;
      });
    },
    subscribeOnBackendWSNotify() {
      this.stompClient?.subscribe(Constants.websocketNotification, (frame) => {
        this.notifications = this.notifications.concat([JSON.parse(frame.body)]);
      });
    },
    sendViaBackendWS(endPoint: string, data?: string | undefined) {
      this.stompClient?.send(endPoint, data);
    },
    clearStore() {
      this.members = [];
      this.userStories = [];
      this.memberUpdates = [];
      this.notifications = [];
      this.webSocketConnected = false;
      this.stompClient = undefined;
    },
    clearStoreWithoutUserStories() {
      this.members = [];
      this.memberUpdates = [];
      this.notifications = [];
      this.webSocketConnected = false;
      this.stompClient = undefined;
    },
    setSelectedProject(project) {
      this.selectedProject = project;
    },
    setProjects(projects) {
      this.projects = projects;
    },
    setUserStories({ stories }) {
      this.userStories = stories;
    },
    setTokenId(tokenId) {
      this.tokenId = tokenId;
    },
    clearTokenId(state) {
      state.tokenId = undefined;
    },
  },
});
