package dam.a53237.lnobt_ticket_app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import dam.a53237.lnobt_ticket_app.model.ReservedTicket
import dam.a53237.lnobt_ticket_app.model.Show
import dam.a53237.lnobt_ticket_app.ui.theme.DarkMaroon
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AreaSelectionScreen(
    showId: String,
    date: Date,
    title: String,
    navController: NavController
) {
    val db = FirebaseFirestore.getInstance()
    var prices by remember { mutableStateOf<Map<String, Double>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }

    val areaColors = listOf(
        Color(0xFFB81D24), Color(0xFF007B7F), Color(0xFF3FB9A9),
        Color(0xFFF145AE), Color(0xFFFFC700), Color(0xFFB49FCC), Color(0xFF6F65FF)
    )

    LaunchedEffect(showId) {
        try {
            val doc = db.collection("shows").document(showId).get().await()
            val show = doc.toObject(Show::class.java)
            prices = show?.prices ?: emptyMap()
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
                        modifier = Modifier.padding(start = 16.dp).size(28.dp)
                    )
                },
                actions = {
                    Icon(
                        painter = painterResource(id = R.drawable.opera_text),
                        contentDescription = "text",
                        tint = Color.Unspecified,
                        modifier = Modifier.padding(end = 16.dp).size(24.dp)
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

            val formattedDate = SimpleDateFormat("dd EEEE, HH:mm", Locale.getDefault()).format(date)
            Text(
                text = formattedDate,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEDEDED))
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        prices.entries.sortedBy { it.key }.forEachIndexed { index, entry ->
                            val (area, price) = entry
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .background(
                                                color = areaColors.getOrElse(index) { Color.Gray },
                                                shape = MaterialTheme.shapes.small
                                            )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(area, color = Color.Black)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("€${"%.2f".format(price)}", color = Color.Black)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Button(
                                        onClick = {
                                            val ticket = ReservedTicket(
                                                title = title,
                                                date = date,
                                                area = area
                                            )
                                            TicketManager.reserveTicket(ticket)
                                            navController.navigate("profile")
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF930C0C)
                                        ),
                                        contentPadding = PaddingValues(
                                            horizontal = 12.dp,
                                            vertical = 4.dp
                                        )
                                    ) {
                                        Text("RESERVE", color = Color.White)
                                    }
                                }
                            }
                            HorizontalDivider(color = Color.LightGray)
                        }
                    }
                }
            }
        }
    }
}
