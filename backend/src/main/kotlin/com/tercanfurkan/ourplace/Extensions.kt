package com.tercanfurkan.ourplace

import com.tercanfurkan.ourplace.repository.ContentType
import com.tercanfurkan.ourplace.repository.Message
import com.tercanfurkan.ourplace.service.MessageViewModel
import com.tercanfurkan.ourplace.service.UserViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.net.URL

fun MessageViewModel.asModel(contentType: ContentType = ContentType.PLAIN): Message = Message(
    content,
    contentType,
    sent,
    user.name,
    user.avatarImageLink.toString(),
    id
)

fun Message.asViewModel(): MessageViewModel = MessageViewModel(
    content,
    UserViewModel(username, URL(userAvatarImageLink)),
    sent,
    id
)

fun Flow<Message>.mapToViewModel(): Flow<MessageViewModel> = map { it.asViewModel() }