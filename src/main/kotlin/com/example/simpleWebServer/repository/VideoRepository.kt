package com.example.simpleWebServer.repository

import com.example.simpleWebServer.entity.User
import com.example.simpleWebServer.entity.Video
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.PagingAndSortingRepository

interface VideoRepository : PagingAndSortingRepository<Video, Long> {
    fun existsVideoByUserAndUserIthVideoAndBanned(user: User, userWithVideo: Int, banned: Boolean): Boolean
}