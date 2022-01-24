<template>
  <h4>
    {{ textBeforeSessionID }}
    <b-link id="popover-link" href="" @click="copyLinkToClipboard()">
      {{ sessionId }}
    </b-link>
    {{ textAfterSessionID }}
    <b-popover
      id="popover"
      target="popover-link"
      triggers="hover"
      placement="top"
    >
      <b-button class="mx-1" variant="success" @click="copyIdToClipboard()">
        {{ $t("page.session.before.copy.id") }}
      </b-button>
      <b-button class="mx-1" variant="success" @click="copyLinkToClipboard()">
        {{ $t("page.session.before.copy.link") }}
      </b-button>
    </b-popover>
  </h4>
</template>

<script lang="ts">
import Vue from "vue";

export default Vue.extend({
  name: "CopySessionIdPopup",
  props: {
    textBeforeSessionID: { type: String, required: false, default: "" },
    sessionId: { type: String, required: true },
    textAfterSessionID: { type: String, required: false, default: "" },
  },
  methods: {
    copyIdToClipboard() {
      navigator.clipboard.writeText(this.sessionId).then(
        () => {
          console.log("Copying to clipboard was successful!");
        },
        (err) => {
          console.error("Could not copy text: ", err);
        }
      );
    },
    copyLinkToClipboard() {
      navigator.clipboard
        .writeText(
          `${document.URL.toString().replace("session", "join?sessionID=")}${
            this.sessionId
          }`
        )
        .then(
          () => {
            console.log("Copying to clipboard was successful!");
          },
          (err) => {
            console.error("Could not copy text: ", err);
          }
        );
    },
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
#popover {
  max-width: 400px;
}
</style>
