package com.example.simpleWebServer.dto

import com.example.simpleWebServer.entity.RoleType

class UserRegistrationDTO(
    var username: String,
    var password: String,
    var role: RoleTypeDTO
):DTO

enum class RoleTypeDTO {
    ADMIN,
    USER
}