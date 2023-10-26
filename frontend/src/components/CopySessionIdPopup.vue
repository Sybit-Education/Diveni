<template>
  <div class="copy-session">
    {{ textBeforeSessionID }}
    <strong>
      <b-link id="popover-link" href="#" @click="copyLinkToClipboard()">
        {{ sessionId }}
      </b-link>
    </strong>
    {{ textAfterSessionID }}
    <b-popover id="popover" target="popover-link" triggers="hover" placement="top">
      <b-button
        id="sessionCode"
        class="mx-1"
        @click="
          copyIdToClipboard();
          $event.target.blur();
        "
      >
        {{ $t("page.session.before.copy.id") }}
      </b-button>
      <b-button
        id="link"
        class="mx-1"
        @click="
          copyLinkToClipboard();
          $event.target.blur();
        "
      >
        {{ $t("page.session.before.copy.link") }}
      </b-button>
      <b-button
        id="qrCode"
        class="mx-1"
        @click="
          $bvModal.show('qr-modal');
          $event.target.blur();
        "
      >
        {{ $t("page.session.before.copy.qr") }}
      </b-button>
    </b-popover>
    <b-modal id="qr-modal" ok-only>
      <template #modal-header>
        <h3>QR code</h3>
      </template>
      <qrcode-vue :value="sessionLink" size="400" class="qr-code" />
    </b-modal>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import QrcodeVue from "qrcode.vue";

export default Vue.extend({
  name: "CopySessionIdPopup",
  components: {
    QrcodeVue,
  },
  props: {
    textBeforeSessionID: { type: String, required: false, default: "" },
    sessionId: { type: String, required: true },
    textAfterSessionID: { type: String, required: false, default: "" },
  },
  data: () => ({
    canCopy: false,
  }),
  computed: {
    sessionLink(): string {
      return `${document.URL.toString().replace("session", "join?sessionID=")}${this.sessionId}`;
    },
  },
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
      const text = this.sessionLink;
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
.copy-session {
  font-size: 1.75rem;
  color: var(--text-primary-color);
}

#popover {
  max-width: 500px;
  background-color: var(--popUpMenu);
}

#popover-link {
  color: var(--linkColor);
}

.qr-code {
  display: table;
  margin: 0 auto;
}

#sessionCode {
  border-radius: var(--buttonShape);
  background-color: var(--startButton);
  color: var(--text-primary-color);
}

#sessionCode:hover {
  background-color: var(--startButtonHovered);
  color: var(--text-primary-color);
}

#sessionCode:focus {
  background-color: var(--startButtonHovered) !important;
  color: var(--text-primary-color);
}

#link {
  border-radius: var(--buttonShape);
  background-color: var(--joinButton);
  color: var(--text-primary-color);
}

#link:hover {
  background-color: var(--joinButtonHovered);
  color: var(--text-primary-color);
}

#link:focus {
  background-color: var(--joinButtonHovered) !important;
  color: var(--text-primary-color);
}

#qrCode {
  border-radius: var(--buttonShape);
  background-color: var(--reconnectButton);
  color: var(--text-primary-color);
}

#qrCode:hover {
  background-color: var(--reconnectButtonHovered);
  color: var(--text-primary-color);
}

#qrCode:focus {
  background-color: var(--reconnectButtonHovered) !important;
  color: var(--text-primary-color);
}
</style>
