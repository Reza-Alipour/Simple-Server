package com.example.simpleWebServer.dto

class VideoDTO(
    val name: String,
    val id: Long,
    val likesNum: Int? = null,
    val dislikesNum: Int? = null,
    val comments: List<CommentDTO>? = null
):DTO