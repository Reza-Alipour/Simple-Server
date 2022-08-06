package com.example.simpleWebServer.dto

class CommentDTO(
    val videoId: Long? = null, val comment: String, val username: String? = null
) : DTO