<template>
  <b-row>
    <div class="col-lg-3 col-md-2 col-sm-1 " />
    <div
      id="join-card"
      :style="`background-color: ${color};`"
      class="card col-lg-6 col-md-8 col-sm-10 p-5"
    >
      <b-row class="justify-content-center">
        <rounded-avatar
          :color="'transparent'"
          :asset-name="animalAssetName"
          :show-name="false"
          :name="''"
        />
      </b-row>
      <b-row class="mt-2">
        <b-col>
          <h6>Name</h6>
          <b-form-input
            v-model="name"
            class="mt-3"
            type="text"
          />
        </b-col>
      </b-row>
      <b-row class="mt-4">
        <b-col
          cols="12"
          :md="'6'"
        >
          <h6>Session id</h6>
          <b-form-input
            v-model="sessionID"
            class="mt-3"
            type="text"
          />
        </b-col>
        <b-col
          class="mt-2 mt-md-0"
          cols="12"
          md="6"
        >
          <h6>Password</h6>
          <b-form-input
            v-model="password"
            class="mt-3"
            type="password"
            placeholder="(optional)"
          />
        </b-col>
      </b-row>
      <b-row>
        <b-button
          class="mt-5"
          variant="success"
          :disabled="name.length < 1 || sessionID.length < 1"
          @click="onClickButton"
        >
          {{ buttonText }}
        </b-button>
      </b-row>
    </div>
    <div class="col-lg-3 col-md-2 col-sm-1" />
    <b-col />
  </b-row>
</template>

<script lang="ts">
import Vue from 'vue';
import JoinCommand from '../model/JoinCommand';
import RoundedAvatar from './RoundedAvatar.vue';

export default Vue.extend({
  name: 'JoinPageCard',
  components: {
    RoundedAvatar,
  },
  props: {
    color: { type: String, required: true },
    animalAssetName: { type: String, required: true },
    buttonText: { type: String, required: true },
    sessionIdFromUrl: { type: String, required: true },
  },
  data() {
    return {
      sessionID: '',
      password: '',
      name: '',
    };
  },
  created() {
    if (this.sessionIdFromUrl) {
      this.sessionID = this.sessionIdFromUrl;
    }
  },
  methods: {
    onClickButton() {
      const data: JoinCommand = {
        sessionID: this.sessionID.split(' ').join(''),
        password: this.password,
        name: this.name.trim(),
      };
      this.$emit('clicked', data);
    },
  },
});
</script>

<style scoped>
#join-card {
  border-radius: 20px;
}
h6 {
  font-weight:700;
}
</style>
