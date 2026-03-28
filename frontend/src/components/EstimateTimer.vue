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

// Non-reactive internal state
let intervalHandle = -1;
let localStartTime = 0;
let initialElapsed = 0;
let syncGeneration = 0;
let roundExpired = false;
let shouldEmitOnExpiry = false;
let unmounted = false;

// --- Computed ---
const textColor = computed(() => {
  if (timerCount.value === 0) return COLOR_INACTIVE;
  if (!props.startTimestamp && props.pauseTimer) return COLOR_INACTIVE;
  if (timerCount.value > (props.duration * 2) / 3) return COLOR_PLENTY;
  if (timerCount.value > props.duration / 3) return COLOR_WARNING;
  return COLOR_URGENT;
});

const formattedTime = computed(() => {
  if (props.duration === 0) return "∞";
  if (!props.startTimestamp) return props.pauseTimer ? "0:00" : "";
  const minutes = Math.floor(timerCount.value / 60);
  const seconds = (timerCount.value % 60).toString().padStart(2, "0");
  return `${minutes}:${seconds}`;
});

// --- Timer core ---
function clampElapsed(seconds: number): number {
  return Math.min(props.duration, Math.max(0, seconds));
}

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
    roundExpired = true;
    return true;
  }
  timerCount.value = Math.min(props.duration, Math.ceil(remaining));
  return false;
}

function handleExpiry() {
  if (shouldEmitOnExpiry) {
    shouldEmitOnExpiry = false;
    emit("timerFinished");
  }
}

function beginInterval() {
  if (tick()) {
    handleExpiry();
    return;
  }
  intervalHandle = window.setInterval(() => {
    if (tick()) {
      stopInterval();
      handleExpiry();
    }
  }, 100);
}

// --- Server sync ---
async function fetchServerElapsed(gen: number, rawElapsed: number): Promise<number | null> {
  try {
    const response = await axios.get(constants.backendURL + "/get-timer-value", {
      params: { memberID: props.member },
    });
    if (gen !== syncGeneration || unmounted) return null;
    const elapsed = Number(response.data);
    return Number.isFinite(elapsed) ? elapsed / 1000 : rawElapsed;
  } catch {
    if (gen !== syncGeneration || unmounted) return null;
    return rawElapsed;
  }
}

async function startInterval() {
  stopInterval();
  localStartTime = 0;
  if (props.duration === 0 || !props.startTimestamp) return;

  const gen = ++syncGeneration;
  const serverStartMs = new Date(props.startTimestamp).getTime();
  if (Number.isNaN(serverStartMs)) return;

  const rawElapsed = (Date.now() - serverStartMs) / 1000;
  const hasClockSkew = rawElapsed < 0 || rawElapsed > props.duration;

  if (hasClockSkew && props.member) {
    const serverElapsed = await fetchServerElapsed(gen, rawElapsed);
    if (serverElapsed === null) return;
    initialElapsed = clampElapsed(serverElapsed);
  } else {
    initialElapsed = clampElapsed(rawElapsed);
  }

  if (props.pauseTimer) return;

  localStartTime = Date.now();
  beginInterval();
}

// --- Watchers ---
watch(
  () => props.startTimestamp,
  () => {
    roundExpired = false;
    shouldEmitOnExpiry = true;
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
      roundExpired = false;
      timerCount.value = newDuration;
      if (props.votingStarted && props.startTimestamp && !props.pauseTimer) {
        shouldEmitOnExpiry = true;
        startInterval();
      }
    }
  }
);

watch(
  () => props.pauseTimer,
  (paused) => {
    if (paused) {
      if (tick()) handleExpiry();
      stopInterval();
      shouldEmitOnExpiry = false;
      if (!props.startTimestamp) timerCount.value = 0;
    } else if (props.startTimestamp && props.duration > 0 && !roundExpired) {
      shouldEmitOnExpiry = true;
      startInterval();
    }
  }
);

// --- Initialization ---
timerCount.value = props.pauseTimer && !props.startTimestamp ? 0 : props.duration;
if (props.votingStarted && props.startTimestamp && !props.pauseTimer) {
  shouldEmitOnExpiry = true;
  startInterval();
}

onBeforeUnmount(() => {
  unmounted = true;
  stopInterval();
});
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
