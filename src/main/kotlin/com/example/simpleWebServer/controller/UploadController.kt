package com.example.simpleWebServer.controller

import com.example.simpleWebServer.service.FileStorageService
import com.example.simpleWebServer.utils.CommonUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@CrossOrigin("http://localhost:8081")
@RestController
@RequestMapping("upload")
class UploadController {
    @Autowired
    private lateinit var fileStorageService: FileStorageService

    @Autowired
    private lateinit var commonUtils: CommonUtils

    @PostMapping("upload")
    fun uploadFile(@RequestParam("file") file: MultipartFile, @CookieValue("jwt") jwt: String): ResponseEntity<String> {
        val user = commonUtils.getUserWithUserRole(jwt) ?: return ResponseEntity.status(401).body("Unauthorized")
        if (user.strike) {
            return ResponseEntity.status(403).body("You are banned")
        }
        return try {
            fileStorageService.store(file, user)
            ResponseEntity.ok().body("File uploaded successfully!")
        } catch (e: Exception) {
            ResponseEntity.internalServerError().body("Could not upload file: " + e.message)
        }
    }
}