<template>
  <div class="d-flex justify-content-end estimate-timer">
    <b-card-text :style="{ color: textColor }" class="timer">
      {{ formattedTime }}
    </b-card-text>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onBeforeUnmount } from "vue";
import axios from "axios";
import constants from "@/constants";

const COLOR_INACTIVE = "#808080";
const COLOR_PLENTY = "#229954";
const COLOR_WARNING = "#D4AC0D";
const COLOR_URGENT = "#CB4335";

const props = withDefaults(
  defineProps<{
    startTimestamp: string;
    duration: number;
    pauseTimer: boolean;
    member?: string;
    votingStarted: boolean;
  }>(),
  { member: "" }
);

const emit = defineEmits<{ timerFinished: [] }>();

const timerCount = ref(0);
let intervalHandle = -1;
let localStartTime = 0;
let initialElapsed = 0;
let syncGeneration = 0;

const textColor = computed(() => {
  if (timerCount.value === 0) return COLOR_INACTIVE;
  if (timerCount.value > (props.duration * 2) / 3) return COLOR_PLENTY;
  if (timerCount.value > props.duration / 3) return COLOR_WARNING;
  return COLOR_URGENT;
});

const formattedTime = computed(() => {
  if (props.duration === 0) return "∞";
  if (!props.startTimestamp) return "";
  const minutes = Math.floor(timerCount.value / 60);
  const seconds = (timerCount.value % 60).toString().padStart(2, "0");
  return `${minutes}:${seconds}`;
});

function stopInterval() {
  if (intervalHandle !== -1) {
    clearInterval(intervalHandle);
    intervalHandle = -1;
  }
}

function tick(): boolean {
  if (localStartTime === 0) return false;

  const localElapsed = (Date.now() - localStartTime) / 1000;
  const remaining = props.duration - initialElapsed - localElapsed;

  if (remaining <= 0) {
    timerCount.value = 0;
    return true;
  }
  timerCount.value = Math.min(props.duration, Math.ceil(remaining));
  return false;
}

function beginInterval() {
  if (tick()) {
    emit("timerFinished");
    return;
  }
  intervalHandle = window.setInterval(() => {
    if (tick()) {
      emit("timerFinished");
      stopInterval();
    }
  }, 100);
}

async function startInterval(resumeFromCurrent = false) {
  stopInterval();
  if (props.duration === 0 || !props.startTimestamp) return;

  const gen = ++syncGeneration;

  if (resumeFromCurrent) {
    initialElapsed = props.duration - timerCount.value;
    localStartTime = Date.now();
    beginInterval();
    return;
  }

  const serverStartMs = new Date(props.startTimestamp).getTime();
  if (Number.isNaN(serverStartMs)) return;

  const now = Date.now();
  const rawElapsed = (now - serverStartMs) / 1000;

  // Clock skew detected: Use backend endpoint to get the true server-side elapsed time for members
  if ((rawElapsed < 0 || rawElapsed > props.duration) && props.member) {
    try {
      const response = await axios.get(constants.backendURL + "/get-timer-value", {
        params: { memberID: props.member },
      });
      if (gen !== syncGeneration) return;
      initialElapsed = Math.max(0, response.data / 1000);
    } catch {
      if (gen !== syncGeneration) return;
      initialElapsed = Math.max(0, rawElapsed);
    }
  } else {
    initialElapsed = Math.max(0, rawElapsed);
  }

  localStartTime = Date.now();
  beginInterval();
}

watch(
  () => props.startTimestamp,
  () => {
    stopInterval();
    timerCount.value = props.duration;
    if (!props.pauseTimer) {
      startInterval();
    }
  }
);

watch(
  () => props.duration,
  (newDuration, oldDuration) => {
    if (oldDuration === 0 && newDuration > 0) {
      timerCount.value = newDuration;
      if (props.votingStarted && props.startTimestamp && !props.pauseTimer) {
        startInterval();
      }
    }
  }
);

watch(
  () => props.pauseTimer,
  (paused) => {
    if (paused) {
      tick();
      stopInterval();
    } else if (props.startTimestamp && props.duration > 0) {
      startInterval(true);
    }
  }
);

timerCount.value = props.duration;
if (props.votingStarted && props.startTimestamp && !props.pauseTimer) {
  startInterval();
}

onBeforeUnmount(stopInterval);
</script>

<style lang="scss" scoped>
.estimate-timer {
  font-size: 1.5rem;
  font-weight: 700;
  border-radius: 0.5rem;
}

.card-text {
  line-height: 485%;
  text-align: center;
}

.timer {
  width: 100px;
  height: 117px;
  background-image: url("@/assets/Timer.png");
  background-size: contain;
  user-select: none;
}
</style>
