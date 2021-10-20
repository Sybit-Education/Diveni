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
    <b-container class="mt-5">
      <b-row>
        <b-col sm="2">
          <WaitingForMemberCard
            color="gold"
            name="Tom"
            icon=1
          />
        </b-col>
        <b-col sm="2">
          <WaitingForMemberCard
              color="mediumpurple"
              name="John"
              icon=2
          />
        </b-col>
        <b-col sm="2">
          <WaitingForMemberCard
              color="royalblue"
              name="Linda"
              icon=3
          />
        </b-col>
      </b-row>
      <b-row>
        <b-col class="text-center mt-5">
          <b-button variant="success"
                    style="color: black; border-style: solid;
                    border-color: black; background: greenyellow">
            Start Planning
          </b-button>
        </b-col>
      </b-row>
    </b-container>
    <div style="position: absolute; bottom: 50px; right: 300px">Icons erstellt von <a href="https://www.freepik.com" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/de/" title="Flaticon">www.flaticon.com</a></div>
  </div>
</template>

<script lang="ts">
import Vue from 'vue';
import { BootstrapVue, IconsPlugin } from 'bootstrap-vue';
import * as Constants from '../constants';
import WaitingForMemberCard from '../components/WaitingForMemberCard.vue';

Vue.use(BootstrapVue);
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
