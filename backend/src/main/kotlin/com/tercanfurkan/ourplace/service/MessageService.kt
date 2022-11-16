package com.tercanfurkan.ourplace.service

import kotlinx.coroutines.flow.Flow

interface MessageService {

    fun recent(): Flow<MessageViewModel>

    fun stream(): Flow<MessageViewModel>

    suspend fun emitAndSaveAll(messages: Flow<MessageViewModel>)
}