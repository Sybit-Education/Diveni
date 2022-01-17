<template>
  <div>
    <h5>{{ $t("session.prepare.step.selection.mode.description.withJira.subtitle") }}</h5>
    <ul>
      <li>{{ $t("session.prepare.step.selection.mode.description.withJira.descriptionLine1") }}</li>
      <li>{{ $t("session.prepare.step.selection.mode.description.withJira.descriptionLine2") }}</li>
      <li>
        {{ $t("session.prepare.step.selection.mode.description.withJira.descriptionLine3") }}
      </li>
      <li>{{ $t("session.prepare.step.selection.mode.description.withJira.descriptionLine4") }}</li>
    </ul>
  </div>
</template>

<script lang="ts">
import Vue from "vue";

export default Vue.extend({
  name: "JiraComponent",
});
</script>
