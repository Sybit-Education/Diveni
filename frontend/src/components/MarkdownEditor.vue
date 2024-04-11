<template>
  <div class="markdown-editor position-relative">
    <editor
      v-if="!disabled"
      ref="toastuiEditor"
      :initial-value="markdown"
      :height="height"
      :options="editorOptions"
      :placeholder="placeholder"
      initial-edit-type="wysiwyg"
      preview-style="vertical"
      :class="theme === 'light' ? 'lightMode' : 'darkMode'"
      @update:modelValue="$emit('textValueChanged', { markdown: $event })"
      @stillTyping="aiButtonVisible = false"
      @stoppedTyping="showAiButton"
    />
    <b-button
      id="submitAIDescription"
      v-show="aiButtonVisible && currentText !== ''"
      @click="showPopOver = !showPopOver;"
    >
      <b-icon-stars id="aiStars" />
    </b-button>

    <div
      v-if="showPopOver"
      id='aiPopOver'
    >
      <div id="popoverBody">
        <b-button
          v-if="foundGrammar"
          class="my-1 aiDescriptionButtons"
          @click="aiButtonClicked('grammar')"
        >
          <b-icon-pencil/> Grammar check
        </b-button>
        <b-button
          v-if="foundDescription"
          class="my-1 aiDescriptionButtons"
          @click="aiButtonClicked('improveDescription')"
        >
          <b-icon-lightbulb/> Improve Description
        </b-button>
      </div>
    </div>
    <div
      v-if="showPopOver"
      class="triangle-down"/>
    <div
      id="privacyModal"
    >
      <b-modal
        v-model="showModal"
        centered
        hide-header-close
      >
        <template>
          <div class="text-center">
            Mark confidential information
          </div>
        </template>
        <div>
          <UiToastEditorWrapper
            :initial-value="currentText"
            :none-clickable="true"
          />
          <div class="d-flex justify-content-end">
            <b-form-input
              v-model="inputData"
              placeholder="Type in confidental words"
            />
            <b-button
              @click="addWordToList()"
            >Add</b-button>
          </div>
          <div
            class="text-center"
          >
            <div
              v-for="(data, index) in confidentalWords"
              :key="data"
              class="d-inline-block"
            >
              <div
                id="confidental-words"
                class="mx-2"
                @mouseover="hover = index"
                @mouseleave="hover = null"
              >
                  {{data}}

                <b-button
                  variant="outline-danger"
                  class="border-0"
                  size="sm"
                  @click="confidentalWords.splice(index, 1);"
                >
                <b-icon-trash />
              </b-button>
              </div>
            </div>
          </div>
        </div>
        <template #modal-footer >
          <div id="aiOptions" class="text-center mt-1">
            <b-button id="acceptAIOption" class="m-1" @click="submitIssue">
              <b-icon-check2 />
              Ok
            </b-button>
            <b-button class="aiOptionButtons m-1" @click="hideModal(); $event.target.blur();">
              <b-icon-x-square/>
              Cancel
            </b-button>
          </div>
        </template>
      </b-modal>
    </div>
  </div>
</template>

<script lang="ts">
import "@toast-ui/editor/dist/toastui-editor.css";
import "prismjs/themes/prism.css";
import "@toast-ui/editor-plugin-code-syntax-highlight/dist/toastui-editor-plugin-code-syntax-highlight.css";

import "@toast-ui/editor/dist/i18n/de-de";

import {customRef, defineComponent} from "vue";
import codeSyntaxHighlight from "@toast-ui/editor-plugin-code-syntax-highlight/dist/toastui-editor-plugin-code-syntax-highlight-all";
import UiToastEditorWrapper from "@/components/UiToastEditorWrapper.vue";
import { PropType } from "vue";

