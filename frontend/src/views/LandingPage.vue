<template>
  <div id="landing-page">
    <div class="d-flex align-items-center d-inline-block">
      <div class="ml-5">
        <h1 class="display-1 text-left">DIVENI</h1>
        <h1 class="display-4 text-left">The Planning-Poker App</h1>
        <p class="diveniDescription">
          Streamline your agile estimation with out user-friendly, free tool, ideal for Scrum teams.
          It enhances remote collaboration, making agile planning simple and efficient. Start now
          and transform your team's productivity!
        </p>
      </div>
      <div class="styleContainer mr-3">
        <div class="d-flex align-items-center d-inline-block">
          <div class="purpleSquare"></div>
          <div class="greenSquare"></div>
        </div>
        <b-icon-question-lg id="questionMark" />
        <div class="d-flex align-items-center d-inline-block">
          <div class="redSquare"></div>
          <div class="brownSquare"></div>
        </div>
      </div>
    </div>
    <b-container class="my-5">
      <b-card-group deck class="justify-content-center">
        <landing-page-card
          :title="t('page.landing.meeting.new.title')"
          :description="t('page.landing.meeting.new.description')"
          :button-text="t('page.landing.meeting.new.buttons.start.label')"
          :on-click="goToPrepareSessionPage"
          class="newSessionCard"
        />
        <landing-page-card
          :title="t('page.landing.meeting.join.title')"
          :description="t('page.landing.meeting.join.description')"
          :button-text="t('page.landing.meeting.join.buttons.start.label')"
          :on-click="goToJoinPage"
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
                <b-img :src="require(`@/assets/SetUpSession.png`)" class="landingPagePictures" />
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
                <b-img :src="require(`@/assets/InviteYourTeam.png`)" class="landingPagePictures" />
              </b-card-text>
            </b-card>
          </b-card-group>
          <b-card-group>
            <b-card class="pictureCard">
              <b-card-text>
                <b-img
                  :src="require(`@/assets/EstimateUserStories.png`)"
                  class="landingPagePictures"
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
export default defineComponent({
  name: "LandingPage",
  components: {
    LandingPageCard,
    AnalyticsDataComponent,
    CarouselComponent,
  },
  setup() {
    const store = useDiveniStore();
    const { t } = useI18n();
    return { store, t };
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
                id: number | null;
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
      this.$router.push({ name: "JoinPage" });
    },
    goToPrepareSessionPage() {
      this.$router.push({ name: "PrepareSessionPage" });
    },
    goToSessionPage() {
      this.store.setUserStories({
        stories: this.sessionWrapper.session.sessionConfig.userStories,
      });
      this.$router.push({
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
  .landingPageCardButton {
    background-color: var(--primary-button) !important;
    color: var(--text-primary-color);
  }
  .card-footer {
    background-color: #52173100; /* So the Footer does not overflow */
  }
}

.joinSessionCard {
  .landingPageCardButton {
    background-color: var(--secondary-button) !important;
  }

  .card-footer {
    background-color: #52173100; /* So the Footer does not overflow */
  }
}

.reconnectSessionCard {
  .landingPageCardButton {
    background-color: var(--reconnectButton) !important;
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

.landingPagePictures {
  height: 270px;
  width: 270px;
}

@media screen and (min-width: 1000px) {
  .styleContainer {
    position: absolute;
    min-height: 20rem;
    min-width: 10rem;
    right: 10%;
  }
  .purpleSquare {
    background-color: #8a17b2;
    position: relative;
    height: 10rem;
    width: 7rem;
    margin-right: 0.75rem;
    margin-bottom: 0.75rem;
    border-radius: 0.5rem;
  }
  .greenSquare {
    background-color: #09816b;
    position: relative;
    height: 10rem;
    width: 7rem;
    margin-left: 0.75rem;
    margin-bottom: 0.75rem;
    border-radius: 0.5rem;
  }
  .redSquare {
    background-color: #791c59;
    position: relative;
    height: 10rem;
    width: 7rem;
    margin-right: 0.75rem;
    margin-top: 0.75rem;
    border-radius: 0.5rem;
  }
  .brownSquare {
    background-color: #601616;
    position: relative;
    height: 10rem;
    width: 7rem;
    margin-left: 0.75rem;
    margin-top: 0.75rem;
    border-radius: 0.5rem;
  }
  #questionMark {
    position: absolute;
    height: 10rem;
    width: 10rem;
    top: 5rem;
    right: 2.25rem;
    z-index: 1;
    visibility: visible;
  }
}
.diveniDescription {
  font-size: 1.25rem;
  max-height: 25rem;
  overflow: auto;
  width: 35%;
}

@media screen and (min-width: 0px) and (max-width: 999px) {
  .styleContainer {
  }
  .purpleSquare {
  }
  .greenSquare {
  }
  .redSquare {
  }
  .brownSquare {
  }
  #questionMark {
    visibility: hidden;
  }
  .diveniDescription {
    width: 100% !important;
  }
}
</style>
