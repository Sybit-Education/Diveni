<template>
  <div>
    <b-list-group-item
      v-for="(story, index) of userStories"
      :key="story.name"
      class="rounded"
      variant="outline-secondary"
      :style="{
        'border-color': story.isActive ? 'RGB(202, 202, 202)' : 'white',
      }"
      :active="story.isActive"
      @mouseover="hover = index"
      @mouseleave="hover = null"
      @click="setUserStoryAsActive(index)"
    >
      <div class="list-group list-group-horizontal">
        <b-button
          v-if="showEditButtons"
          class="m-1"
          :variant="story.isActive ? 'success' : 'outline-success'"
          @click="markUserStory(index)"
        >
          <b-icon-check2 />
        </b-button>
        <b-form-input
          v-model="userStories[index].title"
          :active="index === 0"
          :disabled="true"
          class="border-1"
          size="lg"
          :style="{
            'background-color':
              index === number ? 'RGB(202, 202, 202)' : 'white',
          }"
          :placeholder="
            $t('page.session.before.userStories.placeholder.userStoryTitle')
          "
          @blur="publishChanges"
        />
        <b-button
          v-show="showEditButtons && hover === index"
          variant="danger"
          @click="deleteStory(index)"
        >
          <b-icon-trash />
        </b-button>
        <div>
          <div
            v-show="userStories[index].estimation"
            class="card-body rounded"
            :style="{
              'background-color':
                userStories[index].estimation == null
                  ? 'white'
                  : 'RGB(13, 202, 240)',
              width: '48px',
              'font-size': 'larger',
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
      {{ $t("page.session.before.userStories.button.addFirstUserStory") }}
    </b-button>
    <div v-if="userStories.length > 0 && showEditButtons">
      <b-button
        class="w-100 h-100"
        size="lg"
        variant="success"
        @click="addUserStory()"
      >
        {{ $t("page.session.before.userStories.button.addUserStory") }}
        <b-icon-plus />
      </b-button>
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
      number: null,
      sideBarOpen: false,
      editEnabled: false,
      userStories: [] as Array<{
        title: string;
        description: string;
        estimation: string | null;
        isActive: boolean;
      }>,
      hover: null,
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
      this.number = index;
      this.$emit("selectedStory", index);
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
    markUserStory(index) {
      const stories = this.userStories.map((s) => ({
        title: s.title,
        description: s.description,
        estimation: s.estimation,
        isActive: false,
      }));
      stories[index].isActive = true;
      this.userStories = stories;
    },
  },
});
</script>

<style scoped>
.list-group-item.active {
  background-color: white !important;
  border-width: 3px;
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
</style>
