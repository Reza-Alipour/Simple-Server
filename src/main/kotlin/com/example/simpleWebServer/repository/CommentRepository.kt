package com.example.simpleWebServer.repository

import com.example.simpleWebServer.entity.Comment
import com.example.simpleWebServer.entity.Video
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface CommentRepository : CrudRepository<Comment, Long> {
    fun findByVideo(video: Video): List<Comment>

    @Query("select c.text from Comment c where c.video.id = :video_id")
    fun funcP(@Param("video_id") videoId: Long): List<String>
}