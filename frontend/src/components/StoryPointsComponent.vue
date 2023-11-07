<template>
  <div id="textDescription">
    <h5>
      <i>{{ $t("session.prepare.step.selection.mode.description.withoutUS.subtitle") }}</i>
    </h5>
    <ul>
      <li>
        {{ $t("session.prepare.step.selection.mode.description.withoutUS.descriptionLine1") }}
      </li>
      <li>
        {{ $t("session.prepare.step.selection.mode.description.withoutUS.descriptionLine2") }}
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
import Vue from "vue";

export default Vue.extend({
  name: "StoryPointsComponent",
});
</script>

<!-- Add "scoped" attribute to limit CSS/SCSS to this component only -->
<style lang="scss" scoped>
#textDescription {
  color: var(--text-primary-color);
}
</style>
