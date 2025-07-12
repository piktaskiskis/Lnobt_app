package dam.a53237.lnobt_ticket_app.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class ShowDate(
    @get:PropertyName("Active") @set:PropertyName("Active")
    var active: Boolean = false,

    @get:PropertyName("Date") @set:PropertyName("Date")
    var date: Timestamp? = null
)

