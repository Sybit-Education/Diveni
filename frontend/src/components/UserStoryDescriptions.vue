<template>
  <div class="user-story-descriptions">
    <b-list-group-item
      v-for="(story, idx) of userStories"
      v-show="idx === index"
      :key="story.id"
      class="border-0 description-box"
      variant="outline-secondary"
    >
      <div>
        <markdown-editor
          id="textarea-auto-height"
          :key="updateComponent"
          v-model="userStories[idx].description"
          class="my-2"
          :disabled="!editDescription"
          :placeholder="t('page.session.before.userStories.placeholder.userStoryDescription')"
          :accepted-stories="acceptedStories"
          :current-story-i-d="userStories[idx].id"
          :has-api-key="hasApiKey"
          @textValueChanged="(event) => valueChanged(idx, event)"
          @sendGPTDescriptionRequest="sendGPTDescriptionRequest"
        />
      </div>
      <div v-if="!editDescription">
        <markdown-editor
          id="textarea-auto-height"
          :key="updateComponent"
          v-model="userStories[idx].description"
          class="my-2 noneClickable"
          :placeholder="t('page.session.before.userStories.placeholder.userStoryDescription')"
          @textValueChanged="(event) => valueChanged(idx, event)"
          @sendGPTDescriptionRequest="sendGPTDescriptionRequest"
        />
      </div>
    </b-list-group-item>
    <div
      v-if="userStories.length <= index && userStories.length"
      class="text-center rounded p-3 m-2"
    >
      <b-card class="border-0" :title="t('page.session.before.userStories.text')" />
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import MarkdownEditor from "@/components/MarkdownEditor.vue";
import { useI18n } from "vue-i18n";
import { useToast } from "vue-toastification";

export default defineComponent({
  name: "UserStoryDescriptions",
  components: { MarkdownEditor },
  props: {
    index: { type: Number, required: true },
    initialStories: { type: Array, required: true },
    editDescription: { type: Boolean, required: true, default: false },
    gptDescriptionResponse: { type: Boolean, required: false, default: false },
    updateComponent: { type: Boolean, required: false, default: false },
    storyMode: { type: String, required: true },
    acceptedStories: {
      type: Array<{ storyID: string | null; issueType: string }>,
      required: false,
      default: [],
    },
    hasApiKey: { type: Boolean, required: false, default: false },
  },
  setup() {
    const { t } = useI18n();
    const toast = useToast();
    return { t, toast };
  },
  data() {
    return {
      userStories: [] as Array<{
        id: string | null;
        title: string;
        description: string;
        estimation: string | null;
        isActive: boolean;
      }>,
    };
  },
  watch: {
    initialStories() {
      this.userStories = this.initialStories as Array<{
        id: string | null;
        title: string;
        description: string;
        estimation: string | null;
        isActive: boolean;
      }>;
    },
  },
  created() {
    this.userStories = this.initialStories as Array<{
      id: string | null;
      title: string;
      description: string;
      estimation: string | null;
      isActive: boolean;
    }>;
  },
  methods: {
    valueChanged(idx, { markdown }) {
      if (this.storyMode !== "US_JIRA" || this.userStories[idx].title?.trim()) {
        this.userStories[idx].description = markdown;
        this.publishChanges(idx);
      } else {
        this.toast.error(this.t("session.notification.messages.issueTrackerJiraMissingTitle"));
      }
    },
    publishChanges(idx) {
      this.$emit("userStoriesChanged", { us: this.userStories, idx: idx, doRemove: false });
    },
    sendGPTDescriptionRequest({ description, issue, confidentialData, language }) {
      this.$emit("sendGPTDescriptionRequest", {
        userStory: this.userStories[this.index],
        description: description,
        issue: issue,
        confidentialData: confidentialData,
        language: language,
      });
    },
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS/SCSS to this component only -->
<style lang="scss" scoped>
.description-box {
  background: transparent;
  padding: 0;
}
/* The side navigation menu */
.form-control-lg {
  border-color: var(--estimateButtonBorder) !important;
}

.form-control-lg:disabled {
  background-color: var(--blurredColour8) !important;
  border-radius: 13px;
  border: none;
}

.description-text-area {
  padding-left: 7px;
  color: var(--text-primary-color);
  background-color: var(--blurredColour8);
  border-radius: 13px;
  box-shadow: none !important;
}

.noneClickable {
  pointer-events: none;
}

.sidenav {
  float: right;
  height: 100%; /* 100% Full-height */
  width: 0; /* 0 width - change this with JavaScript */
  position: fixed; /* Stay in place */
  z-index: 2; /* Stay on top */
  top: 0; /* Stay at the top */
  right: 0;
  background-color: white;
  overflow-x: hidden; /* Disable horizontal scroll */
  transition: 0.2s; /* 0.5 second transition effect to slide in the sidenav */
}

.sidenavButton {
  margin: 8px;
  float: right;
  position: fixed;
  z-index: 3;
  top: 0;
  right: 0;
  overflow-x: hidden;
}

/* On smaller screens, where height is less than 450px, change the style of the sidenav (less padding and a smaller font size) */
@media screen and (max-height: 450px) {
  .sidenav {
    padding-top: 15px;
  }

  .sidenav a {
    font-size: 18px;
  }
}
</style>
