package com.tercanfurkan.ourplace.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class AppController() {

    @GetMapping("/")
    fun index(): String {
        TODO("Serve Vue.js app instead")
    }
}