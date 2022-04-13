<template>
  <div
    :id="`parent${memberId}`"
    class="rounded-circle parent"
    :style="`background-color: ${color}`"
  >
    <div :id="`column`" class="text-center">
      <img :src="require(`@/assets/${assetName}`)" width="44" />
      <div v-if="showName" id="name">
        {{ name }}
      </div>
    </div>
    <b-popover :target="`parent${memberId}`" triggers="hover" placement="top" class="popover-title">
      <b-button class="rounded-circle px-2" variant="danger">
        <b-icon icon="x" scale="2" @click="openModal" />
      </b-button>
    </b-popover>
    <b-modal :id="`modal${memberId}`">
      <template #modal-header>
        <h4>{{ $t("page.session.during.modal.title") }}</h4>
      </template>
      {{ $t("page.session.during.modal.bodyPart1") }}
      <b>{{ name }}</b>
      {{ $t("page.session.during.modal.bodyPart2") }}
      <template #modal-footer>
        <b-button @click="closeModal">
          {{ $t("page.session.during.modal.buttons.cancel") }}
        </b-button>
        <b-button variant="danger" @click="removeMember">
          {{ $t("page.session.during.modal.buttons.ok") }}
        </b-button>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import Constants from "@/constants";

export default Vue.extend({
  name: "RoundedAvatar",
  props: {
    memberId: { type: String, default: "" },
    color: { type: String, required: true },
    assetName: { type: String, required: true },
    showName: { type: Boolean, required: true },
    name: { type: String, required: true },
  },
  methods: {
    openModal() {
      this.$root.$emit('bv::hide::popover');
      this.$root.$emit('bv::show::modal', `modal${this.memberId}`, '#btnShow');
    },
    closeModal() {
      this.$root.$emit('bv::hide::modal', `modal${this.memberId}`, '#btnShow');
    },
    removeMember() {
      const endPoint = Constants.webSocketKickMemberRoute;
      this.$store.commit("sendViaBackendWS", { endPoint, data: this.memberId })
      this.closeModal();
    },
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.parent {
  width: 90px;
  height: 90px;
  padding-left: 8px;
  padding-bottom: 8px;
  padding-right: 8px;
  padding-top: 8px;
  display: flex;
  justify-content: center; /* Centering y-axis */
  align-items: center; /* Centering x-axis */
}
.popover {
  background: transparent;
  border: transparent;
}
</style>
