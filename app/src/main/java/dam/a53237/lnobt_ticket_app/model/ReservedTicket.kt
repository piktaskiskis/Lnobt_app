package dam.a53237.lnobt_ticket_app.model

import java.util.Date


data class ReservedTicket(
    val title: String,
    val date: Date,
    val area: String,
    val reservedAt: Long = System.currentTimeMillis()
)

