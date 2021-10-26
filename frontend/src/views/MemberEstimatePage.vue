<template>
  <div>
    <b-container>
      <div>
        <h1 class="my-5 mx-2">
          {{ title }}
        </h1>
        <b-row class="justify-content-center">
          <rounded-avatar
            :color="hexColor"
            :asset-name="animalAssetName"
            :show-name="true"
            :name="name"
          />
        </b-row>
      </div>
      <b-row class="my-5 ">
        <flicking
          style="overflow:visible; width: 100vw; position: relative; left: calc(-50vw + 50%);"

          :options="{ renderOnlyVisible: false,
                      horizontal:true,
                      align: 'center',
                      bound: false,
                      defaultIndex: 0,
                      deceleration: 0.0005 }"
        >
          <member-estimate-card
            v-for="number in numbers"
            :key="number"
            :ref="`memberCard${number}`"
            class="flicking-panel mx-2"
            :number="number"
            :hex-color="hexColor"
            :dragged="number == draggedNumber"
            @sentEstimation="onSentEstimation"
          />
        </flicking>
      </b-row>
    </b-container>
  </div>
</template>

<script lang="ts">
import Vue from 'vue';
import * as Constants from '../constants';
import RoundedAvatar from '../components/RoundedAvatar.vue';
import MemberEstimateCard from '../components/MemberEstimateCard.vue';

export default Vue.extend({
  name: 'MemberEstimatePage',
  components: {
    RoundedAvatar,
    MemberEstimateCard,
  },
  props: {
    sessionID: {
      type: String,
      default: undefined,
    },
    disabled: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      title: 'Estimate!',
      hexColor: Constants.default.getRandomPastelColor(),
      animalAssetName: Constants.default.getRandomAvatarAnimalAssetName(),
      name: 'linda',
      numbers: [1, 2, 3, 5, 8, 13, 21, 34],
      draggedNumber: null,
    };
  },
  methods: {
    onSentEstimation({ estimation }) {
      this.draggedNumber = estimation;
      console.log(`TODO: send estimation to backend ${estimation}`);
    },
  },
});
</script>
