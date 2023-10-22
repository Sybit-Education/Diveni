<template>
  <b-row ref="card">
    <div class="col-lg-3 col-md-2 col-sm-1" />
    <div
      id="join-card"
      :style="`background-color: ${color};`"
      class="card col-lg-6 col-md-8 col-sm-10 p-5"
    >
      <b-row class="justify-content-center">
        <rounded-avatar
          :member="{
            hexColor: 'transparent',
            avatarAnimal: animalAssetName,
            name: '',
          }"
          :show-name="false"
        />
      </b-row>
      <b-row class="mt-2">
        <b-col class="inputText">
          <h6>{{ $t("page.join.input.name") }}</h6>
          <b-form-input ref="name" v-model="name" class="mt-3 inputFields" type="text" />
        </b-col>
      </b-row>
      <b-row class="mt-4">
        <b-col cols="12" :md="'10'" class="inputText">
          <h6>{{ $t("page.join.input.code") }}</h6>
          <b-form-input v-model="sessionID" class="mt-3 inputFields" type="text" />
        </b-col>
      </b-row>
      <b-row>
        <b-col class="mt-2 mt-md-0 inputText" cols="12" md="9" >
          <h6>{{ $t("page.join.input.password") }}</h6>
          <b-form-input v-model="password" class="mt-3 inputFields" type="password" placeholder="(optional)" />
        </b-col>
        <b-col cols="12" md="3" id="startButtonCol">
          <b-button
          class="rounded-circle startingButton "
          type="submit"
          :disabled="name.length < 1 || sessionID.length < 1"
          @click="onClickButton"
        >
          {{ buttonText }}
        </b-button>
        </b-col>
      </b-row>
    </div>
    <div class="col-lg-3 col-md-2 col-sm-1" />
    <b-col />
  </b-row>
</template>

<script lang="ts">
import Vue from "vue";
import JoinCommand from "../model/JoinCommand";
import RoundedAvatar from "./RoundedAvatar.vue";

export default Vue.extend({
  name: "JoinPageCard",
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
      sessionID: "",
      password: "",
      name: "",
    };
  },
  created() {
    if (this.sessionIdFromUrl) {
      this.sessionID = this.sessionIdFromUrl;
    }
    window.addEventListener("keyup", (event) => {
      if (event.keyCode === 13) {
        this.onClickButton();
      }
    });
  },
  mounted() {
    this?.$refs?.name?.["$el"]?.focus();
  },
  methods: {
    onClickButton() {
      if (this.name.length > 1 && this.sessionID.length > 1) {
        const data: JoinCommand = {
          sessionID: this.sessionID.split(" ").join(""),
          password: this.password,
          name: this.name.trim(),
        };
        this.$emit("clicked", data);
      }
    },
  },
});
</script>

<style scoped>
#join-card {
  border-radius: 20px;
  vertical-align: middle;
  margin-bottom: 3%;
}

h6 {
  font-weight: 700;
}

.startingButton {
  background-color: var(--startButton);
  color: var(--text-primary-color);
  border-radius: var(--buttonShape);
  min-height: 75px;
  min-width: 75px;
  margin-top: 16%;
  font-size: xx-large;
  white-space: nowrap;
}

.startingButton:hover {
  background-color: var(--startButtonHovered);
  color: var(--text-primary-color);
}

.startingButton:focus {
  background-color: var(--startButtonHovered) !important;
  color: var(--text-primary-color);
}

.startingButton:hover:disabled {
  background-color: grey;
  color: var(--text-primary-color);
}

.inputText {
  color: black;
}

.inputFields {
  border-radius: var(--buttonShape);
}

#startButtonCol{
  text-align: center;
}
</style>
