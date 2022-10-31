import { StoreState } from "@/types";
import SockJS from "sockjs-client";
import Vue from "vue";
import Vuex from "vuex";
import webstomp from "webstomp-client";
import Constants from "../constants";

export default new Vuex.Store<StoreState>({
  state: {
    stompClient: undefined,
    webSocketConnected: false,
    memberUpdates: [],
    userStories: [],
    members: [],
    notifications: [],
    highlightedMembers: [],
    timerTimestamp: undefined,
    tokenId: undefined,
    projects: [],
    selectedProject: undefined,
  },
  mutations: {
    setMembers(state, members) {
      state.members = members;
    },
    connectToBackendWS(state, url) {
      state.stompClient = webstomp.over(new SockJS(url));
      if (process.env.NODE_ENV === "production") {
        // eslint-disable-next-line @typescript-eslint/no-empty-function
        state.stompClient.debug = () => {};
      }
      state.stompClient.connect(
        {},
        () => {
          state.webSocketConnected = true;
        },
        (error) => {
          console.error(error);
          state.webSocketConnected = false;
        }
      );
    },
    subscribeOnBackendWSMemberUpdates(state) {
      state.stompClient?.subscribe(Constants.webSocketMemberListenRoute, (frame) => {
        state.memberUpdates = state.memberUpdates.concat([frame.body]);
      });
    },
    subscribeOnBackendWSStoriesUpdated(state) {
      state.stompClient?.subscribe(Constants.webSocketMemberListenUserStoriesRoute, (frame) => {
        state.userStories = JSON.parse(frame.body);
      });
    },
    subscribeOnBackendWSAdminUpdate(state) {
      state.stompClient?.subscribe(Constants.webSocketMembersUpdatedRoute, (frame) => {
        console.log(`web socket admin receive update: message ${frame}`);
        state.members = JSON.parse(frame.body).members;
        state.highlightedMembers = JSON.parse(frame.body).highlightedMembers;
      });
    },
    subscribeOnBackendWSTimerStart(state) {
      state.stompClient?.subscribe(Constants.webSocketTimerStartRoute, (frame) => {
        console.log(`Got timer start ${frame.body}`);
        state.timerTimestamp = frame.body;
      });
    },
    subscribeOnBackendWSNotify(state) {
      state.stompClient?.subscribe(Constants.websocketNotification, (frame) => {
        state.notifications = state.notifications.concat([JSON.parse(frame.body)]);
      });
    },
    sendViaBackendWS(state, { endPoint, data }) {
      state.stompClient?.send(endPoint, data);
    },
    clearStore(state) {
      state.members = [];
      state.userStories = [];
      state.memberUpdates = [];
      state.notifications = [];
      state.webSocketConnected = false;
      state.stompClient = undefined;
    },
    setSelectedProject(state, project) {
      state.selectedProject = project;
    },
    setProjects(state, projects) {
      state.projects = projects;
    },
    setUserStories(state, { stories }) {
      state.userStories = stories;
    },
    setTokenId(state, tokenId) {
      state.tokenId = tokenId;
    },
  },
  actions: {},
  modules: {},
});
