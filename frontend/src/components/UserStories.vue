<template>
  <div class="user-stories">
    <div v-if="userStories.length > 0 || filterActive" class="w-100 d-flex justify-content-left">
      <b-input-group>
        <b-input-group-prepend>
          <b-input-group-text><BIconSearch id="searchIcon"></BIconSearch></b-input-group-text>
        </b-input-group-prepend>
        <b-input
          id="search"
          v-model="input"
          type="text"
          :placeholder="$t('page.session.before.userStories.placeholder.searchUserStories')"
          @input="swapPriority"
        />
      </b-input-group>
    </div>
    <b-card-group id="userStoryBlock" class="mt-2">
      <b-list-group-item
        v-for="(story, index) of userStories"
        id="userStoryRow"
        :key="index"
        :active="index === selectedStoryIndex"
        class="w-100 p-1 d-flex justify-content-left"
        :style="index === selectedStoryIndex ? 'border-width: 3px;' : ''"
        @mouseover="hover = index"
        @mouseleave="hover = null"
        @click="setUserStoryAsActive(index)"
      >
        <b-button
          v-if="showEditButtons"
          :class="story.isActive ? 'selectedStory' : 'outlineColorStory'"
          size="sm"
          @click="
            markUserStory(index);
            $event.target.blur();
          "
        >
          <b-img id="userStoryPicture" :src="require('@/assets/ActiveUserStory.png')" />
        </b-button>

        <b-button
          v-else-if="hostSelectedStoryIndex === index && !showEditButtons"
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
          :placeholder="$t('page.session.before.userStories.placeholder.userStoryTitle')"
          @blur="publishChanges"
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
      class="w-100 mb-5"
      variant="secondary"
      @click="
        addUserStory();
        $event.target.blur();
      "
    >
      <b-icon-plus />
      {{ $t("page.session.before.userStories.button.addFirstUserStory") }}
    </b-button>

    <b-alert
      v-if="userStories.length < 1 && showEditButtons && filterActive"
      show
      variant="warning"
    >
      {{ $t("page.session.before.userStories.filter.noStoryFound") }}
    </b-alert>

    <b-button
      v-if="userStories.length > 0 && showEditButtons && !filterActive"
      class="w-100 mb-5"
      variant="secondary"
      @click="
        addUserStory();
        $event.target.blur();
      "
    >
      <b-icon-plus />
      {{ $t("page.session.before.userStories.button.addUserStory") }}
    </b-button>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import UserStory from "../model/UserStory";
export default Vue.extend({
  name: "UserStories",
  props: {
    cardSet: { type: Array, required: true },
    initialStories: { type: Array, required: true },
    showEstimations: { type: Boolean, required: true },
    showEditButtons: { type: Boolean, required: false, default: true },
    hostSelectedStoryIndex: { type: Number, required: false, default: null },
  },
  data() {
    return {
      selectedStoryIndex: null as unknown,
      sideBarOpen: false,
      userStories: [] as Array<UserStory>,
      hover: null,
      input: "",
      filterActive: false,
      savedStories: [] as Array<UserStory>,
    };
  },
  watch: {
    initialStories() {
      this.userStories = this.initialStories as Array<UserStory>;
    },
    hostSelectedStoryIndex() {
      this.selectedStoryIndex = this.hostSelectedStoryIndex;
      this.$emit("selectedStory", this.selectedStoryIndex);
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
        id: null,
        title: "",
        description: "",
        estimation: null,
        isActive: false,
      };
      this.userStories.push(story);
      this.setUserStoryAsActive(this.userStories.length - 1);
    },
    swapPriority: function () {
      if (!this.filterActive) {
        this.savedStories = this.userStories;
      }
      this.userStories = this.savedStories;
      if (this.input !== "") {
        let filteredUserStories: UserStory[] = [];
        this.userStories.forEach((userStory) => {
          if (userStory.title.toLowerCase().includes(this.input.toLowerCase())) {
            filteredUserStories.push(userStory);
          }
        });
        if (filteredUserStories.length > 0) {
          this.filterActive = true;
          this.userStories = filteredUserStories;
          this.publishChanges(null, false);
        } else {
          this.filterActive = true;
          this.userStories = [];
          this.publishChanges(null, false);
        }
      } else {
        this.filterActive = false;
        this.userStories = this.savedStories;
        this.publishChanges(null, false);
      }
    },
    deleteStory(index) {
      this.publishChanges(index, true);
    },
    publishChanges(index, remove) {
      this.$emit("userStoriesChanged", { us: this.userStories, idx: index, doRemove: remove });
    },
    markUserStory(index) {
      const stories = this.userStories.map((s) => ({
        id: s.id,
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

<style lang="scss" scoped>
#searchIcon {
  rotate: 90deg;
}

.selectedStory {
  background-color: transparent;
  border: none;
}

.selectedStory:hover {
  background-color: transparent !important;
  border: none;
}

.selectedStory:focus {
  background-color: transparent !important;
  border: none;
  outline: none;
  box-shadow: none;
}

.outlineColorStory {
  background-color: transparent;
  border: none;
}

.outlineColorStory:hover {
  background-color: transparent;
  border: none;
}

.outlineColorStory:focus {
  background-color: transparent !important;
  border: none;
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
  height: 30px;
  width: 30px;
}

#badge {
  background-color: var(--secondary-button);
  color: var(--text-primary-color);
  font-size: large;
}

.btn-secondary:not(:disabled):not(.disabled),
.show > .btn-secondary.dropdown-toggle {
  background-color: var(--primary-button) !important;
}
</style>
