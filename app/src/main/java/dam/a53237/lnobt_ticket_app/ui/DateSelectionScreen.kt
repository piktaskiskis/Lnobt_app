package dam.a53237.lnobt_ticket_app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import dam.a53237.lnobt_ticket_app.R
import dam.a53237.lnobt_ticket_app.model.ShowDate
import dam.a53237.lnobt_ticket_app.ui.theme.DarkMaroon
import kotlinx.coroutines.tasks.await
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateSelectionScreen(
    showId: String,
    title: String,
    navController: NavController
) {
    var dates by remember { mutableStateOf<List<ShowDate>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(showId) {
        try {
            val db = FirebaseFirestore.getInstance()
            val snapshot = db.collection("shows")
                .document(showId)
                .collection("Dates")
                .whereEqualTo("Active", true)
                .orderBy("Date")
                .get()
                .await()

            dates = snapshot.documents.mapNotNull { it.toObject(ShowDate::class.java) }
        } catch (_: Exception) {
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo",
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .size(28.dp)
                    )
                },
                actions = {
                    Icon(
                        painter = painterResource(id = R.drawable.opera_text),
                        contentDescription = "QR",
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(24.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkMaroon)
            )
        },
        containerColor = Color(0xFF1C1C1C)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "< Back",
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier
                    .clickable { navController.popBackStack() }
                    .padding(bottom = 12.dp)
            )

            Text(
                text = title.uppercase(),
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            if (dates.isNotEmpty()) {
                val first = dates.firstOrNull()?.date?.toDate()
                val last = dates.lastOrNull()?.date?.toDate()
                Text(
                    text = "${first?.let { formatDate(it) }} - ${last?.let { formatDate(it) }}",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else {
                LazyColumn {
                    items(dates) { dateObj ->
                        val localDate = dateObj.date?.toDate()

                        val dayTime = localDate?.let {
                            try {
                                SimpleDateFormat("MMMM d, yyyy (EEEE)", Locale.ENGLISH).format(it)
                            } catch (e: Exception) {
                                ""
                            }
                        } ?: ""

                        val hour = localDate?.let {
                            try {
                                SimpleDateFormat("HH:mm", Locale.ENGLISH).format(it)
                            } catch (e: Exception) {
                                ""
                            }
                        } ?: ""

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable {
                                    val millis = localDate?.time ?: 0L
                                    val encodedTitle = URLEncoder.encode(title, "UTF-8")
                                    navController.navigate("area_selection/$showId/$millis/$encodedTitle")
                                },
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    if (dayTime.isNotEmpty()) {
                                        Text(text = dayTime, fontWeight = FontWeight.SemiBold)
                                    }
                                    if (hour.isNotEmpty()) {
                                        Text(text = hour, fontSize = 14.sp, color = Color.Gray)
                                    }
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = Color.DarkGray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

