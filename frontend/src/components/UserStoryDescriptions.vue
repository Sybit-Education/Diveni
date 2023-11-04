<template>
  <div class="user-story-descriptions">
    <div class="list">
      <b-list-group-item
        v-for="(story, idx) of userStories"
        v-show="idx === index"
        :key="story.id"
        class="border-0 description-box"
        variant="outline-secondary"
      >
        <div class="list-group list-group-horizontal">
          <b-form-textarea
            v-model="userStories[idx].title"
            rows="1"
            max-rows="3"
            :disabled="!editDescription"
            size="lg"
            :placeholder="$t('page.session.before.userStories.placeholder.userStoryTitle')"
            @blur="publishChanges(idx)"
          />
          <b-dropdown
            v-show="editDescription"
            variant="none"
            class="px-3 ml-5 estimationDescription"
            :text="(userStories[idx].estimation ? userStories[idx].estimation : '?') + '    '"
          >
            <b-dropdown-item
              v-for="num in filteredCardSet"
              :key="num"
              :disabled="!editDescription"
              :value="num"
              @click="
                userStories[idx].estimation = num;
                publishChanges(idx);
                $event.target.blur();
              "
            >
              {{ num }}
            </b-dropdown-item>
          </b-dropdown>
        </div>
        <div>
          <markdown-editor
            id="textarea-auto-height"
            v-model="userStories[idx].description"
            class="mt-1 my-5"
            :disabled="!editDescription"
            :placeholder="$t('page.session.before.userStories.placeholder.userStoryDescription')"
            @textValueChanged="(event) => valueChanged(idx, event)"
          />
        </div>
        <div v-if="!editDescription">
          <b-form-textarea
            id="textarea-auto-height-plaintext"
            class="py-2 description-text-area"
            :disabled="!editDescription"
            plaintext
            :value="userStories[idx].description"
            rows="15"
            size="m"
          />
        </div>
      </b-list-group-item>
      <div
        v-if="userStories.length <= index && userStories.length"
        class="text-center rounded p-3 m-2"
      >
        <b-card class="border-0" :title="$t('page.session.before.userStories.text')" />
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import MarkdownEditor from "@/components/MarkdownEditor.vue";

export default Vue.extend({
  name: "UserStoryDescriptions",
  components: { MarkdownEditor },
  props: {
    index: { type: Number, required: true },
    cardSet: { type: Array, required: true },
    initialStories: { type: Array, required: true },
    editDescription: { type: Boolean, required: true, default: false },
    showEstimations: { type: Boolean, required: false },
    showEditButtons: { type: Boolean, required: false, default: true },
  },
  data() {
    return {
      sideBarOpen: false,
      userStories: [] as Array<{
        id: string | null;
        title: string;
        description: string;
        estimation: string | null;
        isActive: boolean;
      }>,
    };
  },
  computed: {
    filteredCardSet(): Array<unknown> {
      return this.cardSet.filter((card) => card !== "?");
    },
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
      this.userStories[idx].description = markdown;
      this.publishChanges(idx);
    },
    setUserStoryAsActive(index) {
      const stories = this.userStories.map((s) => ({
        id: s.id,
        title: s.title,
        description: s.description,
        estimation: s.estimation,
        isActive: false,
      }));
      stories[index].isActive = true;
      this.userStories = stories;
      this.publishChanges(index);
    },
    addUserStory() {
      this.userStories.push({
        id: null,
        title: "",
        description: "",
        estimation: null,
        isActive: false,
      });
    },
    publishChanges(idx) {
      this.$emit("userStoriesChanged", { us: this.userStories, idx: idx, doRemove: false });
    },
    // synchronizeJira(idx) {
    //   this.$emit("synchronizeJira", { story: this.userStories[idx], doRemove: false });
    // },
  },
});
</script>

<style scoped>
.estimationDescription {
  border: 2px solid var(--estimateButtonBorder);
  border-radius: 13px;
  background-color: var(--secondary-button) !important;
}

.estimationDescription:hover {
  border: 2px solid var(--estimateButtonBorder);
  border-radius: 13px;
  background-color: var(--secondary-button-hovered) !important;
}

.description-box {
  background: transparent;
  padding: 0;
}
/* The side navigation menu */
.form-control-lg {
  border: none;
}

.form-control-lg:disabled {
  background-color: var(--blurredColour8) !important;
  border-radius: 13px;
}

.description-text-area {
  padding-left: 10px;
  color: var(--text-primary-color);
  background-color: var(--blurredColour8);
  border-radius: 13px;
  box-shadow: none !important;
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
