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
      @click="setUserStoryAsActive(index)"
    >
      <div class="list-group list-group-horizontal">
        <b-form-input
          v-model="userStories[index].title"
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
        <div>
          <div
            v-if="userStories[index].estimation"
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
      <div
        v-if="index === number && exist"
        :style="{ 'background-color': 'black' }"
      >
        <b-form-textarea
          id="textarea-auto-height"
          v-model="userStories[index].description"
          class="mt-1"
          rows="27"
          max-rows="40"
          :disabled="true"
        />
      </div>
    </b-list-group-item>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
  name: "MobileStoryList",
  props: {
    cardSet: { type: Array, required: true },
    initialStories: { type: Array, required: true },
    showEstimations: { type: Boolean, required: true },
    showEditButtons: { type: Boolean, required: false, default: true },
    selectStory: { type: Boolean, required: false, default: false },
  },
  data() {
    return {
      exist: false,
      number: null,
      sideBarOpen: false,
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
    initialStories() {
      this.userStories = this.initialStories as Array<{
        title: string;
        description: string;
        estimation: string | null;
        isActive: boolean;
      }>;
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
      this.exist = !this.exist;
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
