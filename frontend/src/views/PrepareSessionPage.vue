<template>
  <div>
    <desktop-card
    :cardName="name"
    :cardSet="[1, 2, 4]"
    />
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
        @selectedCardSetNumbers="setCardSetNumbers"
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
import DesktopCard from '../components/DesktopCard.vue';

export default Vue.extend({
  name: 'PrepareSessionPage',
  components: {
    CardSetComponent,
    DesktopCard,
  },
  data() {
    return {
      title: 'Prepare session',
      password: '',
      selectedCardSetNumbers: [],
    };
  },
  methods: {
    async sendCreateSessionRequest() {
      const url = Constants.backendURL + Constants.createSessionRoute;
      const payload = { password: this.password === '' ? null : this.password };
      try {
        const session: Session = (await this.axios.post(url, payload)).data as {
          sessionID: string,
          adminID: string,
          membersID: string
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
        },
      });
    },
    setCardSetNumbers($event) {
      this.selectedCardSetNumbers = $event;
    },
    buttonDisabled() {
      return this.selectedCardSetNumbers.length < 1;
    },
  },
});
</script>
