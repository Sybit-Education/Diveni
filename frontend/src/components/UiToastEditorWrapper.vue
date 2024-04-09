<template>
  <div>
    <div
      ref="editor"
      v-debounce:1s="() => emit('stoppedTyping', { description: markdownText})"
    />
    <div ref="viewer"/>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import Editor from "@toast-ui/editor";
import "@toast-ui/editor/dist/toastui-editor.css";
import codeSyntaxHighlight from "@toast-ui/editor-plugin-code-syntax-highlight/dist/toastui-editor-plugin-code-syntax-highlight-all";
import '@toast-ui/editor/dist/toastui-editor-viewer.css';
import { useI18n } from "vue-i18n";

const props = defineProps({
  modelValue: { type: String, required: false, default: "" },
  placeholder: { type: String, default: "", required: false },
  initialValue: { type: String, default: "", required: false },
  height: { type: String, default: "500px", required: false },
  noneClickable: { type: Boolean, default: false, required: false },
});
const emit = defineEmits(["update:modelValue", "stillTyping",'stoppedTyping']);
const editor = ref();
const viewer = ref();
const i18n = useI18n();
let markdownText = "";

function getMarkdownText(text: string) {
  markdownText = text;
  emit("stillTyping", {text: text});
}

onMounted(() => {
  if (props.noneClickable) {
    const v = Editor.factory({
      el: viewer.value,
      viewer: true,
      minHeight: '200px',
      initialValue: props.initialValue ?? ""
    });
  } else {
    const e = new Editor({
      autofocus: false,
      height: props.height,
      minHeight: "200px",
      language: i18n.locale.value,
      useCommandShortcut: true,
      usageStatistics: true,
      hideModeSwitch: false,
      placeholder: props.placeholder,
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
      el: editor.value,
      initialEditType: "wysiwyg",
      initialValue: props.initialValue ?? "",
      previewStyle: "vertical",
      events: {
        blur: () => emit("update:modelValue", e.getMarkdown()),
        change: () => getMarkdownText(e.getMarkdown()),
      },
    });
  }

});
</script>
