<template>
  <div>
    <b-modal
      v-model="showModal"
      id="gptModal"
      @hide="closeModal()"
      centered
      hide-header-close
    >
      <template #modal-header>
        <div class="text-center">
          {{gptMode === 'improveDescription' ? 'Alternative Description' : 'Corrected Description'}}
        </div>
      </template>
      <div
        id="b-modal-body"
      >
        <UiToastEditorWrapper
          :key="repaint"
          :initial-value="suggestionDescription"
          :none-clickable="!clickable"
          @stillTyping="copyText"
        />
      </div>
      <template #modal-footer >
        <div id="aiOptions" class="text-center mt-1">
          <b-button id="acceptAIOption" class="m-1" @click="acceptDescription">
            <b-icon-check2 />
            Keep
          </b-button>
          <b-button v-if="showOtherOptionButtons" class="aiOptionButtons m-1" @click="adjustDescription(); $event.target.blur();">
            <b-icon-sliders />
            Adjust
          </b-button>
          <b-button v-if="showOtherOptionButtons" class="aiOptionButtons m-1" @click="retryDescription(); $event.target.blur()">
            <b-icon-arrow-repeat />
            Try Again
          </b-button>
          <b-button v-if="showOtherOptionButtons" class="aiOptionButtons m-1" @click="deleteDescription">
            <b-icon-backspace />
            Delete
          </b-button>
          <b-button v-if="!showOtherOptionButtons" class="aiOptionButtons m-1" @click="cancelAdjust(); $event.target.blur();">
            <b-icon-x-square/>
            Cancel
          </b-button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import { useI18n } from "vue-i18n";
import UiToastEditorWrapper from "@/components/UiToastEditorWrapper.vue";

export default defineComponent({
  name: "GptModal",
  components: {UiToastEditorWrapper},
  setup() {
    const showModal = true;
    const { t } = useI18n();
    return { t, showModal};
  },
  data() {
    return {
      clickable: false,
      showOtherOptionButtons: true,
      editText: "",
      repaint: false,
    }
  },

  props: {
    suggestionDescription: { type: String, required: true},
    gptMode: { type: String, required: true },
    retryRepaint: { type: Boolean, required: true },
  },
  watch: {
    retryRepaint() {
      this.repaint = !this.repaint;
    }
  },
  methods: {
    acceptDescription() {
     console.log("ACCEPT");
     this.$emit("acceptSuggestionDescription", {description: this.editText, originalText: this.showOtherOptionButtons});
     this.closeModal();
    },
    adjustDescription() {
      console.log("ADJUST");
      this.clickable = true;
      this.repaint = !this.repaint;
      this.showOtherOptionButtons = false;
      this.editText = this.suggestionDescription;
    },
    retryDescription() {
      console.log("RETRY");
      this.$emit("retry");
    },
    deleteDescription() {
      this.closeModal();
    },
    cancelAdjust() {
      this.clickable = false;
      this.editText = this.suggestionDescription;
      this.repaint = !this.repaint;
      this.showOtherOptionButtons = true;
    },
    copyText({text}) {
      this.editText = text;
    },
    closeModal() {
      this.showModal = true;
      this.showOtherOptionButtons = true;
      this.$emit("hideModal");
    }
  },
});
</script>

<style lang="scss">

#b-modal-body {
  max-height: 500px;
  overflow: auto;
}

.modal-content {
  background-color: white !important;
  color: black !important;
}

.modal-header {
  text-align: center !important;
  display: block !important;
  font-size: x-large !important;
}

.modal-body {
  color: black;
}

.modal-footer {
  text-align: center !important;
  display: block !important;
}

#acceptAIOption {
  background-color: var(--ai-stars) !important;
  color: white !important;
  border-style: none;
}

.aiOptionButtons {
  border: none !important;
  border-radius: 0 !important;
  background-color: transparent !important;
  color: black !important;

  transition: color 0.3s linear !important;

  &:hover {
    background-color: transparent !important;
    color: var(--ai-stars) !important;
    border-radius: 1em !important;
  }
}

#aiOptions {
  background-color: white;
  border-radius: 0.5em;
  display: inline-flex;
  animation: showUp 1s;
}

</style>
