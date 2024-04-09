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
          v-model="userStories[idx].description"
          class="my-2"
          :disabled="!editDescription"
          :placeholder="t('page.session.before.userStories.placeholder.userStoryDescription')"
          :accepted-stories="acceptedStories"
          :current-story-i-d="userStories[idx].id"
          :key="updateComponent"
          @textValueChanged="(event) => valueChanged(idx, event)"
          @sendGPTDescriptionRequest="sendGPTDescriptionRequest"
        />
      </div>
      <div v-if="!editDescription">
        <markdown-editor
          id="textarea-auto-height"
          v-model="userStories[idx].description"
          class="my-2 noneClickable"
          :placeholder="t('page.session.before.userStories.placeholder.userStoryDescription')"
          :key="updateComponent"
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

export default defineComponent({
  name: "UserStoryDescriptions",
  components: {MarkdownEditor },
  props: {
    index: { type: Number, required: true },
    initialStories: { type: Array, required: true },
    editDescription: { type: Boolean, required: true, default: false },
    gptDescriptionResponse: { type: Boolean, required: false, default: false },
    updateComponent: { type: Boolean, required: false, default: false },
    acceptedStories: { type: Array<{ storyID: number | null, issueType: string }>, required: false, default: []}
  },
  setup() {
    const { t } = useI18n();
    return { t };
  },
  data() {
    return {
      userStories: [] as Array<{
        id: number | null;
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
        id: number | null;
        title: string;
        description: string;
        estimation: string | null;
        isActive: boolean;
      }>;
    },
  },
  created() {
    this.userStories = this.initialStories as Array<{
      id: number | null;
      title: string;
      description: string;
      estimation: string | null;
      isActive: boolean;
    }>;
  },
  methods: {
    valueChanged(idx, { markdown }) {
      this.userStories[idx].description = markdown;
      this.publishChanges(idx);
    },
    publishChanges(idx) {
      this.$emit("userStoriesChanged", { us: this.userStories, idx: idx, doRemove: false });
    },
    sendGPTDescriptionRequest({ description, issue, confidentalData }) {
      this.$emit("sendGPTDescriptionRequest", {
        userStory: this.userStories[this.index],
        description: description,
        issue: issue,
        confidentalData: confidentalData,
      });
    },
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS/SCSS to this component only -->
<style lang="scss" scoped>
.animated-badge {
  animation: Shake 3s linear infinite; /* Increased duration to account for the pause */
  cursor: pointer;
}

@keyframes Shake {
  0% {
    transform: rotate(5deg);
  }

  25% {
    transform: rotate(-6deg);
  }

  50% {
    transform: rotate(5deg);
  }

  75% {
    transform: rotate(-6deg);
  }

  100% {
    transform: rotate(5deg);
  }
}

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
