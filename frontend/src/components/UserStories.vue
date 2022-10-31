<template>
  <div class="user-stories">
    <b-card-group class="my-3 overflow-auto" style="max-height: 70vh">
      <b-list-group-item
        v-for="(story, index) of userStories"
        :key="index"
        :active="index === selectedStoryIndex"
        class="w-100 p-1 d-flex justify-content-left"
        @mouseover="hover = index"
        @mouseleave="hover = null"
        @click="setUserStoryAsActive(index)"
      >
        <b-button
          v-if="showEditButtons"
          :variant="story.isActive ? 'success' : 'outline-success'"
          size="sm"
          @click="markUserStory(index)"
        >
          <b-icon-check2 />
        </b-button>

        <b-form-input
          v-model="story.title"
          :disabled="true"
          class="mx-1 w-100"
          size="sm"
          :placeholder="
            $t('page.session.before.userStories.placeholder.userStoryTitle')
          "
          @blur="publishChanges"
        />

        <b-badge variant="info" class="p-2">
          {{ story.estimation == null ? "?" : story.estimation }}
        </b-badge>
        <b-button
          v-show="showEditButtons && hover === index"
          variant="outline-danger"
          class="border-0"
          size="sm"
          @click="deleteStory(index)"
        >
          <b-icon-trash />
        </b-button>
      </b-list-group-item>
    </b-card-group>

    <b-button
      v-if="userStories.length < 1 && showEditButtons"
      class="w-100 mb-3"
      variant="success"
      @click="addUserStory()"
    >
      <b-icon-plus />
      {{ $t("page.session.before.userStories.button.addFirstUserStory") }}
    </b-button>

    <b-button
      v-if="userStories.length > 0 && showEditButtons"
      class="w-100 mb-3"
      variant="success"
      @click="addUserStory()"
    >
      <b-icon-plus />
      {{ $t("page.session.before.userStories.button.addUserStory") }}
    </b-button>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import UserStory from "../model/UserStory";

export default defineComponent({
  name: "UserStories",
  props: {
    cardSet: { type: Array, required: true },
    initialStories: { type: Array, required: true },
    showEstimations: { type: Boolean, required: true },
    showEditButtons: { type: Boolean, required: false, default: true },
    selectStory: { type: Boolean, required: false, default: false },
  },
  emits: [ 'selectedStory', 'userStoriesChanged' ],
  data() {
    return {
      selectedStoryIndex: null,
      sideBarOpen: false,
      userStories: [] as Array<UserStory>,
      hover: null,
    };
  },
  watch: {
    initialStories() {
      this.userStories = this.initialStories as Array<UserStory>;
    },
  },
  mounted() {
    this.userStories = this.initialStories as Array<UserStory>;
  },
  methods: {
    setUserStoryAsActive(index) {
      this.selectedStoryIndex = index;
      this.$emit("selectedStory", index);
    },
    addUserStory() {
      const story: UserStory = {
        jiraId: null,
        title: "",
        description: "",
        estimation: null,
        isActive: false,
      };
      this.userStories.push(story);
    },
    deleteStory(index) {
      this.publishChanges(index, true);
    },
    publishChanges(index, remove) {
      this.$emit("userStoriesChanged", {
        us: this.userStories,
        idx: index,
        doRemove: remove,
      });
    },
    markUserStory(index) {
      const stories = this.userStories.map((s) => ({
        jiraId: s.jiraId,
        title: s.title,
        description: s.description,
        estimation: s.estimation,
        isActive: false,
      }));
      stories[index].isActive = true;
      this.userStories = stories;
      this.publishChanges(index, false);
    },
  },
});
</script>

<style scoped>
.list-group-item.active {
  background-color: transparent;
  border-width: 3px;
}
</style>
