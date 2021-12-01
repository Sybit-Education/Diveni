<template>
<b-container class="bv-example-row bv-example-row-flex-cols">
  <b-row align-v="stretch">
    <b-col
    :style="backgroundcolorIsSet ? 'background-color: #aaaaaa' : 'background-color: #ffffff'"
    class="p-3 mb-2 text-white rounded"
    @click="doTheThing()"> hey </b-col>
  </b-row>
</b-container>
</template>

<script lang="ts">
import Vue from 'vue';

export default Vue.extend({
  name: 'DesktopCard',
  props: {
    cardSet: { type: Array, required: true },
  },
  data() {
    return {
      set: this.cardSet,
      backgroundcolorIsSet: true,
      selectedCardSet: {
        name: '',
        values: [],
        activeValues: [] as string[],
      },
      allCardSets: [
        {
          name: 'Fibonacci',
          description: 'Each number is the sum of the two preceding ones',
          values: ['1', '2', '3', '5', '8', '13', '21', '34', '55'],
          activeValues: ['1', '2', '3', '5', '8', '13', '21'],
        },
        {
          name: 'T-shirt sizes',
          description: 'Basic T-shirt sizes. Simple and effective',
          values: ['XXS', 'XS', 'S', 'M', 'L', 'XL', 'XXL', 'XXXL'],
          activeValues: ['XS', 'S', 'M', 'L', 'XL'],
        },
        {
          name: 'Hours',
          description: 'Simply hours. Great for sub tasks.',
          values: ['1', '2', '3', '4', '5', '6', '8', '10', '12', '16'],
          activeValues: ['1', '2', '3', '4', '5', '6', '8'],
        },
      ],
    };
  },
  methods: {
    doTheThing() {
      this.backgroundcolorIsSet = !this.backgroundcolorIsSet;
    },
    isActiveCardSetNumber(num) {
      return this.selectedCardSet.activeValues.includes(num) ? 'success' : 'outline-secondary';
    },
    onCardSetSelected(set) {
      this.selectedCardSet = set;
      this.emitChanges();
    },
    emitChanges() {
      this.$emit('selectedCardSetNumbers', this.selectedCardSet.activeValues);
    },
    onCardSetNumberSelected(number) {
      if (this.selectedCardSet.activeValues.includes(number)) {
        this.selectedCardSet.activeValues = this.selectedCardSet.activeValues.filter((num) => num !== number);
      } else {
        const newActiveValues = [...this.selectedCardSet.activeValues, number];
        this.selectedCardSet.activeValues = this.selectedCardSet.values.filter((num) => newActiveValues.includes(num));
      }
      this.emitChanges();
    },
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.card {
  border-radius: 25px;
  cursor: pointer;
}
#swipe-card{
  width: 168px;
  height: 202px;
  justify-content: start;  /* Centering y-axis */
  align-items :center; /* Centering x-axis */
  border-radius: 5%;
  display: flex;
  flex-direction: column;
}
#text {
  font-size: 20px;
  font-weight: 500;
}
</style>
