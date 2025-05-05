<template>
  <b-modal
    v-model="isVisibleProxy"
    centered
    :title="t('page.session.during.modal.deleteTitle')"
    :ok-title="t('page.session.during.modal.buttons.ok')"
    :cancel-title="t('page.session.during.modal.buttons.cancel')"
    @ok="confirm"
    @cancel="cancel"
  >
    <p>{{ modalText }}</p>
  </b-modal>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import { useI18n } from "vue-i18n";

export default defineComponent({
  name: "UserStoryDeleteModal",
  props: {
    isVisible: { type: Boolean, required: true },
    storyMode: { type: String, required: true },
  },
  emits: ["update:isVisible", "confirmDelete"],
  setup() {
    const { t } = useI18n();
    return { t };
  },
  computed: {
    isVisibleProxy: {
      get(): boolean {
        return this.isVisible;
      },
      set(value: boolean) {
        this.$emit("update:isVisible", value);
      },
    },
    modalText(): string {
      return this.storyMode === "US_JIRA"
        ? this.t("page.session.during.modal.jiraDeleteInfo")
        : this.t("page.session.during.modal.defaultDeleteInfo");
    },
  },
  methods: {
    confirm() {
      this.$emit("confirmDelete");
      this.$emit("update:isVisible", false);
    },
    cancel() {
      this.$emit("update:isVisible", false);
    },
  },
});
</script>
