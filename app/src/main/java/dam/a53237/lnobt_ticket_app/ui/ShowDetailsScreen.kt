package dam.a53237.lnobt_ticket_app.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import dam.a53237.lnobt_ticket_app.R
import dam.a53237.lnobt_ticket_app.model.Show
import dam.a53237.lnobt_ticket_app.model.ShowDate
import dam.a53237.lnobt_ticket_app.ui.theme.DarkMaroon
import kotlinx.coroutines.tasks.await
import java.util.Date

@SuppressLint("DiscouragedApi")
@Composable
fun ShowDetailsScreen(
    navController: NavController,
    showId: String,
    title: String
) {
    val db = FirebaseFirestore.getInstance()
    var show by remember { mutableStateOf<Show?>(null) }
    var firstDate by remember { mutableStateOf<Date?>(null) }
    var lastDate by remember { mutableStateOf<Date?>(null) }
    val context = LocalContext.current

    LaunchedEffect(showId) {
        try {
            val doc = db.collection("shows").document(showId).get().await()
            show = doc.toObject(Show::class.java)

            val datesSnapshot = db.collection("shows")
                .document(showId)
                .collection("Dates")
                .whereEqualTo("Active", true)
                .orderBy("Date")
                .get()
                .await()

            val dates = datesSnapshot.documents.mapNotNull {
                it.toObject(ShowDate::class.java)?.date?.toDate()
            }.sorted()

            firstDate = dates.firstOrNull()
            lastDate = dates.lastOrNull()

        } catch (e: Exception) {
            println("Error loading show details: ${e.message}")
        }
    }

    if (show == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color.White)
        }
    } else {
        val imageRes = context.resources.getIdentifier(show!!.imageName, "drawable", context.packageName)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1C1C1C))
        ) {
            // Top header with back and logos
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkMaroon)
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(painter = painterResource(id = R.drawable.logo), contentDescription = null, modifier = Modifier.height(28.dp))
                Image(painter = painterResource(id = R.drawable.opera_text), contentDescription = null, modifier = Modifier.height(24.dp))
            }

            LazyColumn(modifier = Modifier.padding(16.dp)) {
                item {
                    Text(
                        text = show!!.title.uppercase(),
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${firstDate?.let { formatDate(it) }} - ${lastDate?.let {
                            formatDate(
                                it
                            )
                        }}",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                "Composer",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                            Text(
                                show!!.composer,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }

                        val hours = show!!.duration / 60
                        val minutes = show!!.duration % 60
                        val formattedDuration = if (minutes.toLong() == 0L) "$hours h" else "$hours h $minutes min"

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                "Duration",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                            Text(
                                formattedDuration,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = {
                            navController.navigate("date_selection/$showId/$title")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkMaroon),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("SHOW DATES", color = Color.White)
                    }



                    Spacer(Modifier.height(24.dp))

                    Text(
                        "Description",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        show!!.description,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }
}
