<template>
  <b-container id="prepare-session-page">
    <h1 class="my-5">
      {{ $t("session.prepare.title") }}
    </h1>
    <h4 class="mt-3">
      {{ $t("session.prepare.step.selection.mode.title") }}
    </h4>
    <b-tabs v-model="tabIndex" content-class="mt-3" fill>
      <b-tab
        class="mg_top_2_per"
        :title="
          $t(
            'session.prepare.step.selection.mode.description.withoutUS.tab.label'
          )
        "
        :title-link-class="linkClass(0)"
      >
        <stroy-points-component />
      </b-tab>
      <b-tab
        :title="
          $t('session.prepare.step.selection.mode.description.withUS.tab.label')
        "
        :title-link-class="linkClass(1)"
      >
        <user-story-component class="mg_top_2_per" />
        <input
          id="fileUpload"
          type="file"
          hidden
          accept="text/csv"
          @change="importStory($event.target.files)"
        />
        <b-button
          block
          color="primary"
          elevation="2"
          @click="openFileUploader()"
        >
          {{
            $t(
              "session.prepare.step.selection.mode.description.withUS.importButton"
            )
          }}
        </b-button>
      </b-tab>
      <b-tab
        v-if="isJiraEnabled"
        :title="
          $t(
            'session.prepare.step.selection.mode.description.withJira.tab.label'
          )
        "
        :title-link-class="linkClass(2)"
      >
        <jira-component class="mg_top_2_per" />
      </b-tab>
    </b-tabs>
    <h4 class="mt-4">
      {{ $t("session.prepare.step.selection.cardSet.title") }}
    </h4>
    <card-set-component
      class="mt-3"
      :user-story-mode="userStoryMode"
      @selectedCardSetOptions="setCardSetOptions"
    />
    <h4 class="mt-3">
      {{ $t("session.prepare.step.selection.time.title") }}
    </h4>
    <b-row class="mt-3 text-center">
      <b-col>
        <b-button variant="outline-secondary" @click="setTimerDown()">
          -</b-button
        >
      </b-col>
      <b-col class="text-center">
        <h4>
          {{ timer == 0 ? "∞" : formatTimer }}
        </h4>
      </b-col>
      <b-col>
        <b-button variant="outline-secondary" @click="setTimerUp()">
          +</b-button
        >
      </b-col>
    </b-row>
    <h4 class="mt-3">
      {{ $t("session.prepare.step.selection.password.title") }}
    </h4>
    <b-row class="mt-3">
      <b-col>
        <b-form>
          <b-form-group label-for="input-password">
            <b-form-input
              id="input-password"
              v-model="password"
              :placeholder="
                $t('session.prepare.step.selection.password.placeholder')
              "
            />
          </b-form-group>
        </b-form>
      </b-col>
    </b-row>
    <b-button
      class="my-5"
      variant="success"
      :disabled="buttonDisabled()"
      @click="sendCreateSessionRequest"
    >
      {{ $t("session.prepare.button.start") }}
    </b-button>
  </b-container>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import Session from "../model/Session";
import Constants from "../constants";
import CardSetComponent from "../components/CardSetComponent.vue";
import UserStoryComponent from "../components/UserStoryComponent.vue";
import JiraComponent from "../components/JiraComponent.vue";
import StroyPointsComponent from "@/components/StroyPointsComponent.vue";
import { useToast } from "vue-toastification";
import constants from "../constants";
import UserStory from "@/model/UserStory";
import papaparse from "papaparse";

export default defineComponent({
  name: "PrepareSessionPage",
  components: {
    CardSetComponent,
    UserStoryComponent,
    JiraComponent,
    StroyPointsComponent,
  },
  data() {
    return {
      password: "",
      selectedCardSetOptions: [],
      timer: 30,
      warningWhenUnderZero: "",
      tabIndex: 0,
      isJiraEnabled: constants.isJiraEnabled,
    };
  },
  computed: {
    userStories() {
      return this.$store.state.userStories;
    },
    userStoryMode(): string {
      return ["NO_US", "US_MANUALLY", "US_JIRA"][this.tabIndex];
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
  created() {
    const parsedTabIndex = parseInt(this.$route.query.tabIndex + "", 10);
    this.tabIndex = isNaN(parsedTabIndex) ? 0 : parsedTabIndex;
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
        userStoryMode: this.userStoryMode,
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
              userStoryMode: string;
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
          userStoryMode: session.sessionConfig.userStoryMode,
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
    openFileUploader() {
      const fileUpload = document.getElementById("fileUpload");
      if (fileUpload != null) {
        fileUpload.click();
      }
    },
    importStory(files: FileList) {
      if (!files || !files[0]) {
        return;
      }

      papaparse.parse(files[0], {
        header: true,
        delimiter: ";",
        complete: (file: { data }) => {
          let stories: UserStory[] = [];

          file.data.forEach((story) => {
            let title = story.title ? story.title : story.Title;
            let description = story.description
              ? story.description
              : story.Description;
            let estimation = story.estimation
              ? story.estimation
              : story.Estimation;

            stories.push({
              jiraId: null,
              title: title,
              description: description,
              estimation: estimation,
              isActive: false,
            });
          });
          this.$store.commit("setUserStories", {
            stories: stories,
          });
          useToast().success(
            this.$t(
              "session.prepare.step.selection.mode.description.withUS.toastSuccessNotification"
            )
          );
        },
        error: () => {
          useToast().error(
            this.$t(
              "session.prepare.step.selection.mode.description.withUS.toastErrorNotification"
            )
          );
        },
      });
    },
  },
});
</script>

<style scoped>
.mg_top_2_per {
  margin-top: 2%;
}
</style>
