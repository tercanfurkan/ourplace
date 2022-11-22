package com.tercanfurkan.ourplace

import com.tercanfurkan.ourplace.repository.Message
import com.tercanfurkan.ourplace.service.MessageViewModel
import java.time.temporal.ChronoUnit.MILLIS

fun MessageViewModel.copyForTesting() = copy(id = null)

fun Message.copyForTesting() = copy(id = null)