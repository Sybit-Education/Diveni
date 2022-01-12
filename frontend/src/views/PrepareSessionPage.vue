<template>
  <div>
    <b-container>
      <h1 class="my-5">
        {{ title }}
      </h1>
      <h4>1. Select a card set and its values</h4>
      <card-set-component class="mt-3" @selectedCardSetOptions="setCardSetOptions" />
      <h4 class="mt-3">2. Specify estimation duration</h4>
      <b-row class="mt-3 text-center">
        <b-col>
          <b-button variant="outline-secondary" @click="setTimerDown()"> - </b-button>
        </b-col>
        <b-col class="text-center">
          <h4>
            {{ timer == 0 ? "∞" : formatTimer }}
          </h4>
        </b-col>
        <b-col>
          <b-button variant="outline-secondary" @click="setTimerUp()"> + </b-button>
        </b-col>
      </b-row>
      <h4 class="mt-3">3. Secure with password</h4>
      <b-row class="mt-3">
        <b-col>
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
        :card-set="selectedCardSetOptions"
        :show-estimations="false"
        :initial-stories="userStories"
        @userStoriesChanged="onUserStoriesChanged($event)"
      />
      <b-tabs v-model="tabIndex" content-class="mt-3 tabs" fill>
        <b-tab
          class="tabs"
          title="Planning with Story Points"
          active
          :title-link-class="linkClass(0)"
        >
          <h5>Start your planning only with Story Points</h5>
        </b-tab>
        <b-tab title="Planning with User Stories" :title-link-class="linkClass(1)">
          <!--TODO: Implement session config with US-->
        </b-tab>
        <b-tab title="Planning with Jira" :title-link-class="linkClass(2)">
          <!--TODO: Implement session config with Jira-->
        </b-tab>
      </b-tabs>
      <b-button
        class="mt-5"
        variant="success"
        :disabled="buttonDisabled()"
        @click="sendCreateSessionRequest"
      >
        Start session
      </b-button>
    </b-container>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import Session from "../model/Session";
import Constants from "../constants";
import CardSetComponent from "../components/CardSetComponent.vue";
import UserStoriesSidebar from "../components/UserStoriesSidebar.vue";

export default Vue.extend({
  name: "PrepareSessionPage",
  components: {
    CardSetComponent,
    UserStoriesSidebar,
  },
  data() {
    return {
      title: "Prepare session",
      password: "",
      selectedCardSetOptions: [],
      timer: 30,
      warningWhenUnderZero: "",
      tabIndex: 0,
    };
  },
  computed: {
    userStories() {
      return this.$store.state.userStories;
    },
    formatTimer(): string {
      const minutes = Math.floor(this.timer / 60);
      const seconds = (this.timer % 60).toString().padStart(2, "0");
      return `${minutes}:${seconds}`;
    },
  },
  watch: {
    timer(newTimer) {
      if (newTimer <= 0) {
        this.warningWhenUnderZero = "Timer cannot be less than zero!";
      }
      if (newTimer > 0) {
        this.warningWhenUnderZero = "";
      }
    },
  },
  mounted() {
    this.$store.commit("setUserStories", { stories: [] });
  },
  methods: {
    linkClass(idx) {
      if (this.tabIndex === idx) {
        return ["bg-success", "text-light"];
      } else {
        return ["bg-light", "text-dark"];
      }
    },
    async sendCreateSessionRequest() {
      const url = Constants.backendURL + Constants.createSessionRoute;
      const sessionConfig = {
        set: this.selectedCardSetOptions,
        timerSeconds: this.timer,
        password: this.password === "" ? null : this.password,
        userStories: this.userStories,
      };
      try {
        const response = (await this.axios.post(url, sessionConfig)).data as {
          session: {
            sessionID: string;
            adminID: string;
            sessionConfig: {
              set: Array<string>;
              timerSeconds: number;
              userStories: Array<{
                title: string;
                description: string;
                estimation: string | null;
                isActive: false;
              }>;
            };
            sessionState: string;
          };
          adminCookie: string;
        };
        window.localStorage.setItem("adminCookie", response.adminCookie);
        this.goToSessionPage(response.session as Session);
      } catch (e) {
        console.error(`Response of ${url} is invalid: ${e}`);
      }
    },
    goToSessionPage(session: Session) {
      this.$router.push({
        name: "SessionPage",
        params: {
          sessionID: session.sessionID,
          adminID: session.adminID,
          timerSecondsString: this.timer.toString(),
          voteSetJson: JSON.stringify(session.sessionConfig.set),
          sessionState: session.sessionState,
        },
      });
    },
    setCardSetOptions($event) {
      this.selectedCardSetOptions = $event;
    },
    buttonDisabled() {
      return this.selectedCardSetOptions.length < 1;
    },
    onUserStoriesChanged(stories) {
      this.$store.commit("setUserStories", { stories });
    },
    setTimerUp() {
      if (this.timer === 4 * 60 + 15) {
        this.timer += 5;
      } else if (this.timer === 4 * 60 + 20) {
        this.timer += 10;
      } else {
        this.timer += 15;
      }
    },
    setTimerDown() {
      if (this.timer === 4 * 60 + 20) {
        this.timer -= 5;
      }
      if (this.timer > 0) {
        this.timer -= 15;
      }
    },
  },
});
</script>

<style scoped>
.tabs {
  margin-top: 5%;
}
</style>
