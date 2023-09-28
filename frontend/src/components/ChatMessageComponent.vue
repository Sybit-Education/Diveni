<template>
  <div>
    <div>
      <b-button v-b-modal.modal-scrollable class="chatButton">
        <BIconMessenger></BIconMessenger>
      </b-button>
    </div>
    <b-modal
      size="lg"
      hide-footer
      id="modal-scrollable"
      scrollable
      :title="$t('page.session.chat.title')"
      header-class="chatRoom-Header"
    >
      <div class="messageDisplay">
        <div
          v-for="message of chatMessager"
          :key="message.message"
          class="chatMessage"
          :class="{ 'message-out': message.author === id, 'message-in': message.author !== id }"
        >
          {{ message.message }}
        </div>
      </div>
      <div id="typingArea">
        <b-form-input
          v-model="message"
          id="chatTextArea"
          :placeholder="$t('page.session.chat.placeholder')"
        />
        <div id="sendButtonDiv">
          <b-button
            id="sendButton"
            @click="sendMessage()"
            :disabled="message.length === 0"
          >
            <BIconCursor/>
          </b-button>
        </div>
      </div>
    </b-modal>
  </div>
</template>
<script lang="ts">
import Vue from "vue";
import Constants from "../constants";
import Chat from "@/model/Chat";

export default Vue.extend({
    name: "ChatMessage",
    components: {
    },
    props: {
      chatMessager: { type: Array<Chat>, required: true },
      id: { type: String, required: true },
      name: { type: String, required: false, default: "Admin"},
    },
    data: () => ({
        message: ""
    }),
    methods: {
      sendMessage() {
        if (this.name === "Admin") {
          const endPoint = Constants.webSocketAdminSendMessageRoute;
          this.$store.commit("sendViaBackendWS", { endPoint, data: this.message });
        } else {
          const endPoint = Constants.webSocketMemberSendMessageRoute;
          this.message = this.message + "\n - " + this.name;
          this.$store.commit("sendViaBackendWS", { endPoint, data: this.message });
        }
        this.message = "";
      }
    }
})
</script>


<style scoped>

/*Start of Styling the b-modal*/

/deep/ .modal-content {
  height: 40em;
  background-color: var(--background-color-primary) !important;
  color: var(--text-primary-color) !important;
}

/deep/ .close {
  color: var(--text-primary-color);
}

/deep/ .close:hover {
  color: var(--text-primary-color);
}

/* End of styling the b-modal*/

.chatButton {
  background-color: var(--textAreaColour) !important;
  color: var(--text-primary-color);
  border-radius: var(--buttonShape);
  border-color: black;
  bottom: 0;
  right: 0;
  position: absolute;
}

.chatButton:hover{
  background-color: var(--textAreaColourHovered);
  color: var(--text-primary-color);
}

.chatButton:focus{
  background-color: var(--textAreaColourHovered) !important;
  color: var(--text-primary-color)  !important;
}

.messageDisplay {
  min-height: 50%;
  max-height: 90%;
  overflow: scroll;
}

#typingArea {
  position: absolute;
  bottom: 1%;
  width: 95%;
  display: flex;
  flex-direction: row;
  border-bottom: transparent;
  border-left: transparent;
  border-right: transparent;
  border-top: var(--text-primary-color);
  border-style: solid;
  padding-top: .5em;
}

#sendButtonDiv {
  margin-left: 5%;
}

#sendButton {
  background:none;
  border:none;
  color: var(--text-primary-color);
  padding: 0;
  height: 110%;
  width: 110%;
}

#sendButton:focus {
  background-color: transparent !important;
  border-color: inherit;
  -webkit-box-shadow: none;
  box-shadow: none;
}

#chatTextArea {
  resize: none;
  background-color: var(--textAreaColour);
  color: var(--text-primary-color);
  width: 100%;
  overflow:scroll;
}

#chatTextArea::placeholder {
  color: var(--text-primary-color);
}

.chatMessage {
  width: 45%;
  border-radius: 10px;
  padding: .5em;
  margin-bottom: .5em;
  font-size: .8em;
  overflow: hidden;
}
.message-out {
  background: #407FFF;
  color: white;
  margin-left: 50%;
}
.message-in {
  background: #F1F0F0;
  color: black;
}
</style>
