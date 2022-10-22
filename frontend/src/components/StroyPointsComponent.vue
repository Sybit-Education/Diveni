<template>
  <div>
    <h5>
      <i>{{
        $t("session.prepare.step.selection.mode.description.withoutUS.subtitle")
      }}</i>
    </h5>
    <ul>
      <li>
        {{
          $t(
            "session.prepare.step.selection.mode.description.withoutUS.descriptionLine1"
          )
        }}
      </li>
      <li>
        {{
          $t(
            "session.prepare.step.selection.mode.description.withoutUS.descriptionLine2"
          )
        }}
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
  name: "StoryPointsComponent",
});
</script>
