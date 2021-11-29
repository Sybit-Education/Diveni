import SockJS from 'sockjs-client';
import Vue from 'vue';
import Vuex from 'vuex';
import webstomp from 'webstomp-client';
import Constants from '../constants';

Vue.use(Vuex);

export default new Vuex.Store({
  state: {
    stompClient: undefined,
    webSocketConnected: false,
    memberUpdates: [],
    userStories: [],
    members: null,
  },
  mutations: {
    connectToBackendWS(state, url) {
      state.stompClient = webstomp.over(new SockJS(url));
      state.stompClient.connect({},
        (frame) => {
          state.webSocketConnected = true;
        },
        (error) => {
          console.error(error);
          state.webSocketConnected = false;
        });
    },
    subscribeOnBackendWSStartPlanningListenRoute(state) {
      state.stompClient.subscribe(
        Constants.webSocketMemberListenRoute, (frame) => {
          state.memberUpdates = state.memberUpdates.concat([frame.body]);
        },
      );
    },
    subscribeOnBackendWSAdminUpdate(state) {
      state.stompClient.subscribe(
        Constants.webSocketMembersUpdatedRoute, (frame) => {
          console.log(`web socket admin receive update: message ${frame}`);
          state.members = JSON.parse(frame.body);
        },
      );
    },
    sendViaBackendWS(state, { endPoint, data }) {
      state.stompClient.send(endPoint, data);
    },
    setUserStories(state, { stories }) {
      state.userStories = stories;
    },
  },
  actions: {
  },
  modules: {
  },
});
