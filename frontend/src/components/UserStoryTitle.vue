<template>
  <div class="list">
      <b-list-group-item
        v-for="(story, idx) of userStories"
        v-show="idx === index"
        :key="story.id"
        class="border-0 title-box"
        variant="outline-secondary"
      >
        <div class="list-group list-group-horizontal w-100">
          <div class="position-relative w-100 text-center">
            <b-form-textarea
              id="titleInputField"
              v-model="userStories[idx].title"
              v-debounce:1s="showGptButtonTitle"
              class="overflow-auto"
              rows="1"
              size="lg"
              :disabled="!host"
              :placeholder="t('page.session.before.userStories.placeholder.userStoryTitle')"

            />
            <b-button
              v-if="
              !displayAiOption &&
              host &&
              showImproveTitleButton &&
              (savedTitle === '' || savedTitle === userStories[idx].title) &&
              userStories[idx].title !== ''
            "
              id="submitAI"
              @click="showModalTitle = true"
            >
              <b-icon-stars class="aiStars" />
            </b-button>
            <b-spinner
              v-if="showSpinner && !displayAiOption"
            >

            </b-spinner>
            <div v-if="displayAiOption" id="aiOptions" class="text-center mt-1">
              <b-button id="acceptAIOption" class="m-1" @click="acceptGptTitle">
                <b-icon-check2 />
                {{ t("general.aiFeature.optionButtons.keep") }}
              </b-button>
              <b-button class="aiOptionButtons m-1" @click="adjustTitle">
                <b-icon-sliders />
                {{ t("general.aiFeature.optionButtons.adjust") }}
              </b-button>
              <b-button class="aiOptionButtons m-1" @click="retryTitle">
                <b-icon-arrow-repeat />
                {{ t("general.aiFeature.optionButtons.tryAgain") }}
              </b-button>
              <b-button class="aiOptionButtons m-1" @click="deleteTitle">
                <b-icon-backspace />
                {{ t("general.aiFeature.optionButtons.delete") }}
              </b-button>
            </div>
          </div>
          <b-dropdown
            v-show="host"
            variant="none"
            class="px-3 ml-3 estimationDescription"
            :text="(userStories[idx].estimation ? userStories[idx].estimation : '?') + '    '"
          >
            <b-dropdown-item
              v-for="num in cardSet"
              :key="num"
              :disabled="!host"
              :value="num"
              @click="
              userStories[idx].estimation = num;
              publishChanges(idx);
              $event.target.blur();
            "
            >
              {{ num }}
            </b-dropdown-item>
            <b-dropdown-item
              v-if="showAIEstimationButton"
              @click="showModalEstimation = true"
            >
              <BIconStars/>
            </b-dropdown-item>
          </b-dropdown>
        </div>
      </b-list-group-item>
    <privacy-modal
      v-if="showModalTitle"
      :current-text="userStories[index].title"
      @sendGPTRequest="sendGptTitle"
      @resetShowModal="showModalTitle = false"
    />
    <privacy-modal
      v-if="showModalEstimation && showAIEstimationButton"
      :current-title="userStories[index].title"
      :current-text="userStories[index].description"
      @sendGPTRequest="submitAiEstimation"
      @resetShowModal="showModalEstimation = false;"
    />
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import {useI18n} from "vue-i18n";
import PrivacyModal from "@/components/PrivacyModal.vue";
export default defineComponent({
  name: "UserStoryTitle",
  components: {PrivacyModal},
  props: {
    index: { type: Number, required: true},
    cardSet: { type: Array, required: false },
    initialStories: { type: Array, required: true },
    host: { type: Boolean, required: true, default: false},
    displayAiOption: { type: Boolean, required: false, default: false },
    alternateTitle: { type: String, required: false, default: "" },
  },
  setup() {
    const { t } = useI18n();
    return { t };
  },
  data() {
    return {
      sideBarOpen: false,
      userStories: [] as Array<{
        id: string | null;
        title: string;
        description: string;
        estimation: string | null;
        isActive: boolean;
      }>,
      showImproveTitleButton: false,
      savedTitle: "",
      oldTitleHolder: "",
      requestedUserStoryID: "" as string | null,
      acceptedUserStoriesID: [] as Array<string | null>,
      showSpinner: false,
      showModalTitle: false,
      showModalEstimation: false,
      confidentialInformation: {} as Map<string,string>,
      showAIEstimationButton: false,
    };
  },
  watch: {
    initialStories() {
      this.userStories = this.initialStories as Array<{
        id: string | null;
        title: string;
        description: string;
        estimation: string | null;
        isActive: boolean;
      }>;
    },
    alternateTitle() {
      if (this.alternateTitle !== "") {
        this.oldTitleHolder = this.userStories[this.index].title;
        this.userStories[this.index].title = this.alternateTitle;
      }
    },
    userStories() {
      if (this.userStories[this.index] !== undefined) {
        if (this.userStories.length > 0) {
          this.showAIEstimationButton = this.userStories[this.index].title !== '' && this.userStories[this.index].description !== '';
        }
      }
    }
  },
  created() {
    this.userStories = this.initialStories as Array<{
      id: string | null;
      title: string;
      description: string;
      estimation: string | null;
      isActive: boolean;
    }>;
    this.confidentialInformation = new Map();
  },
  methods: {
    publishChanges(idx) {
      if (this.userStories[idx].title !== '') {
        this.$emit("userStoriesChanged", { us: this.userStories, idx: idx, doRemove: false });
      }
    },
    showGptButtonTitle() {
      if (
        this.acceptedUserStoriesID.filter((id) => id === this.userStories[this.index].id).pop() ===
        undefined
      ) {
        // you can only Accept a story once
        console.log("Showing GPT Button");
        this.showImproveTitleButton = true;
        this.savedTitle = this.userStories[this.index].title;
      }
    },
    sendGptTitle({confidentialData}) {
      this.confidentialInformation = confidentialData;
      this.showSpinner = true;
      this.showImproveTitleButton = false;
      this.requestedUserStoryID = this.userStories[this.index].id;
      this.showModalTitle = false;
      this.$emit("improveTitle", { userStory: this.userStories[this.index], confidentialInformation: confidentialData });
    },
    acceptGptTitle() {
      this.showSpinner = false;
      this.savedTitle = "";
      this.oldTitleHolder = "";
      this.showImproveTitleButton = false;
      this.acceptedUserStoriesID.push(this.requestedUserStoryID);
      this.$emit("acceptTitle", { userStory: this.requestedUserStoryID });
    },
    adjustTitle() {
      this.showSpinner = false;
      this.userStories[this.index].title = this.oldTitleHolder;
      this.showImproveTitleButton = false;
      this.$emit("adjustTitle");
    },
    retryTitle() {
      this.showSpinner = true;
      this.showImproveTitleButton = false;
      this.$emit("retryTitle", {
        id: this.requestedUserStoryID,
        originalTitle: this.savedTitle,
        confidentialData: this.confidentialInformation,
      });
    },
    deleteTitle() {
      this.showSpinner = false;
      this.oldTitleHolder = "";
      this.showImproveTitleButton = false;
      this.userStories
        .filter((us) => us.id === this.requestedUserStoryID)
        .map((us) => (us.title = ""));
      this.$emit("deleteTitle");
    },
    submitAiEstimation({confidentialData}) {
      this.confidentialInformation = confidentialData;
      this.showModalEstimation = false;
      this.$emit("aiEstimation", {confidentialData: confidentialData});
    }
  }
})
</script>

