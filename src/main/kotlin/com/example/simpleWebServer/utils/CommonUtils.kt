package com.example.simpleWebServer.utils

import com.example.simpleWebServer.entity.RoleType
import com.example.simpleWebServer.entity.Ticket
import com.example.simpleWebServer.entity.User
import com.example.simpleWebServer.entity.Video
import com.example.simpleWebServer.repository.CommentRepository
import com.example.simpleWebServer.repository.TicketRepository
import com.example.simpleWebServer.repository.VideoRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CommonUtils {
    @Autowired
    private lateinit var jwtUtils: JwtUtils

    @Autowired
    private lateinit var videoRepository: VideoRepository

    @Autowired
    private lateinit var ticketRepository: TicketRepository

    @Autowired
    private lateinit var commentRepository: CommentRepository


    fun isAdmin(jwt: String): Boolean {
        return getUserWithAdminRole(jwt) != null
    }

    fun getUser(jwt: String): User? {
        return jwtUtils.getUser(jwt)
    }

    fun getUserWithAdminRole(jwt: String): User? {
        return getUser(jwt)?.let {
            if (it.role == RoleType.ADMIN) it else null
        }
    }

    fun getUserWithAdminOrManagerRole(jwt: String): User? {
        return getUser(jwt)?.let {
            if (it.role == RoleType.ADMIN || it.role == RoleType.MANAGER) it else null
        }
    }

    fun getUserWithUserRole(jwt: String): User? {
        return getUser(jwt)?.let {
            if (it.role == RoleType.USER) it else null
        }
    }

    fun getVideoById(videoId: Long): Video? {
        return videoRepository.findById(videoId).orElse(null)
    }

    fun getUserWithManagerRole(jwt: String): User? {
        return getUser(jwt)?.let {
            if (it.role == RoleType.MANAGER) it else null
        }
    }

    fun isManager(jwt: String): Boolean {
        return getUserWithManagerRole(jwt) != null
    }

    fun getTicket(id: Long): Ticket? {
        return ticketRepository.findById(id).orElse(null)
    }

    fun getCommentsByVideo(videoId: Long): List<String> {
        return commentRepository.funcP(videoId)
    }

    fun getTickets(jwt: String): List<Ticket>? {
        val user = jwtUtils.getUser(jwt) ?: return null
        return when (user.role) {
            RoleType.USER -> ticketRepository.findAllByUser(user)
            RoleType.ADMIN -> ticketRepository.findAllByUserOrUserRole(user, RoleType.USER)
            RoleType.MANAGER -> ticketRepository.findAllByUserRole(RoleType.ADMIN)
            else -> null
        }
    }
}