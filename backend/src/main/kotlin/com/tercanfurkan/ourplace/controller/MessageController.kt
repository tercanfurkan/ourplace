package com.tercanfurkan.ourplace.controller

import com.tercanfurkan.ourplace.service.MessageService
import com.tercanfurkan.ourplace.service.MessageViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.onStart
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Controller

@Controller
@MessageMapping("api.v1.messages")
class MessageController(val service: MessageService) {

    @MessageMapping("stream")
    suspend fun receive(@Payload messages: Flow<MessageViewModel>) =
        service.emitAndSaveAll(messages)

    @MessageMapping("stream")
    fun send(): Flow<MessageViewModel> = service
        .stream()
        .onStart {
            emitAll(service.recent())
        }

}
