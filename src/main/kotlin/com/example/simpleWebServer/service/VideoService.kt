package com.example.simpleWebServer.service

import com.example.simpleWebServer.entity.Comment
import com.example.simpleWebServer.entity.User
import com.example.simpleWebServer.repository.CommentRepository
import com.example.simpleWebServer.repository.UserRepository
import com.example.simpleWebServer.repository.VideoRepository
import com.example.simpleWebServer.utils.CommonUtils
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class VideoService(
    private val videoRepository: VideoRepository,
    private val commentRepository: CommentRepository,
    private val userRepository: UserRepository,
    private val commonUtils: CommonUtils
) {
    fun like(videoId: Long, user: User): ResponseEntity<String> {

        val video = commonUtils.getVideoById(videoId)?: return ResponseEntity.notFound().build()
        video.likes.add(user)
        video.dislikes.remove(user)
        videoRepository.save(video)
        return ResponseEntity.ok("Video liked")
    }

    fun dislike(videoId: Long, user: User): ResponseEntity<String> {
        val video = commonUtils.getVideoById(videoId)?: return ResponseEntity.notFound().build()
        video.likes.remove(user)
        video.dislikes.add(user)
        videoRepository.save(video)
        return ResponseEntity.ok("Video disliked")
    }

    fun comment(txt: String, user: User, videoId: Long): ResponseEntity<String> {
        val video = commonUtils.getVideoById(videoId)?: return ResponseEntity.notFound().build()
        commentRepository.save(Comment(txt, user, video))
        return ResponseEntity.ok("Comment added")
    }


    fun addTag(id:Long, tag:String): ResponseEntity<String> {
        val video = commonUtils.getVideoById(id)?: return ResponseEntity.notFound().build()
        video.tags.add(tag)
        videoRepository.save(video)
        return ResponseEntity.ok("Tag added")
    }

    fun banVideo(videoId: Long): ResponseEntity<String> {
        val video = commonUtils.getVideoById(videoId)?: return ResponseEntity.notFound().build()
        video.banned = true
        if (
            videoRepository.existsVideoByUserAndUserIthVideoAndBanned(video.user, video.userIthVideo - 1, true)||
                    videoRepository.existsVideoByUserAndUserIthVideoAndBanned(video.user, video.userIthVideo + 1, true)
        ) {
            video.user.strike = true
            userRepository.save(video.user)
        }
        videoRepository.save(video)
        return ResponseEntity.ok("Video banned")
    }

}