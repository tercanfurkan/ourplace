<template>
    <div class="mt-10 sm:mx-auto sm:w-full sm:max-w-300">
        <div class="flex justify-evenly mb-4">
            <h1 class="font-bold text-2xl">Users: 2</h1>
            <h1 class="font-bold text-2xl">Place: Vehka Center</h1>
        </div>

        <div class="bg-white py-10 px-6 shadow rounded-lg sm:px-10 h-100">
            <div
                ref="chatBox"
                class="flex flex-col items-stretch overflow-auto h-full scrollbar-thumb-blue scrollbar-thumb-rounded scrollbar-track-blue-lighter scrollbar-w-2 scrolling-touch"
            >
                <div class="chat-message mt-2" v-for="message in messages" :key="message">
                    <div
                        :class="message.user.name == currentUser.name ? 'flex items-end justify-end' : 'flex items-end justify-start'"
                    >
                        <div
                            :class="message.user.name == currentUser.name ? 'flex flex-col space-y-2 text-xs max-w-xs mx-2 order-1 items-end' : ' flex flex-col space-y-2 text-xs max-w-xs mx-2 order-2 items-start'"
                        >
                            <div>
                                <span
                                    class="px-4 py-2 rounded-lg inline-block rounded-bl-none bg-gray-300 text-gray-600"
                                >{{ message.content }}</span>
                            </div>
                            <small>{{ message.user.name }}  | {{ formatDate(message.sent) }}</small>
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
                    type="text"
                    :class="'w-full my-8 py-2 px-4 border border-transparent rounded-md shadow-sm focus:outline-blue-300 focus:shadow-outline '"
                    placeholder="Type your message here..."
                    v-model="newMessage"
                />

                <button
                    type="submit"
                    class="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
                >Send Message</button>
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
}  from "rsocket-core";
import rSocketWebSocketClient from "rsocket-websocket-client";
import { Flowable } from "rsocket-flowable";
import axios from "axios"
import moment from "moment"


var rsocketFlowableSource
const endpoint = "api.v1.messages.stream"

export default {

  data: function() {
    return {
      messages: [],
      newMessage: '',
      currentUser: {}
    }
  },

  created() {
    this.openSurveyResponseStream()
  },

  mounted() {
    axios.get("https://randomuser.me/api/").then((response) => {
      console.log("get user data: ", response)
      this.currentUser = {
        name: response.data.results[0].name.first + " " + response.data.results[0].name.last,
        avatarImageLink: response.data.results[0].picture.thumbnail
      }
    })
  },

  methods: {
    formatDate(date) {
    console.log("format date:", date)
    return moment(date).format("DD-MM-YY, HH:mm")
    },

    sendMessage() {
      var content = this.newMessage
      var data = {content: content , user: this.currentUser, sent: new Date().toISOString()}
      this.newMessage = ''
      if (content) {
          rsocketFlowableSource.onNext({
              data: Buffer.from(JSON.stringify(data)),
              metadata: encodeAndAddWellKnownMetadata(
                  Buffer.alloc(0),
                  MESSAGE_RSOCKET_ROUTING,
                  Buffer.from(String.fromCharCode(endpoint.length) + endpoint)
              )
          });
      }
    },

    // RSOCKET
    openSurveyResponseStream() {
      const client = new RSocketClient({
        transport: new rSocketWebSocketClient(
            {
                url: 'ws://localhost:8080/rsocket',
            },
            BufferEncoders,
        ),
        setup: {
            dataMimeType: 'application/json',
            metadataMimeType: MESSAGE_RSOCKET_COMPOSITE_METADATA.string,
            keepAlive: 5000,
            lifetime: 60000,
        },
    });

      client.connect()
        .then(rsocket => {
            var endpoint = "api.v1.messages.stream";

            rsocket.requestChannel(new Flowable(source => {
                console.log("channel")
                source.onSubscribe({
                    cancel: () => {},
                    request: (n) => {
                      console.log("source.OnSubscribe request:", n)
                    }
                })
                rsocketFlowableSource = source
            }))
                .subscribe({
                    onSubscribe: (s) => {
                        s.request(1000)
                    }
                });

            rsocket.requestStream({
                metadata: encodeAndAddWellKnownMetadata(
                    Buffer.alloc(0),
                    MESSAGE_RSOCKET_ROUTING,
                    Buffer.from(String.fromCharCode(endpoint.length) + endpoint)
                )
            })
                .subscribe({
                    onSubscribe: (s) => {
                        s.request(1000)
                    },
                    onNext: (e) => {
                        var v = JSON.parse(e.data);
                        console.log("onNextMessage: ", v)
                        this.messages.push(v)
                        setTimeout(() => {
                            var container = this.$refs.chatBox
                            container.scrollTop = container.scrollHeight  
                        }, 100) 
                    }
                });
        });
    },
  }
}
</script>





