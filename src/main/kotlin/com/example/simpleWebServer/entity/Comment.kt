package com.example.simpleWebServer.entity

import com.example.simpleWebServer.dto.CommentDTO
import com.example.simpleWebServer.dto.DTO
import javax.persistence.*


@Entity
class Comment(
    var text: String,
    @ManyToOne(fetch = FetchType.LAZY) var user: User,
    @ManyToOne(fetch = FetchType.LAZY) var video: Video,
    @Id @GeneratedValue var id: Long? = null
) : ToDTO {

    constructor() : this("", User(), Video())

    override fun toDTO(): CommentDTO {
        return CommentDTO(comment = this.text, username = this.user.username)
    }
}