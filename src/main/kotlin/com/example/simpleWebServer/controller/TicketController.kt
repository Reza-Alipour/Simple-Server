package com.example.simpleWebServer.controller

import com.example.simpleWebServer.dto.TicketDTO
import com.example.simpleWebServer.entity.RoleType
import com.example.simpleWebServer.entity.Ticket
import com.example.simpleWebServer.entity.TicketState
import com.example.simpleWebServer.entity.User
import com.example.simpleWebServer.repository.TicketRepository
import com.example.simpleWebServer.utils.CommonUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/ticket")
class TicketController {

    @Autowired
    private lateinit var commonUtils: CommonUtils

    @Autowired
    private lateinit var ticketRepository: TicketRepository


    @PostMapping("/create")
    fun createTicket(@RequestBody ticket: TicketDTO, @CookieValue("jwt") jwt: String): ResponseEntity<String> {
        val user = commonUtils.getUser(jwt) ?: return ResponseEntity.status(401).body("Unauthorized")
        if (user.role == RoleType.ADMIN_PENDING || user.role == RoleType.MANAGER) {
            return ResponseEntity.status(403).body("Forbidden")
        }
        val newTicket = Ticket(user)
        newTicket.messages.add(Pair(user, ticket.message))
        ticketRepository.save(newTicket)
        return ResponseEntity.ok("Ticket created")
    }

    @PostMapping("/addMessage")
    fun addMessageToTicket(@RequestBody ticketDto: TicketDTO, @CookieValue("jwt") jwt: String): ResponseEntity<String> {
        val user = commonUtils.getUser(jwt) ?: return ResponseEntity.status(401).body("Unauthorized")
        val ticket: Ticket = ticketDto.id?.let {
            commonUtils.getTicket(it)
        } ?: return ResponseEntity.status(400).body("Ticket not found")

        if (ticket.state == TicketState.CLOSED) {
            return ResponseEntity.status(403).body("Ticket closed")
        }

        return if (ticket.user == user || (ticket.user.role == RoleType.USER && user.role == RoleType.ADMIN) || (ticket.user.role == RoleType.ADMIN && user.role == RoleType.MANAGER)) {
            addMessageToTicket(ticket, user, ticketDto.message)
            ResponseEntity.ok("Message added")
        } else ResponseEntity.status(403).body("Forbidden")
    }

    @PostMapping("changeState")
    fun changeState(@RequestBody ticketDto: TicketDTO, @CookieValue("jwt") jwt: String): ResponseEntity<String> {
        val user = commonUtils.getUser(jwt) ?: return ResponseEntity.status(401).body("Unauthorized")
        val ticket: Ticket = ticketDto.id?.let {
            commonUtils.getTicket(it)
        } ?: return ResponseEntity.status(400).body("Ticket not found")

        val newState = ticketDto.state ?: return ResponseEntity.status(400).body("Please provide new state")
        if (newState == TicketState.CLOSED) return ResponseEntity.status(403).body("Ticket closed")

        return if ((ticket.user.role == RoleType.USER && user.role == RoleType.ADMIN) || (ticket.user.role == RoleType.ADMIN && user.role == RoleType.MANAGER)) {
            ticket.state = newState
            ticketRepository.save(ticket)
            ResponseEntity.ok("State changed")
        } else ResponseEntity.status(403).body("Forbidden")
    }


    private fun addMessageToTicket(ticket: Ticket, user: User, message: String) {
        ticket.messages.add(Pair(user, message))
        ticketRepository.save(ticket)
    }
}