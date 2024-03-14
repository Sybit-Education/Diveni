<template>
  <div>
    <b-modal
      v-if="mode === 'improveTitle'"
      v-model="showModal"
      centered
      title="GPT Suggestion"
      @ok="useAsTitle()"
      @hide="closeModal()"
    >
      A new alternated Title could be:
      <div class="mt-1">
        {{suggestionTitle}}
      </div>
    </b-modal>
    <b-modal
      v-if="mode === 'improveDescription'"
      v-model="showModal"
      centered
      title="GPT Suggestion"
      @ok="useAsDescription()"
      @hide="closeModal()"
    >
      A new alternated Description could be:
      <div class="mt-1">
        {{suggestionDescription}}
      </div>
    </b-modal>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref } from "vue";
import { useI18n } from "vue-i18n";

export default defineComponent({
  name: "GptModal",
  setup() {
    const showModal = true;
    const { t } = useI18n();
    return { t, showModal};
  },
  props: {
    suggestionTitle: { type: String, required: false},
    suggestionDescription: { type: String, required: false},
    mode: { type: String, required: true},
  },
  methods: {
    useAsTitle() {
      this.$emit("acceptSuggestionTitle");
    },
    useAsDescription() {
      this.$emit("acceptSuggestionDescription");
    },
    closeModal() {
      this.showModal = true;
      this.$emit("hideModal");
    }
  },
});
</script>

<style scoped lang="scss">

</style>
