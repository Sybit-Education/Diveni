<template>
  <b-container class="main">
    <h1>
      {{ t("session.prepare.title") }}
    </h1>

    <h4 class="mt-2">
      <b-img
        v-if="theme === 'light'"
        :src="require('@/assets/preparePage/P1.png')"
        class="numberPictures"
      />
      <b-img v-else :src="require('@/assets/preparePage/P1D.png')" class="numberPictures" />
      {{ t("session.prepare.step.selection.mode.title") }}
    </h4>
    <b-tabs v-model="tabIndex" fill>
      <b-tab
        class="mt-2"
        :title="t('session.prepare.step.selection.mode.description.withoutUS.tab.label')"
        :title-link-class="linkClass(0)"
      >
        <story-points-component />
      </b-tab>
      <b-tab
        :title="t('session.prepare.step.selection.mode.description.withUS.tab.label')"
        :title-link-class="linkClass(1)"
      >
        <user-story-component class="mt-2" />
        <input
          id="fileUpload"
          type="file"
          hidden
          accept="text/csv"
          @change="importStory($event.target?.files)"
        />
        <b-button
          block
          elevation="2"
          class="btn-primary"
          variant="primary"
          @click="
            openFileUploader();
            $event.target.blur();
          "
        >
          {{ t("session.prepare.step.selection.mode.description.withUS.importButton") }}
        </b-button>
      </b-tab>
      <b-tab
        v-if="isIssueTrackerEnabled"
        :title="t('session.prepare.step.selection.mode.description.withIssueTracker.tab.label')"
        :title-link-class="linkClass(2)"
      >
        <issue-tracker-component class="mt-2" />
      </b-tab>
    </b-tabs>
    <h4 class="mt-4">
      <b-img
        v-if="theme === 'light'"
        :src="require('@/assets/preparePage/P2.png')"
        class="numberPictures"
      />
      <b-img v-else :src="require('@/assets/preparePage/P2D.png')" class="numberPictures" />
      {{ t("session.prepare.step.selection.cardSet.title") }}
    </h4>
    <card-set-component
      class="mt-2"
      :user-story-mode="userStoryMode"
      @selectedCardSetOptions="setCardSetOptions"
    />
    <h4 class="mt-4">
      <b-img
        v-if="theme === 'light'"
        :src="require('@/assets/preparePage/P3.png')"
        class="numberPictures"
      />
      <b-img v-else :src="require('@/assets/preparePage/P3D.png')" class="numberPictures" />
      {{ t("session.prepare.step.selection.time.title") }}
    </h4>
    <div class="settings-control">
      <b-button
        variant="primary"
        class="btn-sm btn-outline-light"
        @click="
          setTimerDown();
          $event.target.blur();
        "
      >
        -
      </b-button>
      <div id="setting-value" class="font-weight-bolder px-3 text-center">
        {{ timer == 0 ? "∞" : formatTimer }}
      </div>
      <b-button
        variant="primary"
        class="btn-sm btn-outline-light"
        @click="
          setTimerUp();
          $event.target.blur();
        "
      >
        +
      </b-button>
    </div>
    <h4 class="mt-5">
      <b-img
        v-if="theme === 'light'"
        :src="require('@/assets/preparePage/P4.png')"
        class="numberPictures"
      />
      <b-img v-else :src="require('@/assets/preparePage/P4D.png')" class="numberPictures" />
      {{ t("session.prepare.step.selection.hostVoting.title") }}
    </h4>
    <b-row class="mt-2">
      <b-col>
        <div class="settings-control">
          <b-button
            variant="primary"
            class="btn-sm btn-outline-light"
            @click="
              hostVoting = true;
              $event.target.blur();
            "
          >
            {{ t("session.prepare.step.selection.hostVoting.hostVotingOn") }}
          </b-button>
          <div id="setting-value" class="font-weight-bolder px-3 text-center">
            {{
              hostVoting
                ? t("session.prepare.step.selection.hostVoting.hostVotingOn")
                : t("session.prepare.step.selection.hostVoting.hostVotingOff")
            }}
          </div>
          <b-button
            variant="primary"
            class="btn-sm btn-outline-light"
            @click="
              hostVoting = false;
              $event.target.blur();
            "
          >
            {{ t("session.prepare.step.selection.hostVoting.hostVotingOff") }}
          </b-button>
        </div>
      </b-col>
    </b-row>
    <h4 class="mt-5">
      {{ t("session.prepare.step.selection.password.title") }}
    </h4>
    <b-row class="mt-2">
      <b-col>
        <b-form-input
          id="input-password"
          v-model="password"
          :placeholder="t('session.prepare.step.selection.password.placeholder')"
        />
      </b-col>
    </b-row>
    <b-button
      variant="primary"
      class="mt-5 mb-2"
      :disabled="buttonDisabled()"
      @click="sendCreateSessionRequest"
    >
      {{ t("session.prepare.button.start") }}
    </b-button>
  </b-container>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import Session from "../model/Session";
