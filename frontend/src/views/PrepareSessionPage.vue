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
            4. Secure with password
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
import Member from '../model/Member';
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
    };
  },
  created() {
    this.checkAdminCookie();
  },
  methods: {
    async checkAdminCookie() {
      const cookie = window.localStorage.getItem('adminCookie');
      if (cookie !== null) {
        console.log(`Found admin cookie: '${cookie}'`);
        const url = Constants.backendURL + Constants.createSessionRoute;
        try {
          const session = (await this.axios.get(url, {
            params: {
              adminCookie: cookie,
            },
          })).data as {
            sessionID: string,
            adminID: string,
            sessionConfig: {
              set: Array<string>,
              userStories: Array<{title:string, description:string, estimation:string|null }>,
            },
            sessionState: string,
          };
          // this.$store.commit('setMembers', JSON.stringify(session.members));
          this.goToSessionPage(session);
        } catch (e) {
          console.log(`got error: ${e}`);
          window.localStorage.removeItem('adminCookie');
        }
      }
    },
    async sendCreateSessionRequest() {
      const url = Constants.backendURL + Constants.createSessionRoute;
      const sessionConfig = {
        set: this.selectedCardSetOptions,
        password: this.password === '' ? null : this.password,
        userStories: this.userStories,
      };
      try {
        const response = (await this.axios.post(url, sessionConfig)).data as {
          session: {
            sessionID: string,
            adminID: string,
            sessionConfig: {
              set: Array<string>,
              userStories: Array<{title:string, description:string, estimation:string|null }>,
            },
            sessionState: string,
          },
          adminCookie: string,
        };
        window.localStorage.setItem('adminCookie', response.adminCookie);
        this.goToSessionPage(response.session as Session);
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
    onUserStoriesChanged(newUserStories) {
      console.log('user stories changed', newUserStories);
      this.userStories = newUserStories;
    },
  },
});
</script>
