<template>
  <div :id="`user${member.memberID}`" class="component" :style="{ width: width }">
    <Component :is="child" :member="member" :props="props" />
    <b-popover
      :target="`user${member.memberID}`"
      triggers="hover"
      placement="top"
      boundary="viewport"
    >
      <b-button v-b-modal="`modal-${member.memberID}`" class="rounded-circle px-2" variant="danger">
        <b-icon icon="x" scale="2" />
      </b-button>
    </b-popover>
    <b-modal
      :id="`modal-${member.memberID}`"
      class="modal-header"
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

export default defineComponent({
  components: {
    RoundedAvatar: () => import("@/components/RoundedAvatar.vue"),
    SessionMemberCard: () => import("@/components/SessionMemberCard.vue"),
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
</style>
