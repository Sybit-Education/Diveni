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
      :class="theme === 'light-theme' ? 'lightMode' : 'DarkMode'"
      @blur="getTextValueFromEditor"
    />
  </div>
</template>

<script lang="ts">
import "@toast-ui/editor/dist/toastui-editor.css";
import "prismjs/themes/prism.css";
import "@toast-ui/editor-plugin-code-syntax-highlight/dist/toastui-editor-plugin-code-syntax-highlight.css";

import "@toast-ui/editor/dist/i18n/de-de";

import Vue from "vue";
import { Editor } from "@toast-ui/vue-editor";
import codeSyntaxHighlight from "@toast-ui/editor-plugin-code-syntax-highlight/dist/toastui-editor-plugin-code-syntax-highlight-all";

export default Vue.extend({
  name: "MarkdownEditor",
  components: {
    editor: Editor,
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
      default: null,
    },
    placeholder: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      text: "",
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
    getTextValueFromEditor() {
      if (this.$refs.toastuiEditor) {
        const editor = this.$refs.toastuiEditor as Editor;
        const editorText = editor.invoke("getMarkdown");
        this.$emit("textValueChanged", { markdown: editorText });
      }
    },
  },
});
</script>

<style>
.toastui-editor-popup {
  background-color: var(--topNavigationBarColor);
}

.toastui-editor-defaultUI-toolbar {
  background-color: #7a777773;
}
.toastui-editor-defaultUI-toolbar button {
  border: none;
  color: var(--text-primary-color);
}

.toastui-editor-defaultUI .ProseMirror {
  background-color: #405c6c;
}

.toastui-editor-md-container .toastui-editor-md-preview {
  overflow: auto;
  padding: 0 25px;
  height: 100%;
  background-color: #405c6c;
}

.toastui-editor-mode-switch {
  background-color: #405c6c;
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
  background-color: var(--textAreaColour);
}

.lightMode .toastui-editor-popup {
  background-color: grey;
}

.ProseMirror {
  height: 100%;
  color: var(--text-primary-color) !important;
}
</style>
