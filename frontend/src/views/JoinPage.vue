<template>
  <div>
    <h1 class="my-5 mx-2">
      {{ title }}
    </h1>
    <b-container>
      <join-page-card
        :show-password="false"
        :color="hexColor"
        :animal-asset-name="avatarAnimalAssetName"
        :button-text="'GO'"
        @clicked="onClicked"
      />
    </b-container>
  </div>
</template>

<script lang="ts">
import Vue from 'vue';
import SockJS from 'sockjs-client';
import webstomp from 'webstomp-client';
import { v4 as uuidv4 } from 'uuid';
import JoinPageCard from '../components/JoinPageCard.vue';
import * as Constants from '../constants';

export default Vue.extend({
  name: 'JoinPage',
  components: {
    JoinPageCard,
  },
  data() {
    return {
      title: 'Join a meeting ...',
      webSocketConnected: false,
      memberID: uuidv4(),
      hexColor: Constants.default.getRandomPastelColor(),
      avatarAnimalAssetName: Constants.default.getRandomAvatarAnimalAssetName(),
    };
  },
  methods: {
    onClicked(event: any) {
      const data = event as {
        sessionID: string,
        password: string,
        name: string,
      };
      this.sendJoinSessionRequest(data);
    },
    async sendJoinSessionRequest(data: { sessionID: string, password: string, name: string}) {
      const url = Constants.default.backendURL + Constants.default.joinSessionRoute(data.sessionID);
      const member = {
        memberID: this.memberID,
        name: data.name,
        hexColor: this.hexColor,
        avatarAnimal: Constants.default.avatarAnimalAssetNameToBackendEnum(
          this.avatarAnimalAssetName,
        ),
        currentEstimation: null,
      };
      try {
        await this.axios.post(url, member);
        this.connect(data.sessionID);
      } catch (e) {
        console.error(`Response of ${url} is invalid: ${e}`);
      }
    },
    connect(sessionID: string) {
      if (this.webSocketConnected) {
        return;
      }
      const stompClient = webstomp.over(new SockJS(`${Constants.default.backendURL}/connect?sessionID=${sessionID}&memberID=${this.memberID}`));
      stompClient.connect({},
        (frame) => {
          this.webSocketConnected = true;
          stompClient.subscribe('/users/updates/messages', (message) => {
            console.log(message);
            console.log('Message came in');
          });
          stompClient.send(Constants.default.webSocketRegisterMemberRoute, '', {});
        },
        (error) => {
          console.log(error);
          this.webSocketConnected = false;
        });
    },
  },
});
</script>
