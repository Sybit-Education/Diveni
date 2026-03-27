<template>
  <div class="d-flex justify-content-end estimate-timer">
    <b-card-text :style="`color: ${textColor}`" class="timer">
      {{ formattedTime }}
    </b-card-text>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onBeforeUnmount } from "vue";

const COLOR_INACTIVE = "#808080";
const COLOR_PLENTY = "#229954";
const COLOR_WARNING = "#D4AC0D";
const COLOR_URGENT = "#CB4335";

const props = defineProps<{
  startTimestamp: string;
  duration: number;
  pauseTimer: boolean;
  votingStarted: boolean;
}>();

const emit = defineEmits<{ timerFinished: [] }>();

const timerCount = ref(0);
let intervalHandle = -1;
let localStartTime = 0;
let initialElapsed = 0;

const textColor = computed(() => {
  if (timerCount.value === 0) return COLOR_INACTIVE;
  if (timerCount.value > (props.duration / 3) * 2) return COLOR_PLENTY;
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

function startInterval() {
  stopInterval();
  if (props.duration === 0 || !props.startTimestamp) return;

  const serverStartMs = new Date(props.startTimestamp).getTime();
  if (Number.isNaN(serverStartMs)) return;

  // Clamp initial offset to >= 0 so a client clock behind the server
  // never produces a negative elapsed value
  const now = Date.now();
  initialElapsed = Math.max(0, (now - serverStartMs) / 1000);
  localStartTime = now;

  if (initialElapsed >= props.duration) {
    timerCount.value = 0;
    emit("timerFinished");
    return;
  }

  timerCount.value = Math.min(props.duration, Math.ceil(props.duration - initialElapsed));

  intervalHandle = window.setInterval(() => {
    const localElapsed = (Date.now() - localStartTime) / 1000;
    const totalElapsed = initialElapsed + localElapsed;
    const remaining = props.duration - totalElapsed;

    if (remaining <= 0) {
      timerCount.value = 0;
      emit("timerFinished");
      stopInterval();
    } else {
      timerCount.value = Math.min(props.duration, Math.ceil(remaining));
    }
  }, 100);
}

watch(
  () => props.startTimestamp,
  () => {
    stopInterval();
    timerCount.value = props.duration;
    startInterval();
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
    if (paused) stopInterval();
  }
);

timerCount.value = props.duration;
if (props.votingStarted && props.startTimestamp) {
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

.card-body {
  padding: 0.5rem;
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
