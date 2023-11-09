<template>
  <div
    :style="'background-color: #30a444;'"
    class="session-member-card text-center card m-2"
    :class="{ greyOut: highlight === false && estimateFinished }"
  >
    <h1 class="fs-3-rem">
      <strong v-if="!estimateFinished">?</strong>
      <strong v-if="currentEstimation === '' && estimateFinished">-</strong>
      <strong v-if="currentEstimation !== null && estimateFinished">{{ currentEstimation }}</strong>
    </h1>
    <div class="rounded-avatar rounded-circle" :style="'background-color: #30a444;'">
      <div :id="'avatar'" class="text-center">
        <b-img :src="require(`@/assets/crown.png`)" class="rounded-avatar__image" />
        <div class="rounded-avatar__label">
          <b id="hostName">Host</b>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import Vue from "vue";

export default Vue.extend({
  name: "SessionAdminCard",
  props: {
    currentEstimation: { type: String, required: true },
    estimateFinished: { type: Boolean, required: true },
    highlight: { type: Boolean, required: false },
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS/SCSS to this component only -->
<style lang="scss" scoped>
.fs-3-rem {
  font-size: 4rem;
}
.card {
  position: relative;
  width: 168px;
  height: 222px;
  justify-content: center; /* Centering y-axis */
  align-items: center; /* Centering x-axis */
  border-radius: 5%;
  display: flex;
  flex-direction: column;
}
h4 {
  text-overflow: ellipsis;
  white-space: nowrap;
  overflow: hidden;
  width: 100px;
}
.rounded-avatar {
  width: 150px;
  height: 150px;
  padding: 8px;
  display: flex;
  justify-content: center; /* Centering y-axis */
  align-items: center; /* Centering x-axis */
  overflow: hidden;
}
.rounded-avatar__image {
  width: 50px;
}

.rounded-avatar__label {
  font-size: 0.75rem;
  font-weight: 400;
  text-overflow: ellipsis;
}

.greyOut {
  opacity: 0.5;
  transform: scale(0.8);
}

#hostName {
  font-size: large;
}
</style>
