<template>
  <div>
    <div class="list">
      <b-list-group-item
        v-for="(story, idx) of userStories"
        v-show="story.isActive"
        :key="idx"
        class="border-0"
        variant="outline-secondary"
      >
        <div class="list-group list-group-horizontal">
          <b-form-textarea
            v-model="userStories[idx].title"
            rows="1"
            max-rows="3"
            :disabled="true"
            class="border"
            size="lg"
            :placeholder="
              $t('page.session.before.userStories.placeholder.userStoryTitle')
            "
            @blur="publishChanges"
          />
        </div>
      </b-list-group-item>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
  name: "MobileStoryTitle",
  props: {
    index: { type: Number, required: true },
    cardSet: { type: Array, required: true },
    initialStories: { type: Array, required: true },
    editDescription: { type: Boolean, required: true, default: false },
    showEstimations: { type: Boolean, required: false },
    showEditButtons: { type: Boolean, required: false, default: true },
  },
  emits: [ 'userStoriesChanged' ],
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
