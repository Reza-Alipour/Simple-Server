package com.example.simpleWebServer.service

import com.example.simpleWebServer.dto.RoleTypeDTO
import com.example.simpleWebServer.dto.UserRegistrationDTO
import com.example.simpleWebServer.entity.RoleType
import com.example.simpleWebServer.entity.User
import com.example.simpleWebServer.repository.UserRepository
import com.example.simpleWebServer.utils.JwtUtils
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository, private val jwtService: JwtUtils
) {
    fun createUser(registrationDto: UserRegistrationDTO): User {
        val user = User()
        user.username = registrationDto.username
        user.passwordHash = registrationDto.password
        user.role = if (registrationDto.role == RoleTypeDTO.USER) RoleType.USER else RoleType.ADMIN_PENDING
        return userRepository.save(user)
    }

    fun login(username: String, password: String): String? {
        val user = userRepository.findByUsername(username) ?: return null
        if (!user.comparePasswrd(password)) return null
        return user.id?.let { jwtService.generateToken(it) }
    }

    fun unStrike(userId: Long): ResponseEntity<String> {
        val user = userRepository.findById(userId).orElse(null) ?: return ResponseEntity.notFound().build()
        return if (user.strike) {
            user.strike = false
            userRepository.save(user)
            ResponseEntity.ok("Done")
        } else ResponseEntity.status(400).body("User is not strike")
    }

    fun approveAdmin(userId: Long): ResponseEntity<String> {
        val admin = userRepository.findById(userId).orElse(null) ?: return ResponseEntity.notFound().build()
        if (admin.role != RoleType.ADMIN_PENDING) {
            return ResponseEntity.status(400).body("There is no such admin waiting for approval")
        }
        admin.role = RoleType.ADMIN
        userRepository.save(admin)
        return ResponseEntity.ok("Done")
    }
}
