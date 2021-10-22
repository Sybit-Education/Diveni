<template>
  <div>
    <h1 class="my-5 mx-2">
      {{ title }}
    </h1>
    <b-container>
      <join-page-card
        :show="true"
        :color="getRandomColor()"
        :animal-asset-name="'animal-icon-5.png'"
        :button-text="'GO'"
        :on-click="() => {}"
      />
    </b-container>
  </div>
</template>

<script lang="ts">
import Vue from 'vue';
import JoinPageCard from '../components/JoinPageCard.vue';
import * as Constants from '../constants';

export default Vue.extend({
  name: 'JoinPage',
  components: {
    JoinPageCard,
  },
  data() {
    return {
      title: 'Join a meeting ...',
    };
  },
  methods: {
    getRandomColor() {
      return Constants.default.getRandomPastelColor();
    },
  },
});
</script>
