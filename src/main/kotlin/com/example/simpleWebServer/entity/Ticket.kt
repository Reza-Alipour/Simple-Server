package com.example.simpleWebServer.entity

import com.example.simpleWebServer.dto.DTO
import javax.persistence.*

@Entity
class Ticket(
    req: String,
    response: String? = null,
    state: TicketState = TicketState.OPEN,
    @Id @GeneratedValue var id: Long? = null,
) : ToDTO {

    constructor() : this("")

    @ManyToOne(fetch = FetchType.LAZY)
    var user:User? = null

    @ManyToOne(fetch = FetchType.LAZY)
    var admin:User? = null


    override fun toDTO(): DTO {
        TODO("Not yet implemented")
    }

}

enum class TicketState {
    OPEN,
    CLOSED,
    IN_PROGRESS,
    RESOLVED
}