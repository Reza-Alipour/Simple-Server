package com.example.simpleWebServer.entity

import com.example.simpleWebServer.dto.TicketDTO
import javax.persistence.*

@Entity
class Ticket(
    @ManyToOne var user: User, var state: TicketState = TicketState.OPEN, @Id @GeneratedValue var id: Long? = null
) : ToDTO {
    constructor() : this(User())

    override fun toDTO(): TicketDTO {
        return TicketDTO(
            id = id,
            state = state,
            message = "",
        )
    }

}

enum class TicketState {
    OPEN, CLOSED, IN_PROGRESS, RESOLVED
}

@Entity
class MessagePair(
    @ManyToOne var user: User,
    @Column(columnDefinition = "TEXT") var message: String,
    @ManyToOne(cascade = [CascadeType.ALL]) var ticket: Ticket,
    @Id @GeneratedValue var id: Long? = null
) {
    constructor() : this(User(), "", Ticket())
}