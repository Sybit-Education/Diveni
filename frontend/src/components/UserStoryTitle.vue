<template>
  <div class="list">
    <b-list-group-item
      v-for="(story, idx) of userStories"
      v-show="idx === index"
      :key="story.id"
      class="border-0 title-box"
      variant="outline-secondary"
    >
      <div class="list-group list-group-horizontal">
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
            @blur="publishChanges(idx)"
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
            @click="sendGptTitle"
          >
            <b-icon-stars id="aiStars" />
          </b-button>
          <div v-if="displayAiOption" id="aiOptions" class="text-center mt-1">
            <b-button id="acceptAIOption" class="m-1" @click="acceptGptTitle">
              <b-icon-check2 />
              Keep
            </b-button>
            <b-button class="aiOptionButtons m-1" @click="adjustTitle">
              <b-icon-sliders />
              Adjust
            </b-button>
            <b-button class="aiOptionButtons m-1" @click="retryTitle">
              <b-icon-arrow-repeat />
              Try Again
            </b-button>
            <b-button class="aiOptionButtons m-1" @click="deleteTitle">
              <b-icon-backspace />
              Delete
            </b-button>
          </div>
        </div>

        <b-dropdown
          v-show="host"
          variant="none"
          class="px-3 ml-5 estimationDescription"
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
        </b-dropdown>
      </div>
    </b-list-group-item>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import {useI18n} from "vue-i18n";
export default defineComponent({
  name: "UserStoryTitle",
  props: {
    index: { type: Number, required: true},
    cardSet: { type: Array, required: true },
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
  },
  created() {
    this.userStories = this.initialStories as Array<{
      id: string | null;
      title: string;
      description: string;
      estimation: string | null;
      isActive: boolean;
    }>;
  },
  methods: {
    publishChanges(idx) {
      this.$emit("userStoriesChanged", { us: this.userStories, idx: idx, doRemove: false });
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
    sendGptTitle() {
      this.showImproveTitleButton = false;
      this.requestedUserStoryID = this.userStories[this.index].id;
      this.$emit("improveTitle", { userStory: this.userStories[this.index] });
    },
    acceptGptTitle() {
      this.savedTitle = "";
      this.oldTitleHolder = "";
      this.showImproveTitleButton = false;
      this.acceptedUserStoriesID.push(this.requestedUserStoryID);
      this.$emit("acceptTitle", { userStory: this.requestedUserStoryID });
    },
    adjustTitle() {
      this.userStories[this.index].title = this.oldTitleHolder;
      this.showImproveTitleButton = false;
      this.$emit("adjustTitle");
    },
    retryTitle() {
      this.showImproveTitleButton = false;
      this.$emit("retryTitle", {
        id: this.requestedUserStoryID,
        originalTitle: this.oldTitleHolder,
      });
    },
    deleteTitle() {
      this.oldTitleHolder = "";
      this.showImproveTitleButton = false;
      this.userStories
        .filter((us) => us.id === this.requestedUserStoryID)
        .map((us) => (us.title = ""));
      this.$emit("deleteTitle");
    },
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

#aiStars {
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
