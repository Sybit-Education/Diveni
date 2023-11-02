<template>
  <div id="textDescription">
    <h5>
      <i>{{ t("session.prepare.step.selection.mode.description.withoutUS.subtitle") }}</i>
    </h5>
    <ul>
      <li>
        {{ t("session.prepare.step.selection.mode.description.withoutUS.descriptionLine1") }}
      </li>
      <li>
        {{ t("session.prepare.step.selection.mode.description.withoutUS.descriptionLine2") }}
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import { useI18n } from "vue-i18n";

export default defineComponent({
  name: "StoryPointsComponent",
  setup() {
    const { t } = useI18n();
    return { t };
  },
});
</script>

<style scoped>
#textDescription {
  color: var(--text-primary-color);
}
</style>
