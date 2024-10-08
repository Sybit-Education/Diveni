<template>
  <div v-if="isPlanningStart" class="img-holderClose">
    <div id="picture-holderClose">
      <b-img id="pandaPictureClose" :src="require('@/assets/LeaveButton.png')" />
    </div>
    <b-button v-b-modal.close-session-modal variant="danger" class="button">
      <b-icon-x />
      {{ t("page.session.during.estimation.buttons.finish") }}
    </b-button>
    <b-modal
      id="close-session-modal"
      class="modal-header"
      :title="t('page.session.close.popup')"
      :cancel-title="t('page.session.close.button.cancel')"
      :ok-title="t('page.session.close.button.ok')"
      @ok="closeSession"
    >
      <p class="modal-body my-4 description">
        {{ t("page.session.close.description1") }}
      </p>
      <p v-if="userStoryMode !== 'NO_US'" class="description">
        {{ t("page.session.close.description2") }}
      </p>
    </b-modal>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import Constants from "@/constants";
import { useDiveniStore } from "@/store";
import { useI18n } from "vue-i18n";
import { useRouter } from "vue-router";

export default defineComponent({
  name: "SessionCloseButton",
  props: {
    isPlanningStart: {
      type: Boolean,
      required: true,
    },
    userStoryMode: {
      type: String,
      required: true,
    },
  },
  setup() {
    const store = useDiveniStore();
    const { t } = useI18n();
    const router = useRouter();
    return { store, t, router };
  },
  methods: {
    closeSession() {
      this.sendCloseSessionCommand();
      window.localStorage.removeItem("adminCookie");
      if (this.userStoryMode !== "NO_US") {
        this.router.push({ name: "ResultPage" });
      } else {
        this.store.clearStore();
        this.router.push({ name: "LandingPage" });
      }
    },
    sendCloseSessionCommand() {
      const endPoint = `${Constants.webSocketCloseSessionRoute}`;
      this.store.sendViaBackendWS(endPoint);
    },
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS/SCSS to this component only -->
<style lang="scss">
#picture-holderClose {
  width: auto;
  height: 90px;
}

#pandaPictureClose {
  position: absolute;
  left: 65%;
  height: 30px;
  user-select: none;
}

.img-holderClose {
  position: relative;
  width: auto;
}

.img-holderClose .button {
  position: absolute !important;
  text-align: left;
  left: 13.5%;
  bottom: 28%;
  white-space: nowrap;
  max-height: 40px;
}

#picture-holderLeave {
  width: 147px;
  height: 65px;
}

#pandaPictureLeave {
  position: absolute;
  left: 60%;
  height: 30px;
  user-select: none;
}

.img-holderLeave {
  position: relative;
}

.img-holderLeave .button {
  position: absolute !important;
  text-align: left;
  max-height: 40px;
  bottom: 0;
  white-space: nowrap;
}
</style>
