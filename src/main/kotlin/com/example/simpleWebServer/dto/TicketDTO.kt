package com.example.simpleWebServer.dto

import com.example.simpleWebServer.entity.TicketState

class TicketDTO(
    val message: String,
    val id: Long? = null,
    val state: TicketState? = null,
    val messages: List<Pair<String, String>>? = null
) : DTO