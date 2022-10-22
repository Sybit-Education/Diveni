<template>
  <div>
    <h5>
      <i>{{
        $t("session.prepare.step.selection.mode.description.withUS.subtitle")
      }}</i>
    </h5>
    <ul>
      <li>
        {{
          $t(
            "session.prepare.step.selection.mode.description.withUS.descriptionLine1"
          )
        }}
      </li>
      <li>
        {{
          $t(
            "session.prepare.step.selection.mode.description.withUS.descriptionLine2"
          )
        }}
      </li>
      <li>
        {{
          $t(
            "session.prepare.step.selection.mode.description.withUS.descriptionLine3"
          )
        }}
      </li>
      <li>
        {{
          $t(
            "session.prepare.step.selection.mode.description.withUS.descriptionLine4"
          )
        }}
      </li>
      <li>
        {{
          $t(
            "session.prepare.step.selection.mode.description.withUS.descriptionLine5"
          )
        }}
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
  name: "UserStoryComponent",
});
</script>
