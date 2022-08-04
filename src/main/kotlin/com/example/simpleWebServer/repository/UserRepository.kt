package com.example.simpleWebServer.repository

import com.example.simpleWebServer.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.CrudRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): User?
}