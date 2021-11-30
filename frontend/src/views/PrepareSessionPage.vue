<template>
  <div>
    <b-container>
      <h1 class="my-5">
        {{ title }}
      </h1>
      <b-row>
        <b-col>
          <h4>
            1. Select a card set and its values
          </h4>
        </b-col>
      </b-row>
      <card-set-component
        class="mt-3"
        @selectedCardSetOptions="setCardSetOptions"
      />
      <b-row class="mt-4">
        <b-col>
          <h4>
            2. Specify estimation duration
          </h4>
          <b-col>
            <b-button variant="outline-secondary" @click="setTimerDown()">
              -
            </b-button>
            {{ formatTimer }}
            <b-button variant="outline-secondary" @click="setTimerUp()">
              +
            </b-button>
            <span style="color:#FF0000">
            {{ warningWhenUnderZero }}
            </span>
          </b-col>
        </b-col>
      </b-row>
      <b-row>
        <b-col>
          <h4>
            3. Secure with password
          </h4>
          <b-form>
            <b-form-group label-for="input-password">
              <b-form-input
                id="input-password"
                v-model="password"
                placeholder="Password (leave empty for unprotected session)"
              />
            </b-form-group>
          </b-form>
        </b-col>
      </b-row>
      <user-stories-sidebar
        :card-set="['1', '2', 'A', '4', '5', '6']"
        :show-estimations="false"
        @userStoriesChanged="onUserStoriesChanged($event)"
      />
      <b-button class="mt-5" variant="success" :disabled="buttonDisabled()" @click="sendCreateSessionRequest">
        Start session
      </b-button>
    </b-container>
  </div>
</template>

<script lang="ts">
import Vue from 'vue';
import Session from '../model/Session';
import Constants from '../constants';
import CardSetComponent from '../components/CardSetComponent.vue';
import UserStoriesSidebar from '../components/UserStoriesSidebar.vue';

export default Vue.extend({
  name: 'PrepareSessionPage',
  components: {
    CardSetComponent,
    UserStoriesSidebar,
  },
  data() {
    return {
      title: 'Prepare session',
      password: '',
      selectedCardSetOptions: [],
      userStories: [],
      timer: 60,
      warningWhenUnderZero: '',
    };
  },
  watch: {
    timer(newTimer) {
      if (newTimer <= 0) {
        this.warningWhenUnderZero = 'Timer cannot be less than zero!';
      }
      if (newTimer > 0) {
        this.warningWhenUnderZero = '';
      }
    },
  },
  computed: {
    formatTimer() {
      const minutes = Math.floor(this.timer / 60);
      const seconds = `0${this.timer % 60}`.slice(-2);
      return `${minutes}:${seconds}`;
    },
  },
  methods: {
    async sendCreateSessionRequest() {
      const url = Constants.backendURL + Constants.createSessionRoute;
      const sessionConfig = {
        set: this.selectedCardSetOptions,
        password: this.password === '' ? null : this.password,
        userStories: this.userStories,
      };
      try {
        const session: Session = (await this.axios.post(url, sessionConfig)).data as {
          sessionID: string,
          adminID: string,
          membersID: string,
          sessionConfig: {
            set: Array<string>,
            userStories: Array<{title:string, description:string, estimation:string|null }>,
          },
        };
        this.goToSessionPage(session);
      } catch (e) {
        console.error(`Response of ${url} is invalid: ${e}`);
      }
    },
    goToSessionPage(session: Session) {
      this.$router.push({
        name: 'SessionPage',
        params: {
          sessionID: session.sessionID,
          adminID: session.adminID,
          voteSetJson: JSON.stringify(session.sessionConfig.set),
        },
      });
    },
    setCardSetOptions($event) {
      this.selectedCardSetOptions = $event;
    },
    buttonDisabled() {
      return this.selectedCardSetOptions.length < 1;
    },
    onUserStoriesChanged(newUserStories) {
      console.log('user stories changed', newUserStories);
      this.userStories = newUserStories;
    },
    setTimerUp() {
      console.log(this.timer);
      this.timer += 15;
    },
    setTimerDown() {
      this.timer -= 15;
    },
  },
});
</script>
