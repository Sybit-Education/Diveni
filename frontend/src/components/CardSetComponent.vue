<template>
  <div>
    <b-row>
      <b-col>
        <div class="d-flex justify-content-around">
          <div
            v-for="item of allCardSets" id="swipe-card"
            :key="item.name"
            :style="`background-color: ${selectedCardSet.name === item.name ? '#198754': 'lightgrey'}`"
            @click="onCardSetSelected(item)"
          >
            <div id="text" style="padding-top:16px ">
              {{ item.name }}
            </div>
            <div style="padding:16px; text-align: center;">
              {{ item.description }}
            </div>
          </div>
        </div>
      </b-col>
    </b-row>
    <b-row v-if="selectedCardSet.name !== ''">
      <div class="text-center mt-3">
        <b-button
          v-for="item in selectedCardSet.values"
          :key="item" class="m-1" pill
          :variant="isActiveCardSetNumber(item)"
          style="width: 60px"
          @click="onCardSetNumberSelected(item)"
        >
          {{ item }}
        </b-button>
      </div>
    </b-row>
  </div>
</template>

<script lang="ts">
import Vue from 'vue';

export default Vue.extend({
  name: 'CardSetComponent',
  data() {
    return {
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
    isActiveCardSetNumber(num) {
      return this.selectedCardSet.activeValues.includes(num) ? 'success' : 'outline-secondary';
    },
    onCardSetSelected(set) {
      this.selectedCardSet = set;
      this.emitChanges();
    },
    emitChanges() {
      this.$emit('selectedCardSetOptions', this.selectedCardSet.activeValues);
    },
    onCardSetNumberSelected(number) {
      if (this.selectedCardSet.activeValues.includes(number)) {
        this.selectedCardSet.activeValues = this.selectedCardSet.activeValues.filter((num) => num !== number);
      } else {
        this.selectedCardSet.activeValues.push(number);
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
