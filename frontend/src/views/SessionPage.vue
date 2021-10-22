<template>
  <div>
    <h1 class="mt-3">
      {{ title }}
    </h1>
    <h4 class="mt-4">
      Share the code
      <b-link href="/session">XFFRGHT</b-link>
      with your team mates.
      <b-icon-unlock animation="throb"
                     font-scale="1.5"/>
    </h4>
    <b-container class="my-5">
      <b-row>
        <b-col
          cols="6"
          md="4"
          lg="3"
          xl="2"
        >
          <WaitingForMemberCard
            color="gold"
            name="Tom"
            icon=1
          />
        </b-col>
        <b-col
          cols="6"
          md="4"
          lg="3"
          xl="2"
        >
          <WaitingForMemberCard
            color="mediumpurple"
            name="John"
            icon=2
          />
        </b-col>
        <b-col
          cols="6"
          md="4"
          lg="3"
          xl="2"
        >
          <WaitingForMemberCard
            color="royalblue"
            name="Linda"
            icon=3
          />
        </b-col>
      </b-row>
      <b-row>
        <b-col class="text-center mt-5 mb-5">
          <b-button variant="success"
                    style="color: black; border-style: solid;
                    border-color: black; background: greenyellow">
            Start Planning
          </b-button>
        </b-col>
      </b-row>
      <b-row>
        <div class="mt-5 text-center">Icons erstellt von <a href="https://www.freepik.com" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/de/" title="Flaticon">www.flaticon.com</a></div>
      </b-row>
    </b-container>
  </div>
</template>

<script lang="ts">
import Vue from 'vue';
import { BootstrapVue, IconsPlugin } from 'bootstrap-vue';
import * as Constants from '../constants';
import WaitingForMemberCard from '../components/WaitingForMemberCard.vue';

Vue.use(IconsPlugin);

export default Vue.extend({
  name: 'LandingPage',
  components: {
    WaitingForMemberCard,
  },
  data() {
    return {
      title: 'Waiting for members ...',
    };
  },
  methods: {
    async sendCreateSessionRequest() {
      const url = Constants.default.backendURL + Constants.default.backendSessionRoute;
      try {
        const response = await this.axios.get(url);
        this.goToWaitingRoomPage(response.data as string);
      } catch (e) {
        console.error(`Response of ${url} is invalid: ${e}`);
      }
    },
    goToWaitingRoomPage(session: string) {
      this.$router.push({ path: '/waitingRoom', params: { session } });
    },
  },
});
</script>
