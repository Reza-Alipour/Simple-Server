package com.example.simpleWebServer.controller

import com.example.simpleWebServer.dto.VideoDTO
import com.example.simpleWebServer.service.FileStorageService
import com.example.simpleWebServer.service.UserService
import com.example.simpleWebServer.utils.CommonUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.IOException

@CrossOrigin("http://localhost:8081")
@RestController
@RequestMapping("video")
class UploadController {
    @Autowired
    private lateinit var fileStorageService: FileStorageService

    @Autowired
    private lateinit var commonUtils: CommonUtils

    @Autowired
    private lateinit var userService: UserService


    @PostMapping("upload")
    fun uploadFile(@RequestParam("file") file: MultipartFile, @CookieValue("jwt") jwt: String): ResponseEntity<String> {
        val user = commonUtils.getUserWithUserRole(jwt) ?: return ResponseEntity.status(401).body("Unauthorized")
        if (user.strike) {
            return ResponseEntity.status(403).body("You are banned")
        }
        return try {
            return fileStorageService.store(file, user)
        } catch (e: IOException) {
            ResponseEntity.internalServerError().body("Some error :)")
        }
    }

    @GetMapping("getVideos")
    fun getVideos(
        @RequestParam pageNo: Int,
        @RequestParam pageSize: Int,
    ): ResponseEntity<List<VideoDTO>> {
        return ResponseEntity.ok(userService.getVideos(pageNo, pageSize).map { it.toDTO() })
    }
}