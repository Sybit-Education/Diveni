<template>
  <div class="textDescription">
    <h5>
      <i>{{ $t("session.prepare.step.selection.mode.description.withUS.subtitle") }}</i>
    </h5>
    <ul>
      <li>{{ $t("session.prepare.step.selection.mode.description.withUS.descriptionLine1") }}</li>
      <li>{{ $t("session.prepare.step.selection.mode.description.withUS.descriptionLine2") }}</li>
      <li>{{ $t("session.prepare.step.selection.mode.description.withUS.descriptionLine3") }}</li>
      <li>{{ $t("session.prepare.step.selection.mode.description.withUS.descriptionLine4") }}</li>
      <li>{{ $t("session.prepare.step.selection.mode.description.withUS.descriptionLine5") }}</li>
    </ul>
  </div>
</template>

<script lang="ts">
import Vue from "vue";

export default Vue.extend({
  name: "UserStoryComponent",
});
</script>

<!-- Add "scoped" attribute to limit CSS/SCSS to this component only -->
<style lang="scss" scoped>
.textDescription {
  color: var(--text-primary-color) !important;
}
</style>
