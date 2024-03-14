<template>
  <div class="markdown-editor">
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
  background-color: var(--editor-defaultui-container-bg);
  font-size: 18px;
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
  padding: 0 25px;
  height: 100%;
  background-color: var(--editor-defaultui-container-bg);
}

.toastui-editor-mode-switch {
  background-color: var(--editor-toolbar-switch-bg);
}

.toastui-editor-contents p {
  color: var(--text-primary-color) !important;
}

.lightMode .toastui-editor-defaultUI .ProseMirror {
  background-color: var(--textAreaColour);
}

.lightMode .toastui-editor-md-container .toastui-editor-md-preview {
  overflow: auto;
  padding: 0 25px;
  height: 100%;
  background-color: var(--textAreaColour);
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
}
</style>
