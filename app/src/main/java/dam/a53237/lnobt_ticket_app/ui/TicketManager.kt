package dam.a53237.lnobt_ticket_app.ui

import androidx.compose.runtime.mutableStateListOf
import dam.a53237.lnobt_ticket_app.model.ReservedTicket

object TicketManager {
    val reservedTickets = mutableStateListOf<ReservedTicket>()
    val boughtTickets = mutableStateListOf<ReservedTicket>()

    fun reserveTicket(ticket: ReservedTicket) {
        reservedTickets.add(ticket)
    }

    fun buyTicket(ticket: ReservedTicket) {
        reservedTickets.remove(ticket)
        boughtTickets.add(ticket)
    }

    fun getActiveReservedTickets(): List<ReservedTicket> {
        val now = System.currentTimeMillis()
        return reservedTickets.filter {
            now - it.reservedAt < 15 * 60 * 1000
        }
    }

}


