<template>
  <div>
    <h5>Select Jira for your Poker-Planning</h5>
    <ul>
      <li>Please authenticate with Jira</li>
      <li>Select your project</li>
      <li>
        After you have selected your project, you will be redirected to the next page to edit your
        user stories
      </li>
      <li>You will be able to add and edit User Stories once you've started the session</li>
    </ul>
  </div>
</template>

<script lang="ts">
import Vue from "vue";

export default Vue.extend({
  name: "JiraComponent",
});
</script>
