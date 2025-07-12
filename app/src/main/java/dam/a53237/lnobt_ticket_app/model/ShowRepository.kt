package dam.a53237.lnobt_ticket_app.model

import android.annotation.SuppressLint
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object ShowRepository {
    @SuppressLint("StaticFieldLeak")
    private val db = FirebaseFirestore.getInstance()

    suspend fun fetchShows(): List<Show> {
        return try {
            val snapshot = db.collection("shows").get().await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Show::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
