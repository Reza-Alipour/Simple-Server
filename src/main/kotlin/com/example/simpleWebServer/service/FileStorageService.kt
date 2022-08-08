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
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

@Service
class FileStorageService {

//    @Autowired
//    private lateinit var properties: StorageProperties


    companion object {
        const val THRESHOLD: Long = 1024 * 1024 * 50
        const val LOCATION = "upload-dir"

    }

    @Autowired
    private lateinit var videoRepository: VideoRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    fun store(file: MultipartFile, user: User): ResponseEntity<String> {
        if (file.size > THRESHOLD) {
            return ResponseEntity.badRequest().body("File is too large")
        }
        val name = file.originalFilename?.let { StringUtils.cleanPath(it) } ?: return ResponseEntity.badRequest()
            .body("File name is empty")
        val directory = File("$LOCATION/${user.username}")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val loc = Paths.get("$LOCATION/${user.username}").resolve(name)
        val newVideo = Video(name, "file type", user, loc.toString())

        if (file.isEmpty) {
            return ResponseEntity.badRequest().body("File is empty")
        } else if (name.contains("..")) {
            return ResponseEntity.badRequest().body("The file shouldn't have relative path in its name.")
        }
        if (Files.exists(loc)) return ResponseEntity.badRequest().body("File with this name already exists")

        Files.copy(
            file.inputStream, loc, StandardCopyOption.REPLACE_EXISTING
        )

        newVideo.userIthVideo = user.videosCount++
        userRepository.save(user)
        videoRepository.save(newVideo)
        return ResponseEntity.ok("File uploaded successfully")
    }
}
