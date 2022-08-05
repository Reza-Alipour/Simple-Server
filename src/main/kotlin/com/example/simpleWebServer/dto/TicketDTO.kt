package com.example.simpleWebServer.dto

import com.example.simpleWebServer.entity.TicketState

class TicketDTO(
    val message: String, val id: Long? = null, val state: TicketState? = null
)