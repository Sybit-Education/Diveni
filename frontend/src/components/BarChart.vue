<template>
    <Bar id="my-chart-id" :options="chartOptions" :data="chartData"  />
</template>

<script lang="ts">
  import { Bar } from 'vue-chartjs'
  import { Chart as ChartJS, Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale } from 'chart.js'
  import Vue from "vue";

  ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale);
  export default Vue.extend({
    name: 'BarChart',
    components: { Bar },
    props: {
        overAll: { type: Number, required: true },
        lastMonth: { type: Number, required: true },
        currently: { type: Number, required: true },
        overAllAttendees: { type: Number, required: true },
        lastMonthAttendees: { type: Number, required: true },
        currentlyAttendees: { type: Number, required: true },
    },
    data() {
      return {
        chartData: {
          labels: [ 'Overall', 'Last Month', 'Currently'],
          datasets: [
            {
              type: 'bar',
              label: "Session Amount",
              backgroundColor: '#30a444',
              data: [this.overAll, this.lastMonth, this.currently],
              stack: 'Stack 1'
            },
            {
              type: 'bar',
              label: "Attendees Amount",
              backgroundColor: '#3079a4',
              data: [this.overAllAttendees, this.lastMonthAttendees, this.currentlyAttendees],
              stack: 'Stack 2'
            },
          ]
        },
        chartOptions: {
          responsive: true,
          maintainAspectRatio: true,
          plugins: {
            legend: {
              position: 'bottom'
            },
          }

        }
      }
    }
  })
</script>
