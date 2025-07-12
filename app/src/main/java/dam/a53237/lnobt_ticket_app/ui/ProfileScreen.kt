

package dam.a53237.lnobt_ticket_app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dam.a53237.lnobt_ticket_app.R
import dam.a53237.lnobt_ticket_app.ui.theme.DarkMaroon
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ProfileScreen(navController: NavController) {
    var qrVisible by remember { mutableStateOf<String?>(null) }
    LocalContext.current
    remember { TicketManager.reservedTickets }
    val bought = remember { TicketManager.boughtTickets }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFF1C1C1C)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkMaroon)
                    .padding(horizontal = 22.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier.height(30.dp)
                )
                Column(horizontalAlignment = Alignment.End) {
                    Spacer(modifier = Modifier.height(10.dp))
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
                    }
                }
            }

            Column(modifier = Modifier.padding(24.dp)) {
                // User Info
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(DarkMaroon),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("HP", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Harry Potter", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        Text("harry@example.com", color = Color.LightGray, fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                Text("MY TICKETS", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)

                // Reserved tickets
                TicketManager.getActiveReservedTickets().forEach { ticket ->
                    val remaining = remember(ticket) {
                        mutableLongStateOf((15 * 60 * 1000 - (System.currentTimeMillis() - ticket.reservedAt)) / 1000)
                    }

                    LaunchedEffect(ticket) {
                        while (remaining.longValue > 0) {
                            kotlinx.coroutines.delay(1000)
                            remaining.longValue -= 1
                        }
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(ticket.title, color = Color.Black, fontWeight = FontWeight.Bold)
                                Text(
                                    SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(ticket.date),
                                    color = Color.Gray
                                )
                                Text("Area: ${ticket.area}", color = Color.Gray)
                                Text("Time left: ${remaining.longValue}s", color = Color.Red)
                            }

                            Button(
                                onClick = {
                                    TicketManager.buyTicket(ticket)
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("\uD83C\uDF89 Ticket purchased!")
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DarkMaroon),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("BUY", color = Color.White)
                            }
                        }
                    }
                }

                // Bought tickets with QR
                bought.forEach { ticket ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(ticket.title, color = Color.Black, fontWeight = FontWeight.Bold)
                                    Text(
                                        SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(ticket.date),
                                        color = Color.Gray
                                    )
                                    Text("Area: ${ticket.area}", color = Color.Gray)
                                }

                                IconButton(
                                    onClick = {
                                        qrVisible = if (qrVisible == ticket.title) null else ticket.title
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.qr_code),
                                        contentDescription = "QR Code",
                                        tint = Color.Unspecified
                                    )
                                }
                            }

                            AnimatedVisibility(
                                visible = qrVisible == ticket.title,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.qr_code),
                                        contentDescription = "QR",
                                        modifier = Modifier
                                            .size(200.dp)
                                            .clip(RoundedCornerShape(10.dp))
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