export default defineComponent({
  name: "MarkdownEditor",
  components: {
    UiToastEditorWrapper,
    editor: UiToastEditorWrapper,
  },
  model: {
    prop: "markdown",
    event: "change",
  },
  props: {
    disabled: {
      type: Boolean,
      default: false,
    },
    height: {
      type: String,
      default: "500px",
    },
    markdown: {
      type: String,
      required: false,
      default: "",
    },
    placeholder: {
      type: String,
      default: "",
    },
    currentStoryID: {
      type: [String, null] as PropType<string | null>,
      required: false,
      default: null
    },
    acceptedStories: {
      type: Array<{ storyID: number | null, issueType: string }>,
      required: false,
      default: []
    },
  },
  data() {
    return {
      editorOptions: {
        autofocus: false,
        height: "auto",
        minHeight: "200px",
        language: this.$i18n.locale,
        useCommandShortcut: true,
        usageStatistics: true,
        hideModeSwitch: false,
        placeholder: this.placeholder,
        extendedAutolinks: true,
        toolbarItems: [
          ["heading", "bold", "italic", "strike"],
          ["hr", "quote"],
          ["ul", "ol", "task", "indent", "outdent"],
          ["table", "image", "link"],
          ["code", "codeblock"],
          ["scrollSync"],
        ],
        plugins: [codeSyntaxHighlight],
      },
      theme: localStorage.getItem("user-theme"),
      showPopOver: false,
      aiButtonVisible: false,
      currentText: "",
      foundDescription: false,
      foundGrammar: false,
      showModal: false,
      currentIssue: "",
      inputData: "",
      confidentalWords: [] as Array<string>,
      hover: null,
    };
  },
  mounted() {
    window.addEventListener("user-theme-localstorage-changed", (event) => {
      const customEvent = event as CustomEvent;
      this.theme = customEvent.detail.storage;
    });
  },
  methods: {
    customRef,
    showAiButton({description}) {
      if (description.trim().length > 0) { // hier vielleicht eher überprüfen ob String Text oder Zahlen beinhaltet
        this.currentText = description;
        this.foundDescription = !this.acceptedStories.find(us => us.storyID === this.currentStoryID && us.issueType === 'improveDescription');
        this.foundGrammar = !this.acceptedStories.find(us => us.storyID === this.currentStoryID && us.issueType === 'grammar');
        this.aiButtonVisible = this.foundDescription || this.foundGrammar;
      }
    },
    aiButtonClicked(issue) {
      this.showPopOver = false;
      this.aiButtonVisible = false;
      this.showModal = true;
      this.currentIssue = issue;
    },
    addWordToList() {
      const selection = window.getSelection()
      if (!this.confidentalWords.includes(this.inputData) && this.inputData !== '') {
        this.confidentalWords.push(this.inputData);
      }
      if (selection !== null && selection.toString() !== '' && !this.confidentalWords.includes(selection.toString())) {
        this.confidentalWords.push(selection.toString());
      }
      this.inputData = "";
    },
    submitIssue() {
      this.$emit("sendGPTDescriptionRequest", {description: this.currentText, issue: this.currentIssue, confidentalData: this.confidentalWords});
      this.hideModal();
    },
    hideModal() {
      this.showModal = false;
    }
  },
});
</script>

<style lang="scss">
.toastui-editor-popup {
  background-color: var(--topNavigationBarColor);
}

.toastui-editor-defaultUI-toolbar {
  background-color: var(--editor-toolbar-switch-bg);
}
.toastui-editor-defaultUI-toolbar button {
  border: 12px;
  color: var(--text-primary-color);
}

.toastui-editor-defaultUI .ProseMirror {
  background-color: var(--text-field);
  font-size: 18px;
  padding: 18px 60px 18px 25px;
}

.toastui-editor-contents ul > li::before {
  height: 9px;
  width: 9px;
  margin-left: -16px;
}

.toastui-editor-contents ul > li::before, .toastui-editor-contents ol > li::before {
  top: 4px;
}

.toastui-editor-contents h5 {
  color: var(--text-primary-color);
}

.toastui-editor-md-container .toastui-editor-md-preview {
  overflow: auto;
  overflow-wrap: break-word;
  padding: 0 60px 0 25px;
  height: 100%;
  background-color: var(--text-field); /* var(--editor-defaultui-container-bg) */
}

.toastui-editor-mode-switch {
  background-color: var(--editor-toolbar-switch-bg);
}

.toastui-editor-contents p {
  color: var(--text-primary-color);
  font-size: large;
}


.lightMode .toastui-editor-md-container .toastui-editor-md-preview {
  overflow: auto;
  padding: 0 25px;
  height: 100%;
}

.lightMode .toastui-editor-mode-switch {
  background-color: var(--editor-toolbar-switch-bg);
}

.lightMode .toastui-editor-popup {
  background-color: grey;
}

.ProseMirror {
  height: 100%;
  color: var(--text-primary-color) !important;
  z-index: 0;
}
/*AI FEATURE*/

.aiDescriptionButtons {
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

#aiPopOver{
  background-color: white;
  position: absolute;
  border: 3px solid black;
  right: 2.5em;
  bottom: 6.5em;
}

.triangle-down {
  width: 0;
  height: 0;
  border-left: 10px solid transparent;
  border-right: 10px solid transparent;
  border-top: 10px solid black;
  position: absolute;
  right: 2.45em;
  bottom: 5.9em;
}

#popoverBody {
  display: inline-grid;
}

#aiStars {
  height: 2em;
  width: 2em;
}

#submitAIDescription {
  position: absolute;
  right: 1vw;
  top: 81%;
  color: var(--ai-stars) !important;
  background-color: transparent !important;
  border-style: none;
  animation: showUp 1s;
  z-index: 2000;
}

@keyframes showUp {
  0% {
    opacity: 0;
  }
  100% {
    opacity: 1;
  }
}

</style>
