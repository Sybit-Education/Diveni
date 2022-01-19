import Vue from "vue";

class NotificationService {
  public showToastificationHost(message) {
    if (message.type == "MEMBER_LEFT") {
      Vue.$toast.warning("Member left the session");
    } else if (message.type == "MEMBER_JOINED") {
      Vue.$toast.info("Member Joined the session");
    }
  }
  public async showToastificationMember(message) {
    if (message.type == "ADMIN_LEFT") {
      Vue.$toast.warning("Host left the session");
    }
  }
}

export default new NotificationService();
