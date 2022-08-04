package com.example.simpleWebServer.controller

import com.example.simpleWebServer.dto.UserRegistrationDTO
import com.example.simpleWebServer.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/register")
class RegistrationController(private val userService: UserService) {
    @PostMapping
    fun registerUserAccount(@RequestBody registrationDto: UserRegistrationDTO): ResponseEntity<String> {
        userService.createUser(registrationDto)
        return ResponseEntity.ok("User registered successfully! Please login to continue.")
    }
}