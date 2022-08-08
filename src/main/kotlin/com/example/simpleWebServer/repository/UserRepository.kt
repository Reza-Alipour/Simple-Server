package com.example.simpleWebServer.repository

import com.example.simpleWebServer.entity.RoleType
import com.example.simpleWebServer.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): User?
    fun findAllByRole(role: RoleType): List<User>

    fun existsByRole(role: RoleType): Boolean

}