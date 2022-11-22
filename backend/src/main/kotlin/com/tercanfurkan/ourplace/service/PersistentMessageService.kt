package com.tercanfurkan.ourplace.service

import com.tercanfurkan.ourplace.asModel
import com.tercanfurkan.ourplace.mapToViewModel
import com.tercanfurkan.ourplace.repository.MessageRepository
import kotlinx.coroutines.flow.*
import org.springframework.stereotype.Service

@Service
class PersistentMessageService(val messageRepository: MessageRepository) : MessageService {

    val sender: MutableSharedFlow<MessageViewModel> = MutableSharedFlow()

    override fun recent(): Flow<MessageViewModel> =
        messageRepository.findRecent()
            .mapToViewModel()

    override fun stream(): Flow<MessageViewModel> = sender

    override suspend fun emitAndSaveAll(messages: Flow<MessageViewModel>) =
        messages
            .onEach { sender.emit(it) }
            .map { it.asModel() }
            .let { messageRepository.saveAll(it) }
            .collect()
}