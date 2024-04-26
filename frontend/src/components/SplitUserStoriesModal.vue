<template>
  <div
    id="splitUserStoriesModal"
  >
    <b-modal
      v-model="showModal"
      centered
      hide-header-close
      @hide="hideModal"
      size="xl"
    >
      <template #modal-header>
        <div class="text-center">
          {{ t("general.aiFeature.splitUserStoriesModal.title") }}
        </div>
      </template>
      <b-container class="max-height-modal overflow-auto">
        <b-row>
          <b-col>
            <div class="text-center font-italic">
              {{ t("general.aiFeature.splitUserStoriesModal.originalStory") }}
            </div>
            <b-form-textarea
              v-model="originalTitleData"
              class="overflow-auto font-weight-bold titleInputField"
              :disabled="true"
            />
            <UiToastEditorWrapper
              class="editor"
              :initial-value="originalUserStory[0].description"
              :none-clickable="true"
            />
          </b-col>
          <b-col>
            <div class="text-center font-italic">
              {{ t("general.aiFeature.splitUserStoriesModal.newStories") }}
            </div>
            <b-card-group id="userStoryBlock" class="overflow-auto center">
              <b-list-group-item
                v-for="(story,index) of newUserStoriesEditable"
                id="userStoryRow"
                :key="index"
                class="w-100 p-1 d-flex justify-content-left"
                :class="index === idx ? 'selected-Story' : ''"
                @click="switchToUserStory(index)"
              >
                <b-form-input
                  id="userStoryTitles"
                  v-model="story.title"
                  class="mx-1 w-100 shadow-none"
                  readonly
                  size="sm"
                  :placeholder="t('page.session.before.userStories.placeholder.userStoryTitle')"
                />
                <b-button
                  variant="outline-danger"
                  class="border-0"
                  size="sm"
                  @click="deleteStory(index)"
                >
                  <b-icon-trash />
                </b-button>
              </b-list-group-item>
              <div
                class="selectedStoryLine"
                :style="{'top': 50 + (50 * this.idx) + 'px'}"
              />
            </b-card-group>
          </b-col>
          <b-col class="newUserStoryCol">
            <div class="text-center font-italic">
              {{ t("general.aiFeature.splitUserStoriesModal.selectedStory") }}
            </div>
            <b-form-textarea
              v-model="newUserStoriesEditable[idx].title"
              class="overflow-auto font-weight-bold titleInputField"
              rows="1"
              size="lg"
              disabled
            />
            <UiToastEditorWrapper
              class="editor"
              :initial-value="newUserStoriesEditable[idx].description"
              :none-clickable="true"
              :key="idx"
            />
          </b-col>
        </b-row>
      </b-container>
      <template #modal-footer >
        <div id="aiOptions" class="text-center mt-1">
          <b-button id="acceptAISplitButton" class="m-1" @click="acceptDescription">
            <b-icon-check2 />
            Keep
          </b-button>
          <b-button class="aiOptionButtons m-1" @click="retryDescription(); $event.target.blur()">
            <b-icon-arrow-repeat />
            Try Again
          </b-button>
          <b-button class="aiOptionButtons m-1" @click="deleteDescription">
            <b-icon-backspace />
            Delete
          </b-button>
        </div>
      </template>
    </b-modal>
  </div>

</template>

<script lang="ts">
import {defineComponent} from "vue";
import UiToastEditorWrapper from "@/components/UiToastEditorWrapper.vue";
import {useI18n} from "vue-i18n";
import UserStory from "@/model/UserStory";
export default defineComponent({
  name: "SplitUserStoriesModal",
  components: {UiToastEditorWrapper},
  props: {
    originalUserStory: {type: Array<UserStory>, required: true},
    newUserStoriesList: {type: Array<UserStory>, required: true},
  },
  setup() {
    const { t } = useI18n();
    return { t };
  },
  created() {
    this.originalTitleData = this.originalUserStory[0].title;
  },
  mounted() {
    this.newUserStoriesEditable = this.newUserStoriesList;
  },
  data() {
    return {
      showModal: true,
      originalTitleData: "",
      idx: 0,
      newUserStoriesEditable: [] as Array<UserStory>,
    }
  },
  methods: {
    hideModal() {
      this.showModal = false;
      this.$emit("resetShowModal");
    },
    deleteStory(index) {
      this.idx = 0;
      this.newUserStoriesEditable.splice(index, 1);
    },
    switchToUserStory(index) {
      if (index >= this.newUserStoriesEditable.length) {
        console.log("clicked after deletion");
      } else {
        this.idx = index;
      }
    },
    acceptDescription() {
      this.$emit("acceptSplitting", {newUserStories: this.newUserStoriesEditable})
      this.hideModal();
    },
    retryDescription() {
      this.$emit("retry");
    },
    deleteDescription() {
      this.hideModal();
    },
  },
});
</script>

<style scoped lang="scss">
.newUserStoryCol {
  border: 3px solid;
  box-shadow: inset 4px 4px 10px var(--text-primary-color), 4px 4px 10px var(--text-primary-color);
  border-radius: 1rem;
  height: 30%;
}

.selectedStoryLine {
  position: absolute;
  right: 0;
  border-bottom: 2px solid var(--text-primary-color);
  box-shadow: 0 10px 5px var(--text-primary-color);
  width: 16px;
}

.titleInputField {
  width: 100%;
  background-color: transparent !important;
}
.titleInputField:hover {
  width: 100%;
  background-color: transparent !important;
  border-color: #ced4da;
}

.max-height-modal{
  height: 500px;
}

.selected-Story{
  border: var(--ai-stars) 3px solid!important;
}

#userStoryBlock {
  overflow: auto;
  -webkit-overflow-scrolling: touch;
}

#userStoryRow {
  height: 50px;
  border: var(--text-primary-color) 3px solid;
  color: var(--text-primary-color);
  background-color: transparent;
}

#userStoryTitles {
  background-color: transparent;
  color: var(--text-primary-color);
  font-size: large;
  border: none;
  cursor: pointer;
}

.editor {
  height: 80%;
}

/*AI Option Buttons*/
#acceptAISplitButton {
  background-color: var(--ai-stars) !important;
  color: white !important;
  border-style: none;
}

.aiOptionButtons {
  border: none !important;
  border-radius: 0 !important;
  background-color: transparent !important;
  transition: color 0.3s linear !important;

  &:hover {
    background-color: transparent !important;
    color: var(--ai-stars) !important;
    border-radius: 1em !important;
  }
}
</style>
