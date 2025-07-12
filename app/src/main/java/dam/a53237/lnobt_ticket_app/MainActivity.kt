package dam.a53237.lnobt_ticket_app


import dam.a53237.lnobt_ticket_app.ui.BottomBarNavigation
import java.util.Date
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dam.a53237.lnobt_ticket_app.ui.theme.Lnobt_ticket_appTheme
import dam.a53237.lnobt_ticket_app.ui.ProfileScreen
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import dam.a53237.lnobt_ticket_app.ui.LoginScreen
import dam.a53237.lnobt_ticket_app.ui.HomeScreen
import dam.a53237.lnobt_ticket_app.ui.TicketsScreen
import dam.a53237.lnobt_ticket_app.ui.ShowDetailsScreen
import dam.a53237.lnobt_ticket_app.ui.DateSelectionScreen
import dam.a53237.lnobt_ticket_app.ui.AreaSelectionScreen
import dam.a53237.lnobt_ticket_app.ui.SettingsScreen
import java.net.URLDecoder


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Lnobt_ticket_appTheme {
                LNOBTTicketApp()
            }
        }
    }
}

@Composable
fun LNOBTTicketApp() {
    val navController = rememberNavController()

    BottomBarNavigation(navController = navController) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") { LoginScreen(navController) }
            composable("home") { HomeScreen(navController) }
            composable("tickets") { TicketsScreen(navController) }
            composable("show_details/{showId}/{title}") { backStackEntry ->
                val encodedId = backStackEntry.arguments?.getString("showId") ?: ""
                val showId = URLDecoder.decode(encodedId, "UTF-8")

                val encodedTitle = backStackEntry.arguments?.getString("title") ?: ""
                val title = URLDecoder.decode(encodedTitle, "UTF-8")

                ShowDetailsScreen(navController = navController, showId = showId, title = title)
            }
            composable("date_selection/{showId}/{title}") { backStackEntry ->
                val showId = backStackEntry.arguments?.getString("showId") ?: ""
                val title = backStackEntry.arguments?.getString("title") ?: ""
                DateSelectionScreen(showId = showId, title = title, navController = navController)
            }

            composable("area_selection/{showId}/{dateMillis}/{title}") { backStackEntry ->
                val showId = backStackEntry.arguments?.getString("showId") ?: ""
                val millis = backStackEntry.arguments?.getString("dateMillis")?.toLongOrNull() ?: 0L
                val title = URLDecoder.decode(backStackEntry.arguments?.getString("title") ?: "", "UTF-8")
                val date = Date(millis)
                AreaSelectionScreen(showId = showId, date = date, title = title, navController = navController)
            }


            composable("profile") { ProfileScreen(navController) }
            composable("settings") { SettingsScreen(navController) }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Lnobt_ticket_appTheme {
        LNOBTTicketApp()
    }
}
