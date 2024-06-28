<template>
  <b-container class="main">
    <h1>{{ t("session.prepare.title") }}</h1>

    <Steppy
      v-model:step="step"
      :finalize="sendCreateSessionRequest"
      :tabs="tabs"
      :circle-size="40"
      :primary-color1="'var(--preparePage-wizard-status)'"
      :primary-color2="'var(--preparePage-wizard-statusbackground)'"
      :background-color="'var(--preparePage-wizard-background)'"
      :back-text="t('session.prepare.step.wizard.wizardBack')"
      :next-text="t('session.prepare.step.wizard.wizardNext')"
      :done-text="t('session.prepare.step.wizard.wizardDone')"
    >
      <template #1>
        <div class="wizardStep">
          <h4 class="mb-3">
            <b-img
              v-if="theme === 'light'"
              :src="require('@/assets/preparePage/P1.png')"
              class="numberPictures"
            />
            <b-img v-else :src="require('@/assets/preparePage/P1D.png')" class="numberPictures" />
            {{ t("session.prepare.step.selection.mode.title") }}
          </h4>
          <div class="mode-icons d-flex justify-content-around">
            <button
              type="button"
              :class="['mode-icon', tabIndex === 0 ? 'active' : '']"
              @click="setTabIndex(0)"
            >
              <b-img
                v-if="theme === 'light'"
                :src="require('@/assets/preparePage/Mode1-light.png')"
                class="modeIconImage"
                :class="{ active: tabIndex === 0 }"
              />
              <b-img
                v-else
                :src="require('@/assets/preparePage/Mode1-dark.png')"
                class="modeIconImage"
                :class="{ active: tabIndex === 0 }"
              />
              <span class="mode-icon-text">{{
                t("session.prepare.step.selection.mode.description.withoutUS.tab.label")
              }}</span>
            </button>
            <button
              type="button"
              :class="['mode-icon', tabIndex === 1 ? 'active' : '']"
              @click="setTabIndex(1)"
            >
              <b-img
                v-if="theme === 'light'"
                :src="require('@/assets/preparePage/Mode2-light.png')"
                class="modeIconImage"
                :class="{ active: tabIndex === 1 }"
              />
              <b-img
                v-else
                :src="require('@/assets/preparePage/Mode2-dark.png')"
                class="modeIconImage"
                :class="{ active: tabIndex === 1 }"
              />
              <span class="mode-icon-text">{{
                t("session.prepare.step.selection.mode.description.withUS.tab.label")
              }}</span>
            </button>
            <button
              v-if="isIssueTrackerEnabled"
              type="button"
              :class="['mode-icon', tabIndex === 2 ? 'active' : '']"
              @click="setTabIndex(2)"
            >
              <b-img
                v-if="theme === 'light'"
                :src="require('@/assets/preparePage/Mode3-light.png')"
                class="modeIconImage"
                :class="{ active: tabIndex === 2 }"
              />
              <b-img
                v-else
                :src="require('@/assets/preparePage/Mode3-dark.png')"
                class="modeIconImage"
                :class="{ active: tabIndex === 2 }"
              />
              <span class="mode-icon-text">
                {{
                  t("session.prepare.step.selection.mode.description.withIssueTracker.tab.label")
                }}
              </span>
            </button>
          </div>
          <div class="mt-5">
            <story-points-component v-if="tabIndex === 0" />
            <div v-else-if="tabIndex === 1">
              <user-story-component class="mt-5" />
              <input
                id="fileUpload"
                type="file"
                hidden
                accept="text/csv"
                @change="importStory($event)"
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
            </div>
            <jira-component v-else-if="tabIndex === 2" class="mt-5" />
          </div>
        </div>
      </template>

      <template #2>
        <div class="wizardStep">
          <h4 class="mb-3">
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
            :selected-card-set="selectedCardSetOptions"
            @selectedCardSetOptions="setCardSetOptions"
          />
          <h4 class="mt-3">{{ t("session.prepare.step.selection.password.title") }}</h4>
          <b-row class="mt-1">
            <b-col>
              <b-form-input
                id="input-password"
                v-model="password"
                :placeholder="t('session.prepare.step.selection.password.placeholder')"
              />
            </b-col>
          </b-row>
        </div>
      </template>

      <template #3>
        <div class="wizardStep">
          <h4 class="mb-3">
            <b-img
              v-if="theme === 'light'"
              :src="require('@/assets/preparePage/P3.png')"
              class="numberPictures"
            />
            <b-img v-else :src="require('@/assets/preparePage/P3D.png')" class="numberPictures" />
            {{ t("session.prepare.step.selection.time.title") }}
          </h4>
          <div class="timer-control d-flex justify-content-center mb-5">
            <b-button
              variant="primary"
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
              @click="
                setTimerUp();
                $event.target.blur();
              "
            >
              +
            </b-button>
          </div>
          <h4 class="mb-3">
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
              <div class="voting-control d-flex justify-content-center mb-2">
                <b-button
                  :variant="hostVoting ? 'primary' : 'outline-light'"
                  @click="
                    hostVoting = true;
                    $event.target.blur();
                  "
                >
                  {{ t("session.prepare.step.selection.hostVoting.hostVotingOn") }}
                </b-button>
                <b-button
                  :variant="!hostVoting ? 'primary' : 'outline-light'"
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
        </div>
      </template>

      <template #4>
        <div class="wizardStep">
          <h4 class="mb-3">{{ t("session.prepare.step.confirmation.title") }}</h4>
          <b-list-group>
            <b-list-group-item
              >{{ t("session.prepare.step.selection.mode.title") }}:
              {{ userStoryMode }}</b-list-group-item
            >
            <b-list-group-item>
              {{ t("session.prepare.step.selection.cardSet.title") }}:
              {{
                Array.isArray(selectedCardSetOptions.activeValues)
                  ? selectedCardSetOptions.activeValues.join(", ")
                  : ""
              }}
            </b-list-group-item>
            <b-list-group-item
              >{{ t("session.prepare.step.selection.time.title") }}:
              {{ timer == 0 ? "∞" : formatTimer }}</b-list-group-item
            >
            <b-list-group-item
              >{{ t("session.prepare.step.selection.hostVoting.title") }}:
              {{
                hostVoting
                  ? t("session.prepare.step.selection.hostVoting.hostVotingOn")
                  : t("session.prepare.step.selection.hostVoting.hostVotingOff")
              }}</b-list-group-item
            >
            <b-list-group-item
              >{{ t("session.prepare.step.selection.password.title") }}:
              {{ password }}</b-list-group-item
            >
          </b-list-group>
        </div>
      </template>
    </Steppy>
  </b-container>
</template>

<script lang="ts">
import { defineComponent, ref } from "vue";
import Session from "../model/Session";
import CardSet from "../model/CardSet";
import Constants from "../constants";
import CardSetComponent from "../components/CardSetComponent.vue";
import UserStoryComponent from "../components/UserStoryComponent.vue";
import JiraComponent from "../components/JiraComponent.vue";
import StoryPointsComponent from "@/components/StoryPointsComponent.vue";
import UserStory from "@/model/UserStory";
import papaparse, { ParseResult } from "papaparse";
import apiService from "@/services/api.service";
import { useDiveniStore } from "@/store";
import { useToast } from "vue-toastification";
import { useI18n } from "vue-i18n";
import { Steppy } from "vue3-steppy";

export default defineComponent({
  name: "PrepareSessionPage",
  components: {
    CardSetComponent,
    UserStoryComponent,
    JiraComponent,
    StoryPointsComponent,
    Steppy,
  },
  setup() {
    const store = useDiveniStore();
    const toast = useToast();
    const { t } = useI18n();
    const step = ref<number>(1);
    return { store, toast, t, step };
  },
  data() {
    return {
      password: "",
      selectedCardSetOptions: {
        name: "",
        values: [] as string[],
        activeValues: [] as string[],
        position: 0,
      } as CardSet,
      timer: 30,
      warningWhenUnderZero: "",
      tabIndex: null as number | null,
      hostVoting: false,
      isIssueTrackerEnabled: false,
      theme: localStorage.getItem("user-theme"),
      tabs: [
        {
          title: this.t("session.prepare.step.wizard.modeSelection"),
          isValid: false,
          iconSuccess: null,
        },
        {
          title: this.t("session.prepare.step.wizard.configurationCardSet"),
          isValid: false,
          iconSuccess: null,
        },
        {
          title: this.t("session.prepare.step.wizard.configurationTime/HostVote"),
          isValid: true,
          iconSuccess: null,
        },
        {
          title: this.t("session.prepare.step.wizard.confirmation"),
          isValid: true,
          iconSuccess: null,
        },
      ],
    };
  },
  computed: {
    selectedProjectAndTabIndex() {
      return `${this.store.selectedProject}|${this.tabIndex}`;
    },
    userStories() {
      return this.store.userStories;
    },
    userStoryMode(): string {
      return ["NO_US", "US_MANUALLY", "US_JIRA"][this.tabIndex || 0];
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
    selectedProjectAndTabIndex() {
      this.tabs[0].isValid =
        this.tabIndex === 2 ? !!this.store.selectedProject : this.tabIndex != null;
    },
    selectedCardSetOptions(isPresent) {
      this.tabs[1].isValid = isPresent != null;
    },
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
    async sendCreateSessionRequest() {
      const url = Constants.backendURL + Constants.createSessionRoute;
      const sessionConfig = {
        set: this.selectedCardSetOptions.activeValues,
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
    setCardSetOptions($event: CardSet) {
      this.selectedCardSetOptions = $event;
    },
    onUserStoriesChanged(stories: UserStory) {
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
      if (fileUpload) {
        fileUpload.click();
      }
    },
    importStory(event: Event) {
      const target = event.target as HTMLInputElement;
      const files = target.files;

      if (!files || !files[0]) {
        return;
      }

      papaparse.parse(files[0], {
        header: true,
        delimiter: ";",
        complete: (result: ParseResult<UserStory>) => {
          const stories: UserStory[] = [];

          result.data.forEach((story) => {
            const { title, description, estimation } = story;

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
        error: (err) => {
          console.error("Error parsing CSV:", err);
          this.toast.error(
            this.t("session.prepare.step.selection.mode.description.withUS.toastErrorNotification")
          );
        },
      });
    },
    setTabIndex(index: number) {
      this.tabIndex = index;
    },
  },
});
</script>

<style lang="scss" scoped>
@import "@/assets/style/_variables.scss";

.main {
  white-space: pre-line;
}

.wizardStep {
  color: var(--text-primary-color) !important;
}

.voting-control {
  button {
    width: 6rem;
    border-radius: $border-radius;
    border-color: var(--btn-border-color) !important;

    &:not(.active) {
      background-color: var(--preparePageTimerBackground);
      &:hover {
        background-color: var(--preparePageInActiveTabHover);
      }
      &:focus {
        background-color: var(--preparePageInActiveTabHover);
        color: var(--text-color-hover);
      }
    }
  }
}

.timer-control {
  align-items: center;
  border-radius: $border-radius;
  background-color: var(--preparePageTimerBackground);
  font-size: 1.25rem;
  width: 12rem;
  height: 2.5rem;
  padding: 0;
  margin: 0 auto;

  button {
    height: 2.5rem;
    width: 5rem;
    border-radius: $border-radius;
  }

  .setting-value {
    flex: 0 0 auto;
    width: 5rem;
  }
}

.numberPictures {
  height: 45px;
  width: 45px;
}

.mode-icons {
  .mode-icon {
    max-width: 224px;
    min-width: 95px;
    min-height: 200px;
    justify-content: flex-start;
    align-items: center;
    display: flex;
    flex-direction: column;
    cursor: pointer;
    border-radius: $border-radius;
    box-shadow: 8px 8px 5px var(--box-shadow);
    background-color: var(--preparePage-mode-backround);

    &:hover {
      border-width: 4px;
      border-color: var(--secondary-button-hovered);
      border-style: solid;
      p {
        color: var(--text-primary-color) !important;
        font-weight: bold !important;
      }
    }

    &.active {
      border-width: 5px;
      border-color: var(--primary-button-hovered);
      border-style: solid;
    }

    .modeIconImage {
      width: 100px;
      height: 100px;
    }

    .mode-icon-text {
      font-size: 20px;
      padding-top: 16px;
      text-align: center;
      font-weight: bold;
      color: var(--text-primary-color);
    }
  }
}
</style>