<style scoped lang="scss">
#acceptAIOption {
  background-color: var(--ai-stars) !important;
  color: white !important;
  border-style: none;
}

.aiOptionButtons {
  border: none !important;
  border-radius: 0 !important;
  background-color: transparent !important;
  color: black !important;
  transition: color 0.3s linear !important;

  &:hover {
    background-color: transparent !important;
    color: var(--ai-stars) !important;
    border-radius: 1em !important;
  }
}

#aiOptions {
  background-color: white;
  border-radius: 0.5em;
  display: inline-flex;
  animation: showUp 1s;
}

.aiStars {
  height: 2em;
  width: 2em;
}

#titleInputField {
  width: 100%;
  padding: 1em;
  padding-right: 3em;
  border: 0;
  background-color: var(--text-field) !important;
}
#submitAI {
  position: absolute;
  right: 0.75em;
  bottom: 0;
  color: var(--ai-stars) !important;
  background-color: transparent !important;
  border-style: none;
  animation: showUp 1s;
}

@keyframes showUp {
  0% {
    opacity: 0;
  }
  100% {
    opacity: 1;
  }
}

.title-box {
  background-color: transparent;
  padding: 0;
}

.estimationDescription {
  border: 2px solid var(--estimateButtonBorder);
  border-radius: 13px;
  background-color: var(--secondary-button) !important;
}

.estimationDescription:hover {
  border: 2px solid var(--estimateButtonBorder);
  border-radius: 13px;
  background-color: var(--secondary-button-hovered) !important;
}
</style>
