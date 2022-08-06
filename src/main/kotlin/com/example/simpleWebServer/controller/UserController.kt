package com.example.simpleWebServer.controller

import com.example.simpleWebServer.dto.CommentDTO
import com.example.simpleWebServer.service.VideoService
import com.example.simpleWebServer.utils.CommonUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("user")
class UserController {

    @Autowired
    private lateinit var videoService: VideoService

    @Autowired
    private lateinit var commonUtils: CommonUtils

    @PostMapping("like")
    fun likeVideo(@RequestParam videoId: Long, @CookieValue("jwt") jwt: String): ResponseEntity<String> {
        val user = commonUtils.getUserWithUserRole(jwt) ?: return ResponseEntity.status(401).body("Unauthorized")
        return videoService.like(videoId, user)
    }

    @PostMapping("dislike")
    fun dislikeVideo(@RequestParam videoId: Long, @CookieValue("jwt") jwt: String): ResponseEntity<String> {
        val user = commonUtils.getUserWithUserRole(jwt) ?: return ResponseEntity.status(401).body("Unauthorized")
        return videoService.dislike(videoId, user)
    }

    @PostMapping("comment")
    fun comment(@RequestBody commentDTO: CommentDTO, @CookieValue("jwt") jwt: String): ResponseEntity<String> {
        val user = commonUtils.getUserWithUserRole(jwt) ?: return ResponseEntity.status(401).body("Unauthorized")
        return commentDTO.videoId?.let { videoService.comment(commentDTO.comment, user, it) } ?: ResponseEntity.status(
            400
        ).body("Video id is required")
    }

    @GetMapping("comments")
    fun comments(@RequestParam videoId: Long): ResponseEntity<List<String>> {
        return videoService.getComments(videoId)
    }
}



