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
          :md="showPassword ? '6' : '12'"
        >
          <h6>Session id</h6>
          <b-form-input
            v-model="sessionID"
            class="mt-3"
            type="text"
          />
        </b-col>
        <b-col
          v-if="showPassword"
          class="mt-2 mt-md-0"
          cols="12"
          md="6"
        >
          <h6>Password</h6>
          <b-form-input
            v-model="password"
            class="mt-3"
            type="text"
          />
        </b-col>
      </b-row>
      <b-row>
        <success-button
          class="mt-5"
          :button-text="buttonText"
          :on-click="onClickButton"
          :disabled="name.length < 1 || sessionID.length < 1"
        />
      </b-row>
    </div>
    <div class="col-lg-3 col-md-2 col-sm-1" />
    <b-col />
  </b-row>
</template>

<script lang="ts">
import Vue from 'vue';
import RoundedAvatar from './RoundedAvatar.vue';
import SuccessButton from './SuccessButton.vue';

export default Vue.extend({
  name: 'JoinPageCard',
  components: {
    RoundedAvatar,
    SuccessButton,
  },
  props: {
    showPassword: { type: Boolean, required: true },
    color: { type: String, required: true },
    animalAssetName: { type: String, required: true },
    buttonText: { type: String, required: true },
  },
  data() {
    return {
      sessionID: '',
      password: '',
      name: '',
    };
  },
  methods: {
    onClickButton() {
      this.$emit('clicked', {
        sessionID: this.sessionID,
        password: this.password,
        name: this.name,
      });
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