import Constants from "../constants";
import CardSetComponent from "../components/CardSetComponent.vue";
import UserStoryComponent from "../components/UserStoryComponent.vue";
import IssueTrackerComponent from "../components/IssueTrackerComponent.vue";
import StoryPointsComponent from "@/components/StoryPointsComponent.vue";
import UserStory from "@/model/UserStory";
import papaparse from "papaparse";
import apiService from "@/services/api.service";
import { useDiveniStore } from "@/store";
import { useToast } from "vue-toastification";
import { useI18n } from "vue-i18n";

export default defineComponent({
  name: "PrepareSessionPage",
  components: {
    CardSetComponent,
    UserStoryComponent,
    IssueTrackerComponent,
    StoryPointsComponent,
  },
  setup() {
    const store = useDiveniStore();
    const toast = useToast();
    const { t } = useI18n();
    return { store, toast, t };
  },
  data() {
    return {
      password: "",
      selectedCardSetOptions: [],
      timer: 30,
      warningWhenUnderZero: "",
      tabIndex: 0,
      hostVoting: false,
      isIssueTrackerEnabled: false,
      theme: localStorage.getItem("user-theme"),
    };
  },
  computed: {
    userStories() {
      return this.store.userStories;
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
    window.addEventListener("user-theme-localstorage-changed", (event) => {
      const customEvent = event as CustomEvent;
      this.theme = customEvent.detail.storage;
    });
    apiService.getIssueTrackerConfig().then((result) => {
      this.isIssueTrackerEnabled =
        result.isJiraCloudEnabled === "true" ||
        result.isJiraServerEnabled === "true" ||
        result.isAzureDevOpsEnabled === "true" ||
        result.isGithubEnabled === "true" ||
        result.isGitlabEnabled === "true";
    });
    this.store.setUserStories({ stories: [] });
  },
  methods: {
    linkClass(idx) {
      if (this.tabIndex === idx) {
        return ["selectedTab", "selectedTextColor"];
      } else {
        return ["notSelectedTab", "notSelectedTextColor"];
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
          hostVoting: string;
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
        state: {
          sessionID: session.sessionID,
          adminID: session.adminID,
          timerSecondsString: this.timer.toString(),
          voteSetJson: JSON.stringify(session.sessionConfig.set),
          sessionState: session.sessionState,
          userStoryMode: session.sessionConfig.userStoryMode,
          hostVoting: this.hostVoting,
          rejoined: "false",
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
      this.store.setUserStories({ stories });
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
          const stories: UserStory[] = [];

          file.data.forEach((story) => {
            const title = story.title ? story.title : story.Title;
            const description = story.description ? story.description : story.Description;
            const estimation = story.estimation ? story.estimation : story.Estimation;

            stories.push({
              id: null,
              title: title,
              description: description,
              estimation: estimation,
              isActive: false,
            });
          });
          this.store.setUserStories({ stories: stories });
          this.toast.success(
            this.t(
              "session.prepare.step.selection.mode.description.withUS.toastSuccessNotification"
            )
          );
        },
        error: () => {
          this.toast.error(
            this.t("session.prepare.step.selection.mode.description.withUS.toastErrorNotification")
          );
        },
      });
    },
  },
});
</script>

<style lang="scss" scoped>
@import "@/assets/style/_variables.scss";

.settings-control {
  display: flex;
  border-radius: $border-radius;
  background-color: var(--preparePageTimerBackground);
  font-size: 1.25rem;
  width: 12rem;
  height: 2rem;
  padding: 0;

  button {
    flex: auto;
    width: 2rem;
    height: 2rem;
    margin: 0;
    &:hover {
      color: var(--text-color-hover);
    }
  }

  .setting-value {
    flex: content;
    width: 5rem;
  }
}

.numberPictures {
  height: 45px;
  width: 45px;
}
</style>
