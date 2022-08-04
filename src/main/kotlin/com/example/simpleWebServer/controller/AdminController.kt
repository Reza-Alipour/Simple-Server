package com.example.simpleWebServer.controller

import com.example.simpleWebServer.dto.TagDTO
import com.example.simpleWebServer.service.UserService
import com.example.simpleWebServer.service.VideoService
import com.example.simpleWebServer.utils.CommonUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("admin")
class AdminController {
    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var videoService: VideoService

    @Autowired
    private lateinit var commonUtils: CommonUtils

    @PostMapping("addTag")
    fun addTag(@RequestParam tagDTO: TagDTO, @CookieValue("jwt") jwt: String): ResponseEntity<String> {
        return if (commonUtils.isAdmin(jwt)) {
            videoService.addTag(tagDTO.videoId, tagDTO.text)
        } else ResponseEntity.status(401).body("You are not admin")
    }

    @PostMapping("banVideo")
    fun banVideo(@RequestParam videoId: Long, @CookieValue("jwt") jwt: String): ResponseEntity<String> {
        if (!commonUtils.isAdmin(jwt)) {
            return ResponseEntity.status(401).body("You are not admin")
        }
        return videoService.banVideo(videoId)
    }

    @PostMapping("unStrike")
    fun unStrike(@RequestParam userId: Long, @CookieValue("jwt") jwt: String): ResponseEntity<String> {
        commonUtils.getUserWithAdminRole(jwt)?:return ResponseEntity.status(401).body("You are not admin")
        return userService.unStrike(userId)
    }
}