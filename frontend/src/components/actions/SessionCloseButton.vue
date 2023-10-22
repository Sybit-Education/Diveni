<template>
  <div v-if="isPlanningStart" class="img-holderClose">
    <div id="picture-holderClose">
      <b-img :src="require('@/assets/LeaveButton.png')" id="pandaPictureClose"/>
    </div>
    <b-button
     v-b-modal.close-session-modal
      variant="danger"
      class="button"
    >
      <b-icon-x />
      {{ $t("page.session.during.estimation.buttons.finish") }}
    </b-button>
    <b-modal
      id="close-session-modal"
      :title="$t('page.session.close.popup')"
      :cancel-title="$t('page.session.close.button.cancel')"
      :ok-title="$t('page.session.close.button.ok')"
      @ok="closeSession"
    >
      <p class="my-4 description">
        {{ $t("page.session.close.description1") }}
      </p>
      <p v-if="userStoryMode !== 'NO_US'" class="description">
        {{ $t("page.session.close.description2") }}
      </p>
    </b-modal>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import Constants from "@/constants";

export default Vue.extend({
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
  methods: {
    closeSession() {
      this.sendCloseSessionCommand();
      window.localStorage.removeItem("adminCookie");
      if (this.userStoryMode !== "NO_US") {
        this.$router.push({ name: "ResultPage" });
      } else {
        this.$store.commit("clearStore");
        this.$router.push({ name: "LandingPage" });
      }
    },
    sendCloseSessionCommand() {
      const endPoint = `${Constants.webSocketCloseSessionRoute}`;
      this.$store.commit("sendViaBackendWS", { endPoint });
    },
  },
});
</script>
<style>

.description {
  color: black;
}

#picture-holderClose {
  width: auto;
  height: 90px;
}

#pandaPictureClose {
  position: absolute;
  left: 65%;
  height: 15%;
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
    border-radius:var(--buttonShape);
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
    border-radius:  var(--buttonShape);
    bottom: 0px;
    white-space: nowrap;
}
</style>
