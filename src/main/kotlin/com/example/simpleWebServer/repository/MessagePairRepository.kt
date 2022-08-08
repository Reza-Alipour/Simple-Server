package com.example.simpleWebServer.repository

import com.example.simpleWebServer.entity.MessagePair
import org.springframework.data.jpa.repository.JpaRepository

interface MessagePairRepository : JpaRepository<MessagePair, Long> {
    fun getAllByTicketId(ticketId: Long): List<MessagePair>
}