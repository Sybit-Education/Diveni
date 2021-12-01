<template>
  <b-container>
    <h1 class="my-5 mx-2"> {{ titleResult }} </h1>
    <b-list-group>
      <b-list-item v-for="story of userStories" :key="story.title">
        story
      </b-list-item>
    </b-list-group>
  </b-container>
</template>

<script lang="ts">
import Vue from 'vue';

export default Vue.extend({
  name: 'SessionPage',
  components: {},
  data() {
    return {
      titleResult: 'Results',
    };
  },
  computed: {
    userStories() {
      return this.$store.state.userStories;
    },
  },
  methods: {
    retriggerTimer() {
      console.log();
    },
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>
