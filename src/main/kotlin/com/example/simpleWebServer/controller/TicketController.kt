package com.example.simpleWebServer.controller

import com.example.simpleWebServer.dto.TicketDTO
import com.example.simpleWebServer.entity.*
import com.example.simpleWebServer.repository.MessagePairRepository
import com.example.simpleWebServer.repository.TicketRepository
import com.example.simpleWebServer.utils.CommonUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("ticket")
class TicketController {

    @Autowired
    private lateinit var commonUtils: CommonUtils

    @Autowired
    private lateinit var ticketRepository: TicketRepository

    @Autowired
    private lateinit var messagePairRepository: MessagePairRepository


    @PostMapping("create")
    fun createTicket(@RequestBody ticket: TicketDTO, @CookieValue("jwt") jwt: String): ResponseEntity<String> {
        val user = commonUtils.getUser(jwt) ?: return ResponseEntity.status(401).body("Unauthorized")
        if (user.role == RoleType.ADMIN_PENDING || user.role == RoleType.MANAGER) {
            return ResponseEntity.status(403).body("Forbidden")
        }
        val newTicket = Ticket(user)
        addMessageToTicket(newTicket, user, ticket.message)
        return ResponseEntity.ok("Ticket created")
    }

    @PostMapping("addMessage")
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
        if (ticket.state == TicketState.CLOSED) return ResponseEntity.status(403).body("Ticket is already closed")

        return if ((ticket.user.role == RoleType.USER && user.role == RoleType.ADMIN) || (ticket.user.role == RoleType.ADMIN && user.role == RoleType.MANAGER)) {
            ticket.state = newState
            ticketRepository.save(ticket)
            ResponseEntity.ok("State changed")
        } else ResponseEntity.status(403).body("Forbidden")
    }

    @GetMapping("getAll")
    private fun getTickets(@CookieValue("jwt") jwt: String): ResponseEntity<List<TicketDTO>> {
        val tickets: List<Ticket> = commonUtils.getTickets(jwt) ?: return ResponseEntity.status(401).body(emptyList())
        return ResponseEntity.ok().body(tickets.map { it.toDTO() })
    }

    @GetMapping("get")
    private fun getTicket(@RequestParam id: Long, @CookieValue("jwt") jwt: String): ResponseEntity<TicketDTO> {
        val messagePairs = messagePairRepository.getAllByTicketId(id)
        return ResponseEntity.ok()
            .body(TicketDTO("", id, null, messagePairs.map { Pair(it.user.username, it.message) }))
    }

    private fun addMessageToTicket(ticket: Ticket, user: User, message: String) {
        val messagePair = MessagePair(user, message, ticket)
        messagePairRepository.save(messagePair)
    }
}