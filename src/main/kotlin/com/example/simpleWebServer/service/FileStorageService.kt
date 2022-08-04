package com.example.simpleWebServer.service

import com.example.simpleWebServer.entity.User
import com.example.simpleWebServer.entity.Video
import com.example.simpleWebServer.repository.UserRepository
import com.example.simpleWebServer.repository.VideoRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile

@Service
class FileStorageService(val THRESHOLD: Long = 1024 * 1024 * 50) {
    @Autowired
    private lateinit var videoRepository: VideoRepository

    fun store(file: MultipartFile, user: User): Video? {
        if (file.size > this.THRESHOLD) {
            return null
        }
        val name = file.originalFilename?.let { StringUtils.cleanPath(it) } ?: return null
        val newVideo = file.contentType?.let { Video(name, it, file.bytes, user) } ?: return null
        newVideo.userIthVideo = user.videosCount++
        return videoRepository.save(newVideo)
    }

}