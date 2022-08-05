package com.example.simpleWebServer.controller

import com.example.simpleWebServer.service.UserService
import com.example.simpleWebServer.utils.CommonUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ManagerController {

    @Autowired
    private lateinit var commonUtils: CommonUtils

    @Autowired
    private lateinit var userService: UserService

    @PostMapping("approveAdmin")
    fun approveAdmin(@RequestParam userId: Long, @CookieValue("jwt") jwt: String): ResponseEntity<String> {
        if (!commonUtils.isManager(jwt)) return ResponseEntity.status(401)
            .body("You are not authorized to perform this action")

        return userService.approveAdmin(userId)
    }

}