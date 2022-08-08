package com.example.simpleWebServer.dto

class UserDTO(
    val id: Long, val username: String? = null, val password: String? = null, strike: Boolean? = null,
) : DTO