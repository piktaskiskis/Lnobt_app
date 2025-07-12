package dam.a53237.lnobt_ticket_app.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import dam.a53237.lnobt_ticket_app.R
import dam.a53237.lnobt_ticket_app.ui.theme.DarkMaroon

sealed class BottomNavItem(val route: String, val label: String, val iconResId: Int) {
    data object Home : BottomNavItem("home", "Home", R.drawable.home)
    data object Tickets : BottomNavItem("tickets", "Tickets", R.drawable.ticket)
    data object Profile : BottomNavItem("profile", "Profile", R.drawable.user)
}


@Composable
fun BottomBarNavigation(
    navController: NavController,
    content: @Composable (PaddingValues) -> Unit
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Tickets,
        BottomNavItem.Profile
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute !in listOf("login", "settings", "date_selection", "ticket_view")) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .background(DarkMaroon)
                        .padding(vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        items.forEach { item ->
                            val isSelected = currentRoute == item.route
                            val backgroundColor by animateColorAsState(
                                targetValue = if (isSelected) Color.Black.copy(alpha = 0.3f) else Color.Transparent,
                                animationSpec = tween(durationMillis = 300)
                            )
                            val scale by animateFloatAsState(
                                targetValue = if (isSelected) 1.15f else 1f,
                                animationSpec = tween(durationMillis = 300)
                            )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(backgroundColor)
                                    .clickable {
                                        if (!isSelected) {
                                            navController.navigate(item.route) {
                                                popUpTo("home") { inclusive = false }
                                                launchSingleTop = true
                                            }
                                        }
                                    }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .scale(scale),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Image(
                                        painter = painterResource(id = item.iconResId),
                                        contentDescription = item.label,
                                        modifier = Modifier.size(24.dp),
                                        colorFilter = ColorFilter.tint(
                                            if (isSelected) Color.White else Color.LightGray
                                        )
                                    )
                                    Text(
                                        text = item.label,
                                        fontSize = 11.sp,
                                        color = if (isSelected) Color.White else Color.LightGray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}
