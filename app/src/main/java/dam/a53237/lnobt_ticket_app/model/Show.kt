package dam.a53237.lnobt_ticket_app.model

import com.google.firebase.firestore.PropertyName
import java.security.Timestamp

data class Show(
    val id: String = "",

    @get:PropertyName("Title")
    @set:PropertyName("Title")
    var title: String = "",

    @get:PropertyName("Composer")
    @set:PropertyName("Composer")
    var composer: String = "",

    @get:PropertyName("Description")
    @set:PropertyName("Description")
    var description: String = "",

    @get:PropertyName("ImageName")
    @set:PropertyName("ImageName")
    var imageName: String = "",

    @get:PropertyName("Prices")
    @set:PropertyName("Prices")
    var prices: Map<String, Double> = emptyMap(),

    @get:PropertyName("Duration")
    @set:PropertyName("Duration")
    var duration: Int = 0,

    @get:PropertyName("isFromWeb")
    @set:PropertyName("isFromWeb")
    var isFromWeb: Boolean = false,

    val dates: List<Timestamp>? = null

)

