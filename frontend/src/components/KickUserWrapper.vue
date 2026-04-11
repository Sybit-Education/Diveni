<template>
  <div class="component" :style="{ width: width }">
    <div
      :id="targetId"
      class="member-card"
      :class="{ 'member-inactive': !member.isActive && child === 'RoundedAvatar' }"
    >
      <Component :is="child" :member="member" :props="props" />
    </div>
    <b-popover :target="targetId" hover placement="top" boundary="viewport">
      <b-button v-b-modal="`modal-${member.memberID}`" class="rounded-circle px-2" variant="danger">
        <i class="bi bi-x" style="font-size: 1.2rem"></i>
      </b-button>
    </b-popover>
    <b-modal
      :id="`modal-${member.memberID}`"
      lazy
      :title="t('page.session.during.modal.title')"
      :cancel-title="t('page.session.during.modal.buttons.cancel')"
      :ok-title="t('page.session.during.modal.buttons.ok')"
      @ok="removeMember"
    >
      {{ t("page.session.during.modal.bodyPart1") }}
      <b>{{ member.name }}</b>
      {{ t("page.session.during.modal.bodyPart2") }}
    </b-modal>
  </div>
</template>

<script lang="ts">
import { defineComponent, PropType } from "vue";
import Constants from "@/constants";
import { useDiveniStore } from "@/store";
import Member from "@/model/Member";
import { useI18n } from "vue-i18n";
import RoundedAvatar from "@/components/RoundedAvatar.vue";
import SessionMemberCard from "@/components/SessionMemberCard.vue";

export default defineComponent({
  components: {
    RoundedAvatar,
    SessionMemberCard,
  },
  props: {
    child: { type: String, required: true },
    member: { type: Object as PropType<Member>, required: true },
    props: { type: Object, required: false, default: () => ({}) },
  },
  setup() {
    const store = useDiveniStore();
    const { t } = useI18n();
    return { store, t };
  },
  computed: {
    targetId() {
      return `user${this.member.memberID}`;
    },
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
    removeMember() {
      const endPoint = Constants.webSocketKickMemberRoute;
      this.store.sendViaBackendWS(endPoint, this.member.memberID);
    },
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS/SCSS to this component only -->
<style lang="scss" scoped>
.component {
  display: grid;
  justify-content: center;
  align-items: center;
}

.member-card {
  position: relative;
}

.member-inactive {
  opacity: 0.5;
}

.member-inactive::after {
  content: "\F61B";
  font-family: "bootstrap-icons", sans-serif;
  position: absolute;
  top: 0;
  right: 0;
  font-size: 1.2rem;
  color: #dc3545;
  opacity: 1;
  pointer-events: none;
}
</style>
