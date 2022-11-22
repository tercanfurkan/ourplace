<template>
  <div class="mt-10 sm:mx-auto sm:w-full sm:max-w-300">
    <div class="flex justify-evenly mb-4">
      <h1 class="font-bold text-2xl"></h1>
      <h1 class="font-bold text-2xl"></h1>
    </div>

    <div class="bg-white py-10 px-6 shadow rounded-lg sm:px-10 h-100">
      <div
        ref="chatBox"
        class="flex flex-col items-stretch overflow-auto h-full scrollbar-thumb-blue scrollbar-thumb-rounded scrollbar-track-blue-lighter scrollbar-w-2 scrolling-touch"
      >
        <div
          v-for="message in messages"
          :key="message"
          class="chat-message mt-2"
        >
          <div
            :class="
              message.user.name == currentUser.name
                ? 'flex items-end justify-end'
                : 'flex items-end justify-start'
            "
          >
            <div
              :class="
                message.user.name == currentUser.name
                  ? 'flex flex-col space-y-2 text-xs max-w-xs mx-2 order-1 items-end'
                  : ' flex flex-col space-y-2 text-xs max-w-xs mx-2 order-2 items-start'
              "
            >
              <div>
                <span
                  class="px-4 py-2 rounded-lg inline-block rounded-bl-none bg-gray-300 text-gray-600"
                  >{{ message.content }}</span
                >
              </div>
              <small
                >{{ message.user.name }} | {{ formatDate(message.sent) }}</small
              >
            </div>
            <img
              :src="message.user.avatarImageLink"
              alt="My profile"
              class="w-6 h-6 rounded-full order-1"
            />
          </div>
        </div>
      </div>
    </div>

    <div class="mt-10">
      <form @submit.prevent="sendMessage">
        <input
          v-model="newMessage"
          type="text"
          :class="'w-full my-8 py-2 px-4 border border-transparent rounded-md shadow-sm focus:outline-blue-300 focus:shadow-outline '"
          placeholder="Type your message here..."
        />

        <button
          type="submit"
          class="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
        >
          Send Message
        </button>
      </form>
    </div>
  </div>
</template>

<script>
import {
  encodeAndAddWellKnownMetadata,
  MESSAGE_RSOCKET_ROUTING,
  RSocketClient,
  BufferEncoders,
  MESSAGE_RSOCKET_COMPOSITE_METADATA,
} from "rsocket-core";
import rSocketWebSocketClient from "rsocket-websocket-client";
import { Flowable } from "rsocket-flowable";
import axios from "axios";
import moment from "moment";

var rsocketFlowableSource;
const endpoint = "api.v1.messages.stream";

export default {
  data: function () {
    return {
      messages: [],
      newMessage: "",
      currentUser: {},
    };
  },

  created() {
    this.openMessagingStream();
  },

  mounted() {
    axios.get("https://randomuser.me/api/").then((response) => {
      this.currentUser = {
        name:
          response.data.results[0].name.first +
          " " +
          response.data.results[0].name.last,
        avatarImageLink: response.data.results[0].picture.thumbnail,
      };
    });
  },

  methods: {
    formatDate(date) {
      return moment(date).format("DD-MM-YY, HH:mm");
    },

    sendMessage() {
      var content = this.newMessage;
      var data = {
        content: content,
        user: this.currentUser,
        sent: new Date().toISOString(),
      };
      this.newMessage = "";
      if (content) {
        rsocketFlowableSource.onNext({
          data: Buffer.from(JSON.stringify(data)),
          metadata: encodeAndAddWellKnownMetadata(
            Buffer.alloc(0),
            MESSAGE_RSOCKET_ROUTING,
            Buffer.from(String.fromCharCode(endpoint.length) + endpoint)
          ),
        });
      }
    },

    // RSOCKET
    async openMessagingStream() {
      const client = new RSocketClient({
        transport: new rSocketWebSocketClient(
          {
            url: "ws://localhost:8080/rsocket",
          },
          BufferEncoders
        ),
        setup: {
          dataMimeType: "application/json",
          metadataMimeType: MESSAGE_RSOCKET_COMPOSITE_METADATA.string,
          keepAlive: 5000,
          lifetime: 60000,
        },
      });

      const rsocket = await client.connect();
      const maxNumberOfPayloadsToHandle = 1000;
      rsocket
        .requestChannel(
          new Flowable((source) => {
            source.onSubscribe();
            rsocketFlowableSource = source;
          })
        )
        .subscribe({
          onSubscribe: (subscription) => {
            subscription.request(maxNumberOfPayloadsToHandle);
          },
        });

      rsocket
        .requestStream({
          metadata: encodeAndAddWellKnownMetadata(
            Buffer.alloc(0),
            MESSAGE_RSOCKET_ROUTING,
            Buffer.from(String.fromCharCode(endpoint.length) + endpoint)
          ),
        })
        .subscribe({
          onSubscribe: (subscription) => {
            subscription.request(maxNumberOfPayloadsToHandle);
          },
          onNext: (event) => {
            var v = JSON.parse(event.data);
            this.messages.push(v);
            setTimeout(() => {
              var container = this.$refs.chatBox;
              container.scrollTop = container.scrollHeight;
            }, 100);
          },
        });
    },
  },
};
</script>
