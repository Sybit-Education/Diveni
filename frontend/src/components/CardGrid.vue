<template>
  <b-row>
    <b-col
      v-for="item of allCardSets.values" id="click-card"
      :key="item.name"
      class="border border-primary"
      style="height: 400px; background-color: rgba(0, 0, 255, 0.1)"
      >
      <div id="text" style="padding-top:16px ">
              {{ item.name }}
            </div>
        </b-col
    >
    <b-col
      class="border border-primary"
      style="height: 400px; background-color: rgba(0, 0, 255, 0.1)"
      >Column</b-col
    >
    <b-col
      class="border border-primary"
      style="height: 400px; background-color: rgba(0, 0, 255, 0.1)"
      >Column</b-col
    >
    <div class="w-100"></div>
    <b-col
      class="border border-primary"
      style="height: 400px; background-color: rgba(0, 0, 255, 0.1)"
      >Column</b-col
    >
    <b-col
      class="border border-primary"
      style="height: 400px; background-color: rgba(0, 0, 255, 0.1)"
      >Column</b-col
    >
    <b-col
      class="border border-primary"
      style="height: 400px; background-color: rgba(0, 0, 255, 0.1)"
      >Column</b-col
    >
    <div class="w-100"></div>
    <b-col
      class="border border-primary"
      style="height: 400px; background-color: rgba(0, 0, 255, 0.1)"
      >Column</b-col
    >
    <b-col
      class="border border-primary"
      style="height: 400px; background-color: rgba(0, 0, 255, 0.1)"
      >Column</b-col
    >
    <b-col
      class="border border-primary"
      style="height: 400px; background-color: rgba(0, 0, 255, 0.1)"
      >Column</b-col
    >
  </b-row>
</template>

<script lang="ts">
import Vue from 'vue';

export default Vue.extend({
  name: 'CardGrid',
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
});
</script>

<style>
</style>
