<template>
  <form @submit.prevent="sendMessage">
    <input v-model="newMessage">
    <button>Send</button>
  </form>
  <ul>
    <li v-for="message in messages" :key="message.id">
      <message-component :content="message.content" :name="message.user.name" :avatar-image-link="message.user.avatarImageLink" :sent="message.sent"/>
    </li>
  </ul>
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

import MessageComponent from '@/MessageComponent.vue'


var rsocketFlowableSource
const endpoint = "api.v1.messages.stream"

export default {

  data: function() {
    return {
      rsocketFlowableSource: null,
      messages: [],
      newMessage: '',
      user: {
        name: "Leonardo Ibarra",
        avatarImageLink: "https://randomuser.me/api/portraits/men/24.jpg",
      }
    }
  },

  components: {
    MessageComponent: MessageComponent
  },

  created() {
    this.openSurveyResponseStream()
  },

  methods: {

    sendMessage() {
      var content = this.newMessage
      this.newMessage = ''
      if (content) {
          rsocketFlowableSource.onNext({
              data: Buffer.from(JSON.stringify({content: content , user: this.user, sent: new Date().toISOString()})),
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
                    }
                });
        });
    },
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.chats-row div {
    height: 50%;
    border: 1px solid #ddd;
    padding: 0px;
}

.media-object {
    max-width: 64px;
}

.current-chat {
    height: 100vh;
    border: 1px solid #ddd;
}

.current-chat-area {
    padding-top: 10px;
    overflow: auto;
    height: 92vh;
}

.chat-input {
    margin-top: 10px;
    margin-bottom: 10px;
    height: 8vh;
}

.chat-input textarea {
    width: 100%;
    border: none;
}
.chat-input button {
    position: absolute;
    right: 10px;
}
</style>
