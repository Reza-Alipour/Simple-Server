package com.example.simpleWebServer.entity

import com.example.simpleWebServer.dto.DTO
import javax.persistence.*

@Entity
class Ticket(
    @ManyToOne var user: User, var state: TicketState = TicketState.OPEN, @Id @GeneratedValue var id: Long? = null
) : ToDTO {
    constructor() : this(User())

    @ElementCollection(fetch = FetchType.LAZY)
    var messages: MutableList<Pair<User, String>> = mutableListOf()


    override fun toDTO(): DTO {
        TODO("Not yet implemented")
    }

}

enum class TicketState {
    OPEN, CLOSED, IN_PROGRESS, RESOLVED
}