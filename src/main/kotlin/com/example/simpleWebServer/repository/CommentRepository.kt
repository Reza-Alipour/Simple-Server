package com.example.simpleWebServer.repository

import com.example.simpleWebServer.entity.Comment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.CrudRepository

interface CommentRepository : JpaRepository<Comment, Long> {
}