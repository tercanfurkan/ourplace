package com.tercanfurkan.ourplace.service

import java.net.URL
import java.time.Instant

data class MessageViewModel(val content: String, val user: UserViewModel, val sentAt: Instant, val id: String? = null)

data class UserViewModel(val name: String, val avatarURL: URL)