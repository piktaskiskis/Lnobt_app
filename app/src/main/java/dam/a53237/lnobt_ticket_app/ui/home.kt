package dam.a53237.lnobt_ticket_app.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import dam.a53237.lnobt_ticket_app.R
import dam.a53237.lnobt_ticket_app.model.Show
import dam.a53237.lnobt_ticket_app.model.ShowDate
import dam.a53237.lnobt_ticket_app.ui.theme.DarkMaroon
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import java.net.URLEncoder
import java.util.*

@SuppressLint("DiscouragedApi")
@Composable
fun HomeScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    var shows by remember { mutableStateOf<List<Triple<Show, Date?, Date?>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var currentIndex by remember { mutableIntStateOf(0) }
    val context = LocalContext.current

    // Auto-scroll
    LaunchedEffect(shows) {
        if (shows.isNotEmpty()) {
            while (true) {
                delay(4000)
                currentIndex = (currentIndex + 1) % shows.size
            }
        }
    }

    // Fetch shows
    LaunchedEffect(true) {
        try {
            val snapshot = db.collection("shows").get().await()
            val result = mutableListOf<Triple<Show, Date?, Date?>>()

            for (doc in snapshot.documents) {
                val show = doc.toObject(Show::class.java)?.copy(id = doc.id) ?: continue
                val dateSnapshot = db.collection("shows")
                    .document(doc.id)
                    .collection("Dates")
                    .whereEqualTo("Active", true)
                    .orderBy("Date")
                    .get()
                    .await()

                val dates = dateSnapshot.documents.mapNotNull {
                    it.toObject(ShowDate::class.java)?.date?.toDate()
                }.sorted()

                val firstDate = dates.firstOrNull()
                val lastDate = dates.lastOrNull()
                result.add(Triple(show, firstDate, lastDate))
            }

            shows = result.sortedBy { it.second }
        } catch (_: Exception) {
        } finally {
            isLoading = false
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
        }
    } else {
        val (show, firstDate, lastDate) = shows.getOrNull(currentIndex) ?: return
        val imageResId = context.resources.getIdentifier(show.imageName, "drawable", context.packageName)

        Column(modifier = Modifier.fillMaxSize()) {

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

            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
            ) {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                )

                // Tagline
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        text = "Discover our upcoming performances",
                        color = Color.White,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Title between arrows
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.arrowback),
                            contentDescription = "Back",
                            modifier = Modifier
                                .size(32.dp)
                                .clickable {
                                    currentIndex = if (currentIndex - 1 < 0) shows.lastIndex else currentIndex - 1
                                }
                        )
                        Text(
                            text = show.title.uppercase(),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp),
                            maxLines = 2,
                            textAlign = TextAlign.Center
                        )
                        Image(
                            painter = painterResource(id = R.drawable.next),
                            contentDescription = "Next",
                            modifier = Modifier
                                .size(32.dp)
                                .clickable {
                                    currentIndex = (currentIndex + 1) % shows.size
                                }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Date range
                    Text(
                        text = if (firstDate != null && lastDate != null)
                            "From: ${formatDate(firstDate)}   To: ${formatDate(lastDate)}"
                        else "TBA",
                        fontSize = 14.sp,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Shortened description
                    Text(
                        text = show.description,
                        fontSize = 14.sp,
                        color = Color.White,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            val encodedId = URLEncoder.encode(show.id, "UTF-8")
                            val encodedTitle = URLEncoder.encode(show.title, "UTF-8")
                            navController.navigate("show_details/$encodedId/$encodedTitle")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkMaroon),
                        shape = RoundedCornerShape(20.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                    ) {
                        Text("TICKETS", color = Color.White)
                    }


                    Spacer(modifier = Modifier.height(20.dp))

                    // Dot indicators
                    Row(horizontalArrangement = Arrangement.Center) {
                        shows.indices.forEach { i ->
                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .size(if (i == currentIndex) 10.dp else 6.dp)
                                    .clip(CircleShape)
                                    .background(if (i == currentIndex) Color.White else Color.Gray)
                            )
                        }
                    }
                }
            }
        }
    }
}


