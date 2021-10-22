<template>
  <div>
    <h1 class="mt-3">
      {{ title }}
    </h1>
    <h4 class="mt-4">
      Share the code
      <b-link
        href=""
        @click="copyCodeToClipboard"
      >
        {{ joinCode }}
      </b-link>
      with your team mates.
      <b-icon-unlock />
    </h4>
    <b-container class="my-5">
      <b-row class="d-flex justify-content-center">
        <SessionMemberCard
          v-for="member of members"
          :key="member.id"
          class="m-4"
          :color="member.color"
          :asset-name="member.assetName"
          :name="member.name"
        />
      </b-row>
      <b-row>
        <b-col class="text-center mt-5 mb-5">
          <success-button
            :button-text="'Start Planning'"
            :disabled="false"
            :on-click="() =>{}"
          />
        </b-col>
      </b-row>
    </b-container>
  </div>
</template>

<script lang="ts">
import Vue from 'vue';
import { BootstrapVue, IconsPlugin } from 'bootstrap-vue';
import SockJS from 'sockjs-client';
import webstomp from 'webstomp-client';
import * as Constants from '../constants';
import SessionMemberCard from '../components/SessionMemberCard.vue';
import SuccessButton from '../components/SuccessButton.vue';

Vue.use(IconsPlugin);

export default Vue.extend({
  name: 'LandingPage',
  components: {
    SessionMemberCard,
    SuccessButton,
  },
  data() {
    return {
      title: 'Waiting for members ...',
      joinCode: 'XFFRGHT',
      connected: false,
      stompClient: webstomp.over(new SockJS(`${Constants.default.backendURL}/connect`)),
      members: [
        {
          id: '1',
          color: 'mediumpurple',
          assetName: 'animal-icon-1.png',
          name: 'Esenosarumensemwonken',
        },
        {
          id: '2',
          color: 'green',
          assetName: 'animal-icon-2.png',
          name: 'Zellner',
        },
        {
          id: '3',
          color: 'red',
          assetName: 'animal-icon-3.png',
          name: 'Maximilian',
        },
        {
          id: '4',
          color: 'blue',
          assetName: 'animal-icon-4.png',
          name: 'Bahner',
        },
        {
          id: '5',
          color: 'mediumpurple',
          assetName: 'animal-icon-1.png',
          name: 'Esenosarumensemwonken',
        },
        {
          id: '6',
          color: 'green',
          assetName: 'animal-icon-2.png',
          name: 'Zellner',
        },
        {
          id: '7',
          color: 'red',
          assetName: 'animal-icon-3.png',
          name: 'Maximilian',
        },
      ],
    };
  },
  mounted() {
    this.connect();
    const msg = JSON.stringify({ name: 'John' });
    setTimeout(() => this.send(msg), 5000);
  },
  methods: {
    copyCodeToClipboard() {
      navigator.clipboard.writeText(this.joinCode).then(() => {
        console.log('Async: Copying to clipboard was successful!');
      }, (err) => {
        console.error('Async: Could not copy text: ', err);
      });
    },
    send(body: any) {
      if (this.stompClient && this.stompClient.connected) {
        this.stompClient.send('/ws/hello', body, {});
      }
    },
    connect() {
      this.stompClient.connect(
        {},
        (frame) => {
          this.connected = true;
          this.stompClient.subscribe('/admin/update', (tick) => {
            console.log(tick);
          });
        },
        (error) => {
          console.log(error);
          this.connected = false;
        },
      );
    },
    disconnect() {
      if (this.stompClient) {
        this.stompClient.disconnect();
      }
      this.connected = false;
    },
  },
});
</script>
