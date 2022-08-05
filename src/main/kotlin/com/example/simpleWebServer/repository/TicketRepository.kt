package com.example.simpleWebServer.repository

import com.example.simpleWebServer.entity.Ticket
import org.springframework.data.jpa.repository.JpaRepository

interface TicketRepository : JpaRepository<Ticket, Long>
