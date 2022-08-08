package com.example.simpleWebServer.repository

import com.example.simpleWebServer.entity.RoleType
import com.example.simpleWebServer.entity.Ticket
import com.example.simpleWebServer.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface TicketRepository : JpaRepository<Ticket, Long> {
    fun findAllByUser(user: User): List<Ticket>

    fun findAllByUserRole(user_role: RoleType): List<Ticket>

    fun findAllByUserOrUserRole(user: User, user_role: RoleType): List<Ticket>
}
