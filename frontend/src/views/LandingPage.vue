<template>
  <div id="landing-page">
    <b-container fluid class="teaser">
      <b-container>
        <b-jumbotron header="DIVENI" lead="Instant free and easy remote Planning Poker" />
      </b-container>
    </b-container>
    <b-container class="my-5">
      <b-card-group deck class="justify-content-center">
        <landing-page-card
          :title="t('page.landing.meeting.new.title')"
          :description="t('page.landing.meeting.new.description')"
          :button-text="t('page.landing.meeting.new.buttons.start.label')"
          :on-click="goToPrepareSessionPage"
          button-variant="primary"
          class="newSessionCard"
        />
        <landing-page-card
          :title="t('page.landing.meeting.join.title')"
          :description="t('page.landing.meeting.join.description')"
          :button-text="t('page.landing.meeting.join.buttons.start.label')"
          :on-click="goToJoinPage"
          button-variant="secondary"
          class="joinSessionCard"
        />
        <landing-page-card
          v-if="sessionWrapper.session"
          :title="t('page.landing.meeting.reconnect.title')"
          :description="t('page.landing.meeting.reconnect.description')"
          :button-text="t('page.landing.meeting.reconnect.buttons.start.label')"
          :on-click="goToSessionPage"
          class="reconnectSessionCard"
        />
      </b-card-group>
    </b-container>
    <AnalyticsDataComponent ref="dataComponent"> </AnalyticsDataComponent>
    <b-container>
      <div class="parent pb-md-5 px-5">
        <div class="py-5"></div>
        <div>
          <h1 class="text-center">How DIVENI works <b-icon-gear-wide /></h1>
          <b-card-group deck class="py-5">
            <b-card class="pictureCard">
              <b-card-text>
                <b-img-lazy
                  :src="require(`@/assets/SetUpSession.png`)"
                  width="300"
                  height="265"
                  alt="Set Up Session Picture"
                />
              </b-card-text>
            </b-card>
            <b-card class="aboutDiveni" title="Set up Session">
              <b-card-text>
                Create a planning session and select your prefered voting set.
              </b-card-text>
              <b-card-text>
                You could import your user stories or connect JIRA to syncronize story points.
              </b-card-text>
              <b-card-sub-title>
                <a href="https://sybit-education.github.io/Diveni/guide" target="_blank">
                  Connecting to Issue-Tracker</a
                >
              </b-card-sub-title>
              <b-card-text>
                DIVENI could connect to issue trackers like Azure DevOps, JIRA Server and Cloud to
                show user stories and update the voted results of your planning poker.
              </b-card-text>
            </b-card>
          </b-card-group>
          <b-card-group>
            <b-card class="aboutDiveni" title="Invite your Team">
              <b-card-text> Invite your team using QR-Code, invite link or code. </b-card-text>
              <b-card-text>
                Every user will be randomly assinged to a animal and is ready to estimate.
              </b-card-text>
              <b-card-text>
                If everybody is in the session, you could start estimation. Having defined a time
                limit this will be used as limit for voting time.
              </b-card-text>
            </b-card>
            <b-card class="pictureCard">
              <b-card-text>
                <b-img-lazy
                  :src="require(`@/assets/InviteYourTeam.png`)"
                  width="300"
                  height="265"
                  alt="Invite Your Team Picture"
                />
              </b-card-text>
            </b-card>
          </b-card-group>
          <b-card-group>
            <b-card class="pictureCard">
              <b-card-text>
                <b-img-lazy
                  :src="require(`@/assets/EstimateUserStories.png`)"
                  width="300"
                  height="265"
                  alt="Estimate User Stories Picture"
                />
              </b-card-text>
            </b-card>
            <b-card class="aboutDiveni" title="Estimate User Stories">
              <b-card-text>
                Every member of team could use your defined set to vote the selected user story.
              </b-card-text>
              <b-card-text>
                If everybody has voted, DIVENI shows results and randomly selects two voters having
                voted minimum / maximum to discuss their estimation.
              </b-card-text>
              <b-card-text> After discussion you could repeat estimation. </b-card-text>
            </b-card>
          </b-card-group>
        </div>
      </div>
      <h1 class="mt-5 text-center pt-md-5">About DIVENI</h1>
      <p class="text-center">
        DIVENI was initially developed by students of HTWG Constance and is open source now.
      </p>
      <p class="text-center">
        More information could be found in the
        <a href="https://docs.diveni.io/">documentation</a>.
      </p>
    </b-container>
    <b-container class="py-3">
      <h1 class="mt-5 text-center">Connectors</h1>
    </b-container>
    <CarouselComponent class="py-5"></CarouselComponent>
    <DownloadPWAModal />
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import LandingPageCard from "../components/LandingPageCard.vue";
import Constants from "../constants";
import Session from "../model/Session";
import AnalyticsDataComponent from "../components/AnalyticsDataComponent.vue";
import { useDiveniStore } from "@/store";
import { useI18n } from "vue-i18n";
import CarouselComponent from "@/components/CarouselComponent.vue";
import DownloadPWAModal from "@/components/DownloadPWAModal.vue";
import { useRouter } from "vue-router";

export default defineComponent({
  name: "LandingPage",
  components: {
    DownloadPWAModal,
    LandingPageCard,
    AnalyticsDataComponent,
    CarouselComponent,
  },
  setup() {
    const store = useDiveniStore();
    const { t } = useI18n();
    const router = useRouter();
    return { store, t, router };
  },
  data() {
    return {
      sessionWrapper: {} as { session: Session },
      startNewSessionOnMounted: false,
    };
  },
  created() {
    this.disconnectFromBackendWS();
    this.checkAdminCookie();
  },
  methods: {
    async checkAdminCookie() {
      const cookie = window.localStorage.getItem("adminCookie");
      if (cookie !== null) {
        console.log(`Found admin cookie: '${cookie}'`);
        const url = Constants.backendURL + Constants.createSessionRoute;
        try {
          const session = (
            await this.axios.get(url, {
              params: {
                adminCookie: cookie,
              },
            })
          ).data as {
            sessionID: string;
            adminID: string;
            sessionConfig: {
              set: Array<string>;
              timerSeconds: number;
              userStories: Array<{
                id: string | null;
                title: string;
                description: string;
                estimation: string | null;
                isActive: false;
              }>;
              userStoryMode: string;
            };
            sessionState: string;
            members: Array<string>;
            hostVoting: boolean;
          };
          this.sessionWrapper = { session };
        } catch (e) {
          console.error(`got error: ${e}`);
          window.localStorage.removeItem("adminCookie");
        }
      }
    },
    goToJoinPage() {
      this.router.push({ name: "JoinPage" });
    },
    goToPrepareSessionPage() {
      this.router.push({ name: "PrepareSessionPage" });
    },
    goToSessionPage() {
      this.store.setUserStories({
        stories: this.sessionWrapper.session.sessionConfig.userStories,
      });
      this.router.push({
        name: "SessionPage",
        state: {
          sessionID: this.sessionWrapper.session.sessionID,
          adminID: this.sessionWrapper.session.adminID,
          voteSetJson: JSON.stringify(this.sessionWrapper.session.sessionConfig.set),
          sessionState: this.sessionWrapper.session.sessionState,
          timerSecondsString: this.sessionWrapper.session.sessionConfig.timerSeconds.toString(),
          startNewSessionOnMountedString: this.startNewSessionOnMounted.toString(),
          userStoryMode: this.sessionWrapper.session.sessionConfig.userStoryMode,
          hostVoting: this.sessionWrapper.session.hostVoting,
          rejoined: "false",
        },
      });
    },
    disconnectFromBackendWS() {
      this.store.disconnectFromBackendWS();
    },
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS/SCSS to this component only -->
<style lang="scss">
.newSessionCard {
  .card-footer {
    background-color: #52173100; /* So the Footer does not overflow */
  }
}

.joinSessionCard {
  .card-footer {
    background-color: #52173100; /* So the Footer does not overflow */
  }
}

.reconnectSessionCard {
  .landingPageCardButton {
    background-color: var(--reconnectButton) !important;
    &:hover {
      background-color: var(--reconnectButtonHovered) !important;
    }
  }
  .card-footer {
    background-color: #52173100; /* So the Footer does not overflow */
  }
}

.teaser {
  background: linear-gradient(var(--background-color-primary), var(--pictureGradientEnd)),
    url("~@/assets/img/diveni-background.png");
  background-size: cover;
  background-repeat: no-repeat;
}

.aboutDiveni {
  background: transparent !important;
  border-style: none !important;
  height: 300px;
  font-size: large;
  overflow: auto;
}

.card-title {
  color: var(--text-primary-color) !important;
}

.parent {
  position: relative;
  height: 110%;
  width: 100%;
}

.pictureCard {
  background: transparent !important;
  align-items: center;
  box-shadow: none !important;
  border: none !important;
}

.diveniDescription {
  font-size: 1.25rem;
  max-height: 25rem;
  overflow: auto;
  width: 35%;
}
</style>
