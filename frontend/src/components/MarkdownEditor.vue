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
      @updateDescription="emitMarkdownText"
    />
    <b-button
      id="submitAIDescription"
    >
      <b-icon-stars id="aiStars" />
    </b-button>
    <b-popover
      id='aiPopOver'
      :show.sync="show"
      target="submitAIDescription"
      triggers="focus"
      placement="right"
    >
      <template #default>
        <div id="popoverBody">
          <b-button
            class="my-1 aiDescriptionButtons"
            @click="console.log('Rechtschreibung'); show = !show;"
          >
            <b-icon-pencil/> Grammatic check
          </b-button>
          <b-button
            class="my-1 aiDescriptionButtons"
            @click="console.log('Improve Description'); show = !show"
          >
            <b-icon-lightbulb/> Improve Description
          </b-button>
        </div>
      </template>
    </b-popover>
  </div>
</template>

<script lang="ts">
import "@toast-ui/editor/dist/toastui-editor.css";
import "prismjs/themes/prism.css";
import "@toast-ui/editor-plugin-code-syntax-highlight/dist/toastui-editor-plugin-code-syntax-highlight.css";

import "@toast-ui/editor/dist/i18n/de-de";

import { defineComponent } from "vue";
import codeSyntaxHighlight from "@toast-ui/editor-plugin-code-syntax-highlight/dist/toastui-editor-plugin-code-syntax-highlight-all";
import UiToastEditorWrapper from "@/components/UiToastEditorWrapper.vue";

export default defineComponent({
  name: "MarkdownEditor",
  components: {
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
      show: false,
    };
  },
  mounted() {
    window.addEventListener("user-theme-localstorage-changed", (event) => {
      const customEvent = event as CustomEvent;
      this.theme = customEvent.detail.storage;
    });
  },
  methods: {
    emitMarkdownText({description}) {
      console.log("Markdown: " + description);
      this.$emit("improveDescription", {description: description});
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
  color: var(--text-primary-color) !important;
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
  border-style: solid !important;
  border-color: transparent !important;
  border-width: thin !important;
  background-color: transparent !important;
  color: black !important;

  &:hover {
    border-style: dashed !important;
    border-color: black !important;
  }
  &:active {
    border-style: dashed !important;
    border-color: black !important;
    background-color: transparent !important;
    color: black !important;
  }
}

#aiPopOver{
  background-color: white;
}

#popoverBody {
  display: inline-grid;
}

.test {
  display: inline-grid !important;
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
  z-index: 2;
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
