<template>
  <div :id="`user${member.memberID}`" class="component" :style="{ width: width }">
    <Component :is="child" :member="member" :props="props" />
    <b-popover
      :target="`user${member.memberID}`"
      triggers="hover"
      placement="top"
      boundary="viewport"
    >
      <b-button class="rounded-circle px-2" variant="danger" @click="openModal">
        <b-icon icon="x" scale="2" />
      </b-button>
    </b-popover>
    <b-modal :id="`modal${member.memberID}`">
      <template #modal-header>
        <h4>{{ $t("page.session.during.modal.title") }}</h4>
      </template>
      {{ $t("page.session.during.modal.bodyPart1") }}
      <b>{{ member.name }}</b>
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
  components: {
    RoundedAvatar: () => import("@/components/RoundedAvatar.vue"),
    SessionMemberCard: () => import("@/components/SessionMemberCard.vue"),
  },
  props: {
    child: { type: String, required: true },
    member: { type: Object, required: true },
    props: { type: Object, required: false, default: () => ({}) },
  },
  computed: {
    width() {
      switch (this.child) {
        case "RoundedAvatar":
          return "100px";
        case "SessionMemberCard":
          return "190px";
        default:
          return "100px";
      }
    },
  },
  methods: {
    openModal() {
      this.$root.$emit("bv::hide::popover");
      this.$root.$emit("bv::show::modal", `modal${this.member.memberID}`, "#btnShow");
    },
    closeModal() {
      this.$root.$emit("bv::hide::modal", `modal${this.member.memberID}`, "#btnShow");
    },
    removeMember() {
      const endPoint = Constants.webSocketKickMemberRoute;
      this.$store.commit("sendViaBackendWS", { endPoint, data: this.member.memberID });
      this.closeModal();
    },
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS/SCSS to this component only -->
<style lang="scss" scoped>
.popover {
  background: transparent;
  border: transparent;
}
.component {
  display: grid;
  justify-content: center;
  align-items: center;
}
</style>
