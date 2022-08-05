package com.example.simpleWebServer.service

import com.example.simpleWebServer.entity.User
import com.example.simpleWebServer.entity.Video
import com.example.simpleWebServer.repository.UserRepository
import com.example.simpleWebServer.repository.VideoRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile

@Service
class FileStorageService(val THRESHOLD: Long = 1024 * 1024 * 50) {
    @Autowired
    private lateinit var videoRepository: VideoRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    fun store(file: MultipartFile, user: User): ResponseEntity<String> {
        if (file.size > this.THRESHOLD) {
            return ResponseEntity.badRequest().body("File is too large")
        }
        val name = file.originalFilename?.let { StringUtils.cleanPath(it) } ?: return ResponseEntity.badRequest().body("File name is empty")
        val newVideo = file.contentType?.let { Video(name, it, file.bytes, user) } ?: return ResponseEntity.badRequest().body("File type is empty")
        newVideo.userIthVideo = user.videosCount++
        userRepository.save(user)
        videoRepository.save(newVideo)
        return ResponseEntity.ok("File uploaded successfully")
    }

}