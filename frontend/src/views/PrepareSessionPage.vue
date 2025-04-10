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
          <div class="mode-icons mt-2 d-flex justify-content-around">
            <button
              type="button"
              :class="['mode-icon', tabIndex === 0 ? 'active' : '']"
              @click="setTabIndex(0)"
            >
              <b-img
                :src="require('@/assets/preparePage/Mode1.png')"
                class="modeIconImage"
                :class="{ active: tabIndex === 0 }"
              />
              <span class="mode-icon-text">
                {{ t("session.prepare.step.selection.mode.description.withoutUS.tab.label") }}
              </span>
            </button>
            <button
              type="button"
              :class="['mode-icon', tabIndex === 1 ? 'active' : '']"
              @click="setTabIndex(1)"
            >
              <b-img
                :src="require('@/assets/preparePage/Mode2.png')"
                class="modeIconImage"
                :class="{ active: tabIndex === 1 }"
              />
              <span class="mode-icon-text">
                {{ t("session.prepare.step.selection.mode.description.withUS.tab.label") }}
              </span>
            </button>
            <button
              v-if="isIssueTrackerEnabled"
              type="button"
              :class="['mode-icon', tabIndex === 2 ? 'active' : '']"
              @click="setTabIndex(2)"
            >
              <b-img
                :src="require('@/assets/preparePage/Mode3.png')"
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
            <jira-component v-else-if="tabIndex === 2" class="mt-5" @jira="isJiraSelected = true" />
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
              >-</b-button
            >
            <div id="setting-value" class="font-weight-bolder px-3 text-center">
              {{ timer == 0 ? "∞" : formatTimer }}
            </div>
            <b-button
              variant="primary"
              @click="
                setTimerUp();
                $event.target.blur();
              "
              >+</b-button
            >
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
          <div class="copy-btn-container">
            <b-button
              v-if="!planningStart"
              class="copy-btn"
              variant="outline-dark"
              @click="copyDeepLink"
            >
              <b-icon icon="clipboard" class="bIcons" />
              {{ t("session.prepare.step.wizard.deeplink.copyDeeplink") }}
            </b-button>
          </div>
          <h4 class="mb-3">{{ t("session.prepare.step.confirmation.title") }}</h4>
          <b-list-group>
            <b-list-group-item>
              {{ t("session.prepare.step.selection.mode.title") }}:
              {{ userStoryMode }}
            </b-list-group-item>
            <b-list-group-item>
              {{ t("session.prepare.step.selection.cardSet.title") }}:
              {{
                Array.isArray(selectedCardSetOptions.activeValues)
                  ? selectedCardSetOptions.activeValues.join(", ")
                  : ""
              }}
            </b-list-group-item>
            <b-list-group-item>
              {{ t("session.prepare.step.selection.time.title") }}:
              {{ timer == 0 ? "∞" : formatTimer }}
            </b-list-group-item>
            <b-list-group-item>
              {{ t("session.prepare.step.selection.hostVoting.title") }}:
              {{
                hostVoting
                  ? t("session.prepare.step.selection.hostVoting.hostVotingOn")
                  : t("session.prepare.step.selection.hostVoting.hostVotingOff")
              }}
            </b-list-group-item>
            <b-list-group-item>
              {{ t("session.prepare.step.selection.password.title") }}:
              {{ password }}
            </b-list-group-item>
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
import { useRouter, useRoute, LocationQueryValue } from "vue-router";

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
    const router = useRouter();
    const route = useRoute();
    const step = ref<number>(1);
    return { store, toast, t, step, router, route };
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
      timer: 0,
      warningWhenUnderZero: "",
      tabIndex: null as number | null,
      hostVoting: false,
      isIssueTrackerEnabled: false,
      theme: localStorage.getItem("user-theme"),
      isJiraSelected: false,
      isDeepLink: false,
      generatedUUIDs: new Set<number>(),
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
          isValid: false,
          iconSuccess: null,
        },
      ],
    };
  },
  computed: {
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
    modeStepIsValid(): boolean {
      if (this.tabIndex === 2) {
        return !!this.store.selectedProject;
      }
      return this.tabIndex !== null;
    },
    cardSetStepIsValid(): boolean {
      return (
        this.selectedCardSetOptions &&
        Array.isArray(this.selectedCardSetOptions.activeValues) &&
        this.selectedCardSetOptions.activeValues.length > 0
      );
    },
    finalStepIsValid(): boolean {
      return this.modeStepIsValid && this.cardSetStepIsValid;
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
    modeStepIsValid(newVal: boolean) {
      this.tabs[0].isValid = newVal;
    },
    cardSetStepIsValid(newVal: boolean) {
      this.tabs[1].isValid = newVal;
    },
    finalStepIsValid(newVal: boolean) {
      this.tabs[3].isValid = newVal;
    },
    step(newStep) {
      if (this.isDeepLink && this.tabIndex === 2 && newStep === 2 && this.store.selectedProject) {
        this.step = 4;
      }
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
    this.parseDeepLink();
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
      this.router.push({
        name: "SessionPage",
        state: {
          sessionID: session.sessionID,
          adminID: session.adminID,
          timerSecondsString: this.timer.toString(),
          password: this.password,
          voteSetJson: JSON.stringify(session.sessionConfig.set),
          sessionState: session.sessionState,
          userStoryMode: session.sessionConfig.userStoryMode,
          hostVoting: this.hostVoting,
          rejoined: "false",
          isJiraSelected: this.isJiraSelected,
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
    generateNumericUUID() {
      let uuid: number;
      do {
        uuid = Math.floor(Math.random() * 1e15) + Date.now();
      } while (this.generatedUUIDs.has(uuid));
      this.generatedUUIDs.add(uuid);
      return uuid;
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
              id: this.generateNumericUUID().toString(),
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
      if (this.tabIndex !== index) {
        this.selectedCardSetOptions = {
          name: "",
          values: [] as string[],
          activeValues: [] as string[],
          position: 0,
        } as CardSet;
      }
      this.tabIndex = index;
    },
    parseDeepLink() {
      const { query } = this.route;
      if (!Object.keys(query).length) return; // No deep link provided

      const getQueryParam = (
        param: LocationQueryValue | LocationQueryValue[] | null | undefined
      ): string | null => {
        if (param == null) return null;
        return Array.isArray(param) ? param.find((item) => item != null) ?? null : param;
      };

      const modeValue = getQueryParam(query.mode);
      const setValue = getQueryParam(query.set);
      const timerValue = getQueryParam(query.timer);
      const hostVotingValue = getQueryParam(query.hostVoting);
      const passwordValue = getQueryParam(query.password);

      if (!modeValue || !setValue || !timerValue || hostVotingValue == null) {
        this.toast.error(this.t("session.prepare.step.wizard.deeplink.missingParameters"));
        return;
      }

      const modeMap: Record<string, number> = {
        NO_US: 0,
        US_MANUALLY: 1,
        US_JIRA: 2,
      };

      if (!(modeValue in modeMap)) {
        this.toast.error(this.t("session.prepare.step.wizard.deeplink.invalidMode"));
        return;
      }

      this.tabIndex = modeMap[modeValue];
      this.selectedCardSetOptions.activeValues = setValue.split(",");

      const parsedTimer = parseInt(timerValue, 10);
      if (isNaN(parsedTimer)) {
        this.toast.error(this.t("session.prepare.step.wizard.deeplink.invalidTime"));
        return;
      }
      this.timer = parsedTimer;
      this.hostVoting = hostVotingValue === "true";

      if (passwordValue !== null) {
        this.password = passwordValue;
      }

      this.isDeepLink = true;
      // In US_JIRA mode, enforce authentication (step 1); otherwise, jump to confirmation (step 4)
      this.step = modeValue === "US_JIRA" ? 1 : 4;
    },
    copyDeepLink() {
      // Build the deep link URL based on current configuration
      const modeMap: Record<number, string> = {
        0: "NO_US",
        1: "US_MANUALLY",
        2: "US_JIRA",
      };
      const index = this.tabIndex ?? 0;
      const mode = modeMap[index] || "NO_US";
      const setParam = this.selectedCardSetOptions.activeValues.join(",");
      const timerParam = this.timer;
      const hostVotingParam = this.hostVoting;
      const passwordParam = this.password ? `&password=${encodeURIComponent(this.password)}` : "";
      const baseUrl = window.location.origin + "/prepare";
      const deepLink = `${baseUrl}?mode=${mode}&set=${encodeURIComponent(
        setParam
      )}&timer=${timerParam}&hostVoting=${hostVotingParam}${passwordParam}`;

      navigator.clipboard
        .writeText(deepLink)
        .then(() => {
          this.toast.success(this.t("session.prepare.step.wizard.deeplink.copy"));
        })
        .catch((err) => {
          console.error("Failed to copy deep link", err);
          this.toast.error(this.t("session.prepare.step.wizard.deeplink.copyFailed"));
        });
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
  display: flex;
  align-items: center;
  border-radius: $border-radius;
  background-color: var(--preparePageTimerBackground);
  font-size: clamp(1rem, 2vw, 1.25rem);
  width: 100%;
  max-width: 12rem;
  height: clamp(2.5rem, 5vw, 2.5rem);
  padding: 0;
  margin: 0 auto;
  button {
    height: 100%;
    width: 50%;
    border-radius: $border-radius;
  }
}

.numberPictures {
  height: 45px;
  width: 45px;
}

.mode-icons {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 1rem;
  .mode-icon {
    max-width: 225px;
    min-width: 95px;
    min-height: 175px;
    justify-content: flex-start;
    align-items: center;
    display: flex;
    flex-direction: column;
    cursor: pointer;
    color: black;
    border: thin solid;
    border-color: var(--estimateButtonBorder);
    border-radius: $border-radius;
    box-shadow: 8px 8px 5px var(--box-shadow);
    background-color: var(--preparePage-mode-backround);
    &:hover {
      border-width: 4px;
      border-color: var(--preparePage-hover-icon-border);
      border-style: solid;
    }
    &.active {
      border-width: 5px;
      border-color: var(--preparePage-active-icon-border);
      border-style: solid;
    }
    .modeIconImage {
      width: 100px;
      height: 100px;
    }
    .mode-icon-text {
      font-size: 18px;
      text-align: center;
      font-weight: bold;
    }
  }
}

.copy-btn-container {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-bottom: 1rem;
}

.copy-btn {
  background-color: var(--textAreaColour);
  display: inline-flex;
  align-items: center;
  margin: 0.25rem;
}

.copy-btn:hover {
  background-color: var(--textAreaColourHovered);
  color: var(--text-primary-color);
}

.mode-icon-text::before {
  content: "";
  display: block;
  width: 100%;
  height: 1px;
  background: var(--estimateButtonBorder);
  margin: 8px 0;
}

:deep(.steppy) {
  padding-bottom: calc(20px + (1000px - 100vw) / 20);
}

:deep(.steppy-item-title) {
  font-weight: bold;
  text-align: center;
  display: block;
  font-size: clamp(6px, 3vw, 17px) !important;
  max-width: clamp(52px, 15vw, 200px);
  margin: 0 auto;
  line-height: 1.2;
  padding: 0 10px;
}

@media (max-width: 500px) {
  :deep(.steppy-item-title) {
    display: -webkit-box;
    -webkit-box-orient: vertical;
    line-clamp: 5;
    -webkit-line-clamp: 5;
    overflow: hidden;
  }
}

:deep(.wrapper-steppy .controls .btn:hover) {
  color: black;
}
</style>
