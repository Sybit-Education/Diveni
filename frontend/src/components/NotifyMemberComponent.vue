<script lang="ts">
import Vue from "vue";
import NotificationService from "@/services/notification.service";

export default Vue.extend({
  name: "NotifyMemberComponent",

  computed: {
    notifications() {
      return this.$store.state.notifications;
    },
  },
  watch: {
    notifications(notifications) {
      this.showToast(notifications.at(-1));
    },
  },
  methods: {
    showToast(message) {
      return NotificationService.showToastificationMember(message);
    },
  },
});
</script>
