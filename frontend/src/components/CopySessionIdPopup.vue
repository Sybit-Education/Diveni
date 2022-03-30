<template>
  <h4>
    {{ textBeforeSessionID }}
    <b-link id="popover-link" href="" @click="copyLinkToClipboard()">
      {{ sessionId }}
    </b-link>
    {{ textAfterSessionID }}
    <b-popover id="popover" target="popover-link" triggers="hover" placement="top">
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
  data: () => ({
    canCopy: false,
  }),
  mounted() {
    this.canCopy = !!navigator.clipboard;
  },
  methods: {
    copyIdToClipboard() {
      if (this.canCopy) {
        navigator.clipboard.writeText(this.sessionId).then(
          () => {
            console.log("Copying to clipboard was successful!");
          },
          (err) => {
            console.error("Could not copy text: ", err);
          }
        );
      } else {
        this.copyToClipboardAlternative(this.sessionId);
      }
    },
    copyLinkToClipboard() {
      const text = `${document.URL.toString().replace("session", "join?sessionID=")}${
        this.sessionId
      }`;
      if (this.canCopy) {
        navigator.clipboard.writeText(text).then(
          () => {
            console.log("Copying to clipboard was successful!");
          },
          (err) => {
            console.error("Could not copy text: ", err);
          }
        );
      } else {
        this.copyToClipboardAlternative(text);
      }
    },
    copyToClipboardAlternative(text) {
      const textarea = document.createElement("textarea");
      textarea.textContent = text;
      textarea.style.position = "fixed";
      document.body.appendChild(textarea);
      textarea.select();
      try {
        document.execCommand("copy");
      } catch (err) {
        console.error("Could not copy text: ", err);
      } finally {
        document.body.removeChild(textarea);
      }
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
