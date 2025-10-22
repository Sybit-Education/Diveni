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
          :placeholder="t('page.session.before.userStories.placeholder.searchUserStories')"
          @input="swapPriority"
        />
      </b-input-group>
    </div>
    <b-card-group id="userStoryBlock" class="my-2 overflow-auto">
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
          variant="primary"
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
        <b-button
          v-if="showEditButtons && hasApiKey"
          v-show="userStories[index].title !== '' && userStories[index].description !== ''"
          id="stars"
          @click="
            selectedStoryIndex = index;
            showPrivacyModal = true;
          "
        >
          <BIconStars />
        </b-button>
        <b-form-input
          id="userStoryTitles"
          v-model="story.title"
          class="mx-1 w-100 shadow-none"
          readonly
          size="sm"
          :placeholder="t('page.session.before.userStories.placeholder.userStoryTitle')"
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
    <PrivacyModal
      v-if="showPrivacyModal"
      :current-title="userStories[selectedStoryIndex].title"
      :current-text="userStories[selectedStoryIndex].description"
      :is-description="true"
      @resetShowModal="showPrivacyModal = false"
      @sendGPTRequest="submitRequest"
    />
    <SplitUserStoriesModal
      v-if="splittedUserStoriesData.length > 0 && !showPrivacyModal"
      :new-user-stories-list="splittedUserStoriesData"
      :original-user-story="[userStories[storyToSplitIdx]]"
      @resetShowModal="
        showUserStorySplitModal = false;
        splittedUserStoriesData = [];
      "
      @acceptSplitting="acceptSplitting"
      @retry="retry"
    />
    <UserStoryDeleteModal
      v-model:is-visible="showDeleteConfirmModal"
      :story-mode="storyMode"
      @confirmDelete="confirmDelete"
    />
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import UserStory from "../model/UserStory";
import { useI18n } from "vue-i18n";
import PrivacyModal from "@/components/PrivacyModal.vue";
import SplitUserStoriesModal from "@/components/SplitUserStoriesModal.vue";
import UserStoryDeleteModal from "@/components/UserStoryDeleteModal.vue";

export default defineComponent({
  name: "UserStories",
  components: { SplitUserStoriesModal, PrivacyModal, UserStoryDeleteModal },
  props: {
    cardSet: { type: Array, required: true },
    initialStories: { type: Array, required: true },
    host: { type: Boolean, required: true, default: false },
    showEstimations: { type: Boolean, required: true },
    showEditButtons: { type: Boolean, required: false, default: true },
    hostSelectedStoryIndex: { type: Number, required: false, default: null },
    storyMode: { type: String, required: false, default: null },
    splittedUserStories: { type: Array<UserStory>, required: false, default: [] },
    storyToSplitIdx: { type: Number, required: false, default: 0 },
    hasApiKey: { type: Boolean, required: false, default: false },
  },
  setup() {
    const { t } = useI18n();
    return { t };
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
      generatedUUIDs: new Set<number>(),
      showPrivacyModal: false,
      showUserStorySplitModal: false,
      splittedUserStoriesData: [] as Array<UserStory>,
      showDeleteConfirmModal: false,
      indexToDelete: null,
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
    this.splittedUserStoriesData = this.splittedUserStories;
  },
  methods: {
    setUserStoryAsActive(index) {
      if (index >= this.userStories.length) {
        console.log("Pressed after deletion");
      } else {
        this.selectedStoryIndex = index;
        this.$emit("selectedStory", index);
        this.publishChanges(index, false);
      }
    },
    addUserStory() {
      const story: UserStory = {
        id: this.storyMode === "US_JIRA" ? null : this.generateNumericUUID().toString(),
        title: "",
        description: "",
        estimation: null,
        isActive: false,
      };
      this.userStories.push(story);
      this.setUserStoryAsActive(this.userStories.length - 1);
      this.$nextTick(() => {
        const inputField = document.getElementById(
          `titleInputField_${this.userStories.length - 1}`
        ) as HTMLInputElement;
        if (inputField && this.host) {
          inputField.focus();
        }
      });
    },
    swapPriority: function () {
      if (!this.filterActive) {
        this.savedStories = this.userStories;
      }
      this.userStories = this.savedStories;
      if (this.input !== "") {
        const filteredUserStories: UserStory[] = [];
        this.userStories.forEach((userStory) => {
          if (userStory.title.toLowerCase().includes(this.input.toLowerCase())) {
            filteredUserStories.push(userStory);
          }
        });
        if (filteredUserStories.length > 0) {
          this.filterActive = true;
          this.userStories = filteredUserStories;
        } else {
          this.filterActive = true;
          this.userStories = [];
        }
      } else {
        this.filterActive = false;
        this.userStories = this.savedStories;
      }
    },
    deleteStory(index) {
      this.indexToDelete = index;
      this.showDeleteConfirmModal = true;
    },
    confirmDelete() {
      const index = this.indexToDelete;
      if (index !== null) {
        this.publishChanges(index, true);
        this.indexToDelete = null;
      }
    },
    publishChanges(index, remove) {
      if (this.userStories[index] !== undefined) {
        if (this.userStories[index].title !== "" || remove) {
          this.$emit("userStoriesChanged", { us: this.userStories, idx: index, doRemove: remove });
        }
      }
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
    generateNumericUUID() {
      let uuid: number;
      do {
        uuid = Math.floor(Math.random() * 1e15) + Date.now();
      } while (this.generatedUUIDs.has(uuid));
      this.generatedUUIDs.add(uuid);
      return uuid;
    },
    submitRequest({ confidentialData, language }) {
      this.$emit("sendGPTRequest", {
        confidentialData: confidentialData,
        language: language,
        retry: false,
      });
    },
    acceptSplitting({ newUserStories }) {
      newUserStories.map((us) => this.userStories.push(us));
      this.publishChanges(this.storyToSplitIdx, true);
      if (this.storyMode === "US_JIRA") {
        let count = newUserStories.length;
        newUserStories.forEach(() => {
          this.publishChanges(this.userStories.length - count, false);
          count = count - 1;
        });
      }
    },
    retry() {
      this.$emit("sendGPTRequest", { retry: true });
    },
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS/SCSS to this component only -->
<style lang="scss" scoped>
#stars {
  color: var(--ai-stars) !important;
  background-color: transparent !important;
  border-style: none;
  animation: showUp 1s;
}

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
