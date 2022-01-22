<template>
  <div>
    <div class="list">
      <b-list-group-item
        v-for="(story, idx) of userStories"
        v-show="idx === index"
        :key="story.name"
        class="border-0"
        variant="outline-secondary"
      >
        <div class="list-group list-group-horizontal">
          <b-form-input
            v-model="userStories[idx].title"
            :disabled="!editDescription"
            class="border"
            size="lg"
            :placeholder="
              $t('page.session.before.userStories.placeholder.userStoryTitle')
            "
            @blur="publishChanges"
          />
          <b-dropdown
            v-show="editDescription"
            class="m-1"
            :text="
              (userStories[idx].estimation
                ? userStories[idx].estimation
                : '?') + '    '
            "
            variant="info"
          >
            <b-dropdown-item
              v-for="num in cardSet"
              :key="num"
              :disabled="!editDescription"
              :value="num == null ? '?' : num"
              @click="userStories[idx].estimation = num"
            >
              {{ num }}
            </b-dropdown-item>
            <b-dropdown-item
              :disabled="!editDescription"
              @click="userStories[idx].estimation = null"
            >
              ?
            </b-dropdown-item>
          </b-dropdown>
          <div
            v-show="!editDescription"
            class="card-body rounded"
            :style="{
              'background-color':
                userStories[idx].estimation == null
                  ? 'white'
                  : 'RGB(13, 202, 240)',
            }"
          >
            {{ story.estimation }}
          </div>
        </div>
        <div>
          <b-form-textarea
            id="textarea-auto-height"
            v-model="userStories[idx].description"
            class="mt-1"
            rows="27"
            max-rows="40"
            :disabled="!editDescription"
            :placeholder="
              $t(
                'page.session.before.userStories.placeholder.userStoryDescription'
              )
            "
            @blur="publishChanges"
          />
        </div>
      </b-list-group-item>
      <div v-if="userStories.length <= index" class="border text-center">
        <b-card title="textextextextextextext" />
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import Vue from "vue";

export default Vue.extend({
  name: "UserStoryDescriptions",
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
      editEnabled: false,
      userStories: [] as Array<{
        title: string;
        description: string;
        estimation: string | null;
        isActive: boolean;
      }>,
    };
  },
  watch: {
    userStories() {
      this.publishChanges();
    },
    initialStories() {
      this.userStories = this.initialStories as Array<{
        title: string;
        description: string;
        estimation: string | null;
        isActive: boolean;
      }>;
    },
    editEnabled() {
      this.publishChanges();
    },
  },
  created() {
    this.userStories = this.initialStories as Array<{
      title: string;
      description: string;
      estimation: string | null;
      isActive: boolean;
    }>;
  },
  methods: {
    setUserStoryAsActive(index) {
      const stories = this.userStories.map((s) => ({
        title: s.title,
        description: s.description,
        estimation: s.estimation,
        isActive: false,
      }));
      stories[index].isActive = true;
      this.userStories = stories;
    },
    addUserStory() {
      this.userStories.push({
        title: "",
        description: "",
        estimation: null,
        isActive: false,
      });
    },
    deleteStory(index) {
      this.userStories.splice(index, 1);
    },
    editOrSave() {
      if (!this.editEnabled) {
        this.publishChanges();
      }
      this.editEnabled = !this.editEnabled;
    },
    publishChanges() {
      this.$emit("userStoriesChanged", this.userStories);
    },
    toggleSideBar() {
      this.sideBarOpen = !this.sideBarOpen;
      this.editEnabled = false;
      this.publishChanges();
    },
  },
});
</script>

<style scoped>
/* The side navigation menu */
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
