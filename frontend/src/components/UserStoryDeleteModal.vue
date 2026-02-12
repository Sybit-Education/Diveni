<template>
  <b-modal
    :visible="isVisibleProxy"
    centered
    :title="$t('page.session.during.modal.deleteTitle')"
    :ok-title="$t('page.session.during.modal.buttons.ok')"
    :cancel-title="$t('page.session.during.modal.buttons.cancel')"
    @ok="confirm"
    @cancel="cancel"
  >
    <p>{{ modalText }}</p>

    <div class="form-check mt-3">
      <input
        id="dontAskAgainCheckbox"
        v-model="dontAskAgain"
        class="form-check-input"
        type="checkbox"
      />
      <label class="form-check-label" for="dontAskAgainCheckbox">
        {{ $t("page.session.during.modal.dontAskAgain") }}
      </label>
    </div>
  </b-modal>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import { useI18n } from "vue-i18n";

export default defineComponent({
  name: "UserStoryDeleteModal",
  props: {
    isVisible: { type: Boolean, required: true },
    storyMode: { type: String, required: true, default: null },
  },
  emits: ["update:is-visible", "confirm-delete"],
  setup() {
    const { t } = useI18n();
    return { t };
  },
  data() {
    return {
      dontAskAgain: false,
    };
  },
  computed: {
    isVisibleProxy: {
      get(): boolean {
        return this.isVisible;
      },
      set(val: boolean) {
        this.$emit("update:is-visible", val);
      },
    },
    modalText(): string {
      return this.storyMode === "US_JIRA"
        ? this.t("page.session.during.modal.jiraDeleteInfo")
        : this.t("page.session.during.modal.defaultDeleteInfo");
    },
  },
  watch: {
    isVisibleProxy(newVal: boolean) {
      if (newVal) {
        this.dontAskAgain = false;
      }
    },
  },
  methods: {
    confirm() {
      if (this.dontAskAgain) {
        localStorage.setItem("skipUserStoryDeleteConfirm", "true");
      }
      this.$emit("confirm-delete");
      this.isVisibleProxy = false;
    },
    cancel() {
      this.isVisibleProxy = false;
    },
  },
});
</script>
