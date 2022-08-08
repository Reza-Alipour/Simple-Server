package com.example.simpleWebServer.service

import com.example.simpleWebServer.dto.RoleTypeDTO
import com.example.simpleWebServer.dto.UserDTO
import com.example.simpleWebServer.dto.UserRegistrationDTO
import com.example.simpleWebServer.entity.RoleType
import com.example.simpleWebServer.entity.User
import com.example.simpleWebServer.entity.Video
import com.example.simpleWebServer.repository.UserRepository
import com.example.simpleWebServer.repository.VideoRepository
import com.example.simpleWebServer.utils.JwtUtils
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val jwtService: JwtUtils,
    private val videoRepository: VideoRepository
) {
    fun createUser(registrationDto: UserRegistrationDTO): User {
        return createUser(
            registrationDto.username,
            registrationDto.password,
            if (registrationDto.role == RoleTypeDTO.USER) RoleType.USER else RoleType.ADMIN_PENDING
        )
    }

    private fun createUser(username: String, password: String, role: RoleType): User {
        val user = User()
        user.username = username
        user.passwordHash = password
        user.role = role
        return userRepository.save(user)
    }

    fun login(username: String, password: String): Pair<String, String>? {
        val user = userRepository.findByUsername(username) ?: return null
        if (!user.comparePasswrd(password)) return null
        return Pair(user.id?.let { jwtService.generateToken(it) } ?: return null, user.role.name)
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

    fun getUsers(role: RoleType): List<UserDTO>? {
        return userRepository.findAllByRole(if (role == RoleType.ADMIN) RoleType.USER else RoleType.ADMIN_PENDING)
            .map { it.toDTO() }
    }

    //    @Transactional
    fun getVideos(pageNo: Int, pageSize: Int): List<Video> {
        val paging: Pageable = PageRequest.of(pageNo, pageSize, Sort.by("views"))
        return videoRepository.findAllByBanned(false, paging).toList()
    }

    fun initAdmin() {
        if (!userRepository.existsByRole(RoleType.MANAGER)) {
            createUser("manager", "supreme_manager#2022", RoleType.MANAGER)
        }
    }
}
