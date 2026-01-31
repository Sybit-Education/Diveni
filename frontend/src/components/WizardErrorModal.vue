<template>
  <b-modal
    v-model="localVisible"
    centered
    :title="title"
    ok-only
    :ok-title="Ok"
    hide-header-close
    no-close-on-esc
    no-close-on-backdrop
    @ok="handleOk"
  >
    <p class="text-center">{{ message }}</p>
  </b-modal>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import { useI18n } from "vue-i18n";
import { useRouter } from "vue-router";

export default defineComponent({
  name: "WizardErrorModal",
  props: {
    visible: { type: Boolean, default: false },
    title: { type: String, default: "" },
    message: { type: String, default: "" },
  },
  emits: ["reset-wizard"],
  setup() {
    const { t } = useI18n();
    const router = useRouter();
    return { t, router };
  },
  data() {
    return {
      localVisible: this.visible as boolean,
    };
  },
  watch: {
    visible(newVal: boolean) {
      this.localVisible = newVal;
    },
  },
  methods: {
    handleOk() {
      this.$emit("reset-wizard");
    },
  },
});
</script>
