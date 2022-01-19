<template>
  <div>
    <div class="list">
      <b-list-group-item
        v-for="(story, index) of userStories"
        :key="story.name"
        class="border"
        variant="outline-secondary"
        :active="story.isActive"
        id="to-hover"
        @click="setUserStoryAsActive(index)"
      >
        <div
          class="list-group list-group-horizontal"
          @mouseover="hover = true"
          @mouseleave="hover = false"
        >
          <b-form-input
            :active="story.isActive"
            :disabled="true"
            class="border-0"
            v-model="userStories[index].title"
            size="lg"
            :style="{
              'background-color': story.isActive
                ? 'rgb(202, 202, 202)'
                : 'white',
            }"
            placeholder="Story title"
            @blur="publishChanges"
          />
          <div v-show="showEditButtons && hover">
            <b-button variant="danger" @click="deleteStory(index)">
              <b-icon-trash />
            </b-button>
          </div>
          <div>
            <div
              v-show="userStories[index].estimation"
              class="card-body rounded"
              :style="{
                'background-color':
                  userStories[index].estimation == null
                    ? 'white'
                    : 'RGB(13, 202, 240)',
              }"
            >
              {{ story.estimation }}
            </div>
          </div>
        </div>
      </b-list-group-item>
      <b-button
        v-if="userStories.length < 1 && showEditButtons"
        size="lg"
        variant="success"
        @click="addUserStory()"
      >
        No stories yet... Add a story to start estimating.
      </b-button>
      <div v-if="userStories.length > 0 && showEditButtons">
        <b-button
          class="w-100 h-100"
          size="lg"
          variant="success"
          @click="addUserStory()"
        >
          Add Story
          <b-icon-plus />
        </b-button>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import Vue from "vue";

export default Vue.extend({
  name: "UserStoryTitles",
  props: {
    cardSet: { type: Array, required: true },
    initialStories: { type: Array, required: true },
    showEstimations: { type: Boolean, required: true },
    showEditButtons: { type: Boolean, required: false, default: true },
    selectStory: { type: Boolean, required: false, default: false },
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
      hover: false,
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
      if (this.selectStory) {
        stories[index].isActive = true;
        this.userStories = stories;
      }
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
.list-group-item.active {
  background-color: rgb(202, 202, 202) !important;
}
.active {
  color: rgb(202, 202, 202);
  background-color: gray;
}
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

#to-show {
  display: none;
}
#to-hover:hover > #to-show {
  display: block;
}
</style>