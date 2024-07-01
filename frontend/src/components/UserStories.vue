<template>
  <div class="user-stories">
    <div v-if="userStories.length > 0 || filterActive" class="w-100 d-flex justify-content-left">
      <b-input-group>
        <b-input-group-prepend>
          <b-input-group-text><BIconSearch id="searchIcon" /></b-input-group-text>
        </b-input-group-prepend>
        <b-input
          id="search"
          v-model="input"
          type="text"
          :placeholder="t('page.session.before.userStories.placeholder.searchUserStories')"
          @input="filterStories"
        />
      </b-input-group>
    </div>
    <b-card-group id="userStoryBlock" class="mt-2">
      <b-list-group-item
        v-for="(story, index) in displayedStories"
        id="userStoryRow"
        :key="index"
        :active="getOriginalIndex(index) === selectedStoryIndex"
        class="w-100 p-1 d-flex justify-content-left"
        :style="getOriginalIndex(index) === selectedStoryIndex ? 'border-width: 3px;' : ''"
        @mouseover="hover = index"
        @mouseleave="hover = null"
        @click="setUserStoryAsActive(index)"
      >
        <b-button
          v-if="showEditButtons"
          variant="primary"
          :class="story.isActive ? 'selectedStory' : 'outlineColorStory'"
          size="sm"
        >
          <b-img id="userStoryPicture" :src="require('@/assets/ActiveUserStory.png')" />
        </b-button>

        <b-button
          v-else-if="hostSelectedStoryIndex === getOriginalIndex(index) && !showEditButtons"
          size="sm"
          variant="success"
          disabled
        >
          <b-icon-arrow-right />
        </b-button>

        <b-form-input
          id="userStoryTitles"
          v-model="story.title"
          class="mx-1 w-100 shadow-none"
          readonly
          size="sm"
          :placeholder="t('page.session.before.userStories.placeholder.userStoryTitle')"
          @blur="publishChanges(index, false)"
        />

        <b-badge id="badge" class="p-2">
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
      v-if="userStories.length < 1 && showEditButtons && !filterActive"
      class="w-100 mb-1"
      variant="primary"
      @click="
        addUserStory();
        $event.target.blur();
      "
    >
      <b-icon-plus />
      {{ t("page.session.before.userStories.button.addFirstUserStory") }}
    </b-button>

    <b-alert
      v-if="userStories.length < 1 && showEditButtons && filterActive"
      show
      variant="warning"
    >
      {{ t("page.session.before.userStories.filter.noStoryFound") }}
    </b-alert>

    <b-button
      v-if="userStories.length > 0 && showEditButtons && !filterActive"
      class="w-100 mb-1"
      variant="primary"
      @click="
        addUserStory();
        $event.target.blur();
      "
    >
      <b-icon-plus />
      {{ t("page.session.before.userStories.button.addUserStory") }}
    </b-button>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import UserStory from "../model/UserStory";
import { useI18n } from "vue-i18n";

export default defineComponent({
  name: "UserStories",
  props: {
    cardSet: { type: Array, required: true },
    initialStories: { type: Array, required: true },
    showEstimations: { type: Boolean, required: true },
    showEditButtons: { type: Boolean, required: false, default: true },
    hostSelectedStoryIndex: { type: Number, required: false, default: null },
  },
  setup() {
    const { t } = useI18n();
    return { t };
  },
  data() {
    return {
      selectedStoryIndex: null as number | null,
      userStories: [] as Array<UserStory>,
      hover: null as number | null,
      input: "",
      filterActive: false,
      savedStories: [] as Array<UserStory>,
      filteredStories: [] as Array<{ story: UserStory; originalIndex: number }>,
    };
  },
  computed: {
    displayedStories() {
      return this.filterActive ? this.filteredStories.map((item) => item.story) : this.userStories;
    },
  },
  watch: {
    initialStories: {
      immediate: true,
      handler(newStories) {
        this.userStories = newStories;
        this.savedStories = newStories;
        this.filterStories();
      },
    },
    hostSelectedStoryIndex(newIndex) {
      this.selectedStoryIndex = newIndex;
      this.$emit("selectedStory", newIndex);
    },
  },
  mounted() {
    this.userStories = this.initialStories as Array<UserStory>;
    this.displayedStories = this.initialStories as Array<UserStory>;
  },
  methods: {
    getOriginalIndex(index: number) {
      if (this.filterActive && index >= 0 && index < this.filteredStories.length) {
        return this.filteredStories[index].originalIndex;
      } else {
        return index;
      }
    },
    setUserStoryAsActive(index: number) {
      const originalIndex = this.getOriginalIndex(index);
      this.selectedStoryIndex = originalIndex;
      this.$emit("selectedStory", originalIndex);
    },
    addUserStory() {
      const newStory: UserStory = {
        id: null,
        title: "",
        description: "",
        estimation: null,
        isActive: false,
      };
      this.userStories.push(newStory);
      this.setUserStoryAsActive(this.userStories.length - 1);
    },
    filterStories() {
      if (!this.filterActive) {
        this.savedStories = this.userStories;
      }
      this.userStories = this.savedStories;
      if (this.input) {
        const filteredUserStories = this.userStories
          .map((story, index) => ({ story, originalIndex: index }))
          .filter(({ story }) => story.title.toLowerCase().includes(this.input.toLowerCase()));
        this.filterActive = true;
        this.filteredStories = filteredUserStories;
        this.userStories = filteredUserStories.map((item) => item.story);
      } else {
        this.filterActive = false;
        this.userStories = this.savedStories;
      }
    },
    deleteStory(index: number) {
      const originalIndex = this.getOriginalIndex(index);
      this.publishChanges(originalIndex, true);
    },
    publishChanges(index: number, remove: boolean) {
      this.$emit("userStoriesChanged", { us: this.savedStories, idx: index, doRemove: remove });
    },
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS/SCSS to this component only -->
<style lang="scss" scoped>
#searchIcon {
  rotate: 90deg;
}

#userStoryRow {
  background-color: var(--textAreaColour);
  color: var(--text-primary-color);
}

#userStoryBlock {
  max-height: 205px; /*exactly 4 User Stories tall*/
  border-radius: 1rem;
  overflow: scroll;
  -webkit-overflow-scrolling: touch;
}

#userStoryTitles {
  background-color: transparent;
  color: var(--text-primary-color);
  font-size: large;
  border: none;
}

#userStoryPicture {
  height: 25px;
  width: 25px;
}

#badge {
  background-color: var(--secondary-button);
  color: var(--text-primary-color);
  font-size: large;
}
</style>
