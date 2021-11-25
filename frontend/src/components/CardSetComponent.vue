<template>
  <div class="card-group">
    <b-card
      v-for="item of cardSets" :key="item.name"
      class="my-4 text-center card" :style="`background-color: ${ item.name === selectedCardSet ? '#5cb85c' : 'lightgray' }`"
      @click="clicked(item.name)"
    >
      <h4> {{ item.name }} </h4>
      <b-card-text>
        {{ item.description }}
      </b-card-text>
    </b-card>
  </div>
</template>

<script lang="ts">
import Vue from 'vue';

export default Vue.extend({
  name: 'CardSetComponent',
  data() {
    return {
      selectedCardSet: '',
      cardSets: [
        {
          name: 'Fibonacci',
          description: 'Each number is the sum of the two preceding ones. Great for estimating complex stories ...',
          color: 'lightgray',
        },
        {
          name: 'T-shirt sizes',
          description: 'Basic T-shirt sizes, like XS, S or XL. Simple and well known ...',
          color: 'lightgray',
        },
        {
          name: 'Hours',
          description: 'Specific and flexible. Perfect for estimating sub tasks...',
          color: 'lightgray',
        },
      ],
    };
  },
  methods: {
    clicked(name) {
      this.selectedCardSet = name;
      this.$emit('selectedCardSet', { name });
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
</style>
