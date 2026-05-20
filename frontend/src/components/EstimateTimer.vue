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

const TICK_INTERVAL_MS = 100;
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

// --- State ---
const timerCount = ref(0);

let tickHandle = -1;
let tickOrigin = 0;
let elapsedBeforeTick = 0;
let asyncGeneration = 0;
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

function stopTicking() {
  if (tickHandle !== -1) {
    clearInterval(tickHandle);
    tickHandle = -1;
  }
}

// Returns true when the timer has expired
function tick(): boolean {
  if (tickOrigin === 0) return false;

  const tickElapsed = (Date.now() - tickOrigin) / 1000;
  const remaining = props.duration - elapsedBeforeTick - tickElapsed;

  if (remaining <= 0) {
    timerCount.value = 0;
    roundExpired = true;
    return true;
  }
  timerCount.value = Math.min(props.duration, Math.ceil(remaining));
  return false;
}

function onExpired() {
  if (shouldEmitOnExpiry) {
    shouldEmitOnExpiry = false;
    emit("timerFinished");
  }
}

function startTicking() {
  if (tick()) {
    onExpired();
    return;
  }
  tickHandle = window.setInterval(() => {
    if (tick()) {
      stopTicking();
      onExpired();
    }
  }, TICK_INTERVAL_MS);
}

// --- Timer sync ---
async function fetchServerElapsed(gen: number, fallback: number): Promise<number | null> {
  try {
    const response = await axios.get(constants.backendURL + "/get-timer-value", {
      params: { memberID: props.member },
    });
    if (gen !== asyncGeneration || unmounted) return null;
    const elapsed = Number(response.data);
    return Number.isFinite(elapsed) ? elapsed / 1000 : fallback;
  } catch {
    if (gen !== asyncGeneration || unmounted) return null;
    return fallback;
  }
}

async function syncTimer() {
  stopTicking();
  tickOrigin = 0;
  if (props.duration === 0 || !props.startTimestamp) return;

  const gen = ++asyncGeneration;
  const serverStartMs = new Date(props.startTimestamp).getTime();
  if (Number.isNaN(serverStartMs)) return;

  const rawElapsed = (Date.now() - serverStartMs) / 1000;
  const hasClockSkew = rawElapsed < 0 || rawElapsed > props.duration;

  if (hasClockSkew && props.member) {
    const serverElapsed = await fetchServerElapsed(gen, rawElapsed);
    if (serverElapsed === null) return;
    elapsedBeforeTick = clampElapsed(serverElapsed);
  } else {
    elapsedBeforeTick = clampElapsed(rawElapsed);
  }

  // Sync display to actual remaining time (even when paused)
  const remaining = props.duration - elapsedBeforeTick;
  if (remaining <= 0) {
    timerCount.value = 0;
    roundExpired = true;
    onExpired();
    return;
  }
  timerCount.value = Math.ceil(remaining);

  if (props.pauseTimer) return;

  tickOrigin = Date.now();
  startTicking();
}

// --- Watchers ---
watch(
  () => props.startTimestamp,
  () => {
    roundExpired = false;
    stopTicking();
    timerCount.value = props.duration;
    if (!props.pauseTimer) {
      shouldEmitOnExpiry = true;
    }
    syncTimer();
  }
);

watch(
  () => props.duration,
  (newDuration, oldDuration) => {
    if (oldDuration === 0 && newDuration > 0) {
      roundExpired = false;
      timerCount.value = newDuration;
      if (props.votingStarted && props.startTimestamp) {
        if (!props.pauseTimer) {
          shouldEmitOnExpiry = true;
        }
        syncTimer();
      }
    }
  }
);

watch(
  () => props.pauseTimer,
  (paused) => {
    if (paused) {
      if (tick()) onExpired();
      stopTicking();
      shouldEmitOnExpiry = false;
      if (!props.startTimestamp) timerCount.value = 0;
    } else if (props.startTimestamp && props.duration > 0 && !roundExpired) {
      shouldEmitOnExpiry = true;
      syncTimer();
    }
  }
);

// --- Initialization ---
timerCount.value = props.pauseTimer && !props.startTimestamp ? 0 : props.duration;
if (props.votingStarted && props.startTimestamp) {
  if (!props.pauseTimer) {
    shouldEmitOnExpiry = true;
  }
  syncTimer();
}

onBeforeUnmount(() => {
  unmounted = true;
  stopTicking();
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
