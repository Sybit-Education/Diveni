<template>
  <div>
    <div class="sidenavButton">
      <b-button size="lg" variant="info" @click="toggleSideBar()">
        <b-icon-card-list /> Stories
      </b-button>
    </div>
    <div class="sidenav" :style="`width: ${sideBarOpen? '400px': '0px'};` ">
      <div>
        <b-button size="lg" variant="success" @click="addUserStory()">
          Add <b-icon-plus />
        </b-button>
        <b-button size="lg" class="m-2" variant="warning" @click="editOrSave()">
          {{ editEnabled ? 'Save' : 'Edit' }} <b-icon-pencil />
        </b-button>
      </div>
      <div class="list-group">
        <div v-if="userStories.length < 1" class="text-center">
          No stories yet...
          Add a story to start estimating.
        </div>
        <div
          v-for="(story, index) of userStories"
          :key="story.name"
          class="list-group-item"
        >
          <b-input-group>
            <b-button
              v-b-toggle="`collapse-${index}`"
              variant="outline-secondary"
            >
              <b-icon-caret-down />
            </b-button>
            <b-form-input
              v-model="userStories[index].title"
              :readonly="!editEnabled"
              size="lg"
              placeholder="Story title"
            />
            <b-dropdown
              v-if="editEnabled && showEstimations"
              :text="(userStories[index].estimation ? userStories[index].estimation : '?') + '    ' "
              variant="info"
            >
              <b-dropdown-item
                v-for="num in cardSet" :key="num"
                :value="num == null ? '?' : num"
                @click="userStories[index].estimation = num"
              >
                {{ num }}
              </b-dropdown-item>
              <b-dropdown-item @click="userStories[index].estimation = null">
                ?
              </b-dropdown-item>
            </b-dropdown>

            <b-button
              v-if="!editEnabled && showEstimations"
              class="mx-2"
              pill variant="info"
            >
              {{ story.estimation ? story.estimation: '?' }}
            </b-button>

            <b-button v-if="editEnabled" variant="danger" @click="deleteStory(index)">
              <b-icon-trash />
            </b-button>
          </b-input-group>

          <b-collapse :id="`collapse-${index}`" class="mt-4">
            <b-form-textarea
              v-model="userStories[index].description"
              :disabled="!editEnabled"
              placeholder="Description and acceptance criteria..."
              rows="3"
              max-rows="6"
            />
          </b-collapse>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import Vue from 'vue';

export default Vue.extend({
  name: 'UserStoriesSidebar',
  props: {
    cardSet: { type: Array, required: true },
    showEstimations: { type: Boolean, required: true },
  },
  data() {
    return {
      sideBarOpen: false,
      editEnabled: false,
      userStories: [] as Array<{title:string, description:string, estimation:string|null }>,
    };
  },
  watch: {
    userStories() {
      this.publishChanges();
    },
    editEnabled() {
      this.publishChanges();
    },
  },
  methods: {
    addUserStory() {
      this.userStories.push({ title: '', description: '', estimation: null });
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
      this.$emit('userStoriesChanged', this.userStories);
    },
    toggleSideBar() {
      this.sideBarOpen = !this.sideBarOpen;
      this.editEnabled = false;
      this.publishChanges();
    },
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
/* The side navigation menu */
.sidenav {
  float: right;
  height: 100%; /* 100% Full-height */
  width: 0; /* 0 width - change this with JavaScript */
  position: fixed; /* Stay in place */
  z-index: 1; /* Stay on top */
  top: 0; /* Stay at the top */
  right:0;
  background-color: white;
  overflow-x: hidden; /* Disable horizontal scroll */
  transition: 0.2s; /* 0.5 second transition effect to slide in the sidenav */
}

 .sidenavButton {
   margin: 8px;
  float: right;
  position: fixed;
  z-index: 2;
  top: 0;
  right:0;
  overflow-x: hidden;
}

/* On smaller screens, where height is less than 450px, change the style of the sidenav (less padding and a smaller font size) */
@media screen and (max-height: 450px) {
  .sidenav {padding-top: 15px;}
  .sidenav a {font-size: 18px;}
}
</style>
