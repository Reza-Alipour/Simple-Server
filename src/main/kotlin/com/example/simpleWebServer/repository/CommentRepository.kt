package com.example.simpleWebServer.repository

import com.example.simpleWebServer.entity.Comment
import org.springframework.data.jpa.repository.JpaRepository

interface CommentRepository : JpaRepository<Comment, Long>