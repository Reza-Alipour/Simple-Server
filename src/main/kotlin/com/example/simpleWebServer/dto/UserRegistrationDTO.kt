package com.example.simpleWebServer.dto

class UserRegistrationDTO(
    var username: String, var password: String, var role: RoleTypeDTO
) : DTO

enum class RoleTypeDTO {
    ADMIN, USER
}