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
        <b-col>
          <h6>{{ $t("page.join.input.name") }}</h6>
          <b-form-input ref="name" v-model="name" class="mt-3" type="text" />
        </b-col>
      </b-row>
      <b-row class="mt-4">
        <b-col cols="12" :md="'6'">
          <h6>{{ $t("page.join.input.code") }}</h6>
          <b-form-input v-model="sessionID" class="mt-3" type="text" />
        </b-col>
        <b-col class="mt-2 mt-md-0" cols="12" md="6">
          <h6>{{ $t("page.join.input.password") }}</h6>
          <b-form-input v-model="password" class="mt-3" type="password" placeholder="(optional)" />
        </b-col>
      </b-row>
      <b-row v-if="jobTitleRequired !== ''" class="mt-2">
        <b-col>
          <h6>Job-Title</h6>
          <autocomplete v-model="jobTitle" :items="getJobTitles"></autocomplete>
        </b-col>
      </b-row>
      <b-row>
        <b-button
          class="mt-5"
          variant="success"
          type="submit"
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
import Vue from "vue";
import JoinCommand from "../model/JoinCommand";
import RoundedAvatar from "./RoundedAvatar.vue";
import autocomplete from "vue2-autocomplete-input-tag";

export default Vue.extend({
  name: "JoinPageCard",
  components: {
    RoundedAvatar,
    autocomplete,
  },
  props: {
    color: { type: String, required: true },
    animalAssetName: { type: String, required: true },
    buttonText: { type: String, required: true },
    sessionIdFromUrl: { type: String, required: true },
    jobTitleRequired: { type: String, required: true },
  },
  data() {
    return {
      sessionID: "",
      password: "",
      name: "",
      jobTitle: "",
    };
  },
  computed: {
    getJobTitles(): Array<string> {
      // evenutell kann der Admin dann auch die Jobs verschicken über Sessionconfig ?
      let jobTitles = Array.of("Frontend", "Backend", "FullStack", "Product Owner", "Scrummaster");
      return jobTitles;
    },
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
        console.log("JoinPageCard: ", this.jobTitle);
        const data: JoinCommand = {
          sessionID: this.sessionID.split(" ").join(""),
          password: this.password,
          name: this.name.trim(),
          jobTitle: this.jobTitle,
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
}

h6 {
  font-weight: 700;
}
</style>

<style>
input.autocomplete {
  width: 100%;
  border: 1px solid #ccc;
  color: #666;
  border-radius: 5px;
  outline: none;
  padding: 7px 14px;
  box-sizing: border-box;
  font-size: 15px;
}
.vue2-autocomplete-input-tag-items {
  border: 1px solid #ccc;
  max-height: 200px;
  margin-top: 8px;
  width: 100%;
  background-color: white;
  border-radius: 8px;
  overflow: auto;
}
.vue2-autocomplete-input-tag-item {
  padding: 6px 16px;
  color: #4a4a4a;
  max-width: 100%;
  cursor: pointer;
  text-align: left;
  font-size: 14px;
}
.vue2-autocomplete-input-tag-item:hover {
  background-color: #e8e8e8;
}
</style>
