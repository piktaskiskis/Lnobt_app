package dam.a53237.lnobt_ticket_app.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import dam.a53237.lnobt_ticket_app.R
import dam.a53237.lnobt_ticket_app.model.Show
import dam.a53237.lnobt_ticket_app.model.ShowDate
import dam.a53237.lnobt_ticket_app.ui.theme.DarkMaroon
import kotlinx.coroutines.tasks.await
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@SuppressLint("DiscouragedApi")
@Composable
fun TicketsScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    var showsWithDates by remember { mutableStateOf<List<Pair<Show, Date?>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // State for month filter
    val months = listOf("All") + (1..12).map {
        SimpleDateFormat("MMMM", Locale.getDefault()).format(Calendar.getInstance().apply { set(Calendar.MONTH, it - 1) }.time)
    }
    var selectedMonth by remember { mutableStateOf("All") }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            val snapshot = db.collection("shows").get().await()
            val shows = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Show::class.java)?.copy(id = doc.id)
            }

            val result = mutableListOf<Pair<Show, Date?>>()
            for (show in shows) {
                val dateSnapshot = db.collection("shows")
                    .document(show.id)
                    .collection("Dates")
                    .whereEqualTo("Active", true)
                    .orderBy("Date")
                    .limit(1)
                    .get()
                    .await()

                val showDate = dateSnapshot.documents.firstOrNull()?.toObject(ShowDate::class.java)
                result.add(show to showDate?.date?.toDate())
            }

            // Sort by nearest upcoming date (nulls go last)
            showsWithDates = result.sortedWith(compareBy(nullsLast()) { it.second })
        } catch (e: Exception) {
            println("ERROR: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1C1C1C)) // dark gray
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkMaroon)
                .padding(horizontal = 22.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.height(30.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.opera_text),
                contentDescription = "Text",
                modifier = Modifier.height(28.dp)
            )
        }

        // Month filter dropdown
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            OutlinedButton(onClick = { expanded = true }) {
                Text(selectedMonth)
            }

            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                months.forEach { month ->
                    DropdownMenuItem(
                        text = { Text(month) },
                        onClick = {
                            selectedMonth = month
                            expanded = false
                        }
                    )
                }
            }
        }

        if (isLoading) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator(color = Color.White)
            }
        } else {
            // Filter by selected month
            val filtered = showsWithDates.filter { (_, date) ->
                if (selectedMonth == "All") true
                else {
                    date?.let {
                        val monthName = SimpleDateFormat("MMMM", Locale.getDefault()).format(it)
                        monthName == selectedMonth
                    } ?: false
                }
            }

            LazyColumn(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                items(filtered) { (show, date) ->
                    val context = LocalContext.current
                    val imageResId = remember(show.imageName) {
                        context.resources.getIdentifier(show.imageName, "drawable", context.packageName)
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable {
                                val encodedId = URLEncoder.encode(show.id, "UTF-8")
                                val encodedTitle = URLEncoder.encode(show.title, "UTF-8")
                                navController.navigate("show_details/$encodedId/$encodedTitle")

                            }
                        ,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box {
                            Image(
                                painter = painterResource(id = imageResId),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.4f))
                                    .padding(12.dp),
                                contentAlignment = Alignment.BottomStart
                            ) {
                                Column {
                                    Text(
                                        text = show.title,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = "Next: ${date?.let { formatDate(it) } ?: "TBA"}",
                                        color = Color.White,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Utility date formatter
fun formatDate(date: Date): String {
    val sdf = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
    return sdf.format(date)
}
