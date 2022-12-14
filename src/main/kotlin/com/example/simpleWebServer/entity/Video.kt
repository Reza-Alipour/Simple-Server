package com.example.simpleWebServer.entity

import com.example.simpleWebServer.dto.VideoDTO
import javax.persistence.*

@Entity
class Video(
    var name: String,
    var type: String,
    @ManyToOne(fetch = FetchType.LAZY) var user: User,
    var path: String,
    @Id @GeneratedValue var id: Long? = null,
) : ToDTO {
    constructor() : this("", "", User(), "")

    @ManyToMany(fetch = FetchType.LAZY)
    var likes: MutableSet<User> = mutableSetOf()

    @ManyToMany(fetch = FetchType.LAZY)
    var dislikes: MutableSet<User> = mutableSetOf()

    @ElementCollection
    var tags: MutableList<String> = mutableListOf()

    var userIthVideo: Int = 0
    var banned: Boolean = false

    @Column(name = "video_views")
    var views: Int = 0


    override fun toDTO(): VideoDTO {
        return VideoDTO(
            this.name,
            this.id!!,
            dislikesNum = this.dislikes.size,
            likesNum = this.likes.size,
            tags = this.tags,
            path = this.path
        )
    }
}