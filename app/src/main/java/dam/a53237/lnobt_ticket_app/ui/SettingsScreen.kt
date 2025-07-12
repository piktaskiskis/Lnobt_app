package dam.a53237.lnobt_ticket_app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dam.a53237.lnobt_ticket_app.ui.theme.DarkMaroon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    var selectedLanguage by remember { mutableStateOf("English") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Settings", color = Color.White)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkMaroon)
            )
        },
        containerColor = Color(0xFF1C1C1C)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(20.dp)
                .fillMaxSize()
        ) {
            SectionHeader("Language")

            LanguageDropdown(
                current = selectedLanguage,
                options = listOf("English", "Portuguese", "Lithuanian"),
                onOptionSelected = { selectedLanguage = it }
            )

            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider(color = Color.Gray.copy(alpha = 0.5f))

            Spacer(modifier = Modifier.height(20.dp))
            SectionHeader("Creator")
            Text(
                "Ieva – Erasmus student from Vilnius Tech",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider(color = Color.Gray.copy(alpha = 0.5f))

            Spacer(modifier = Modifier.height(20.dp))
            SectionHeader("Help Center")

            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider(color = Color.Gray.copy(alpha = 0.5f))

            Spacer(modifier = Modifier.height(20.dp))
            SectionHeader("Account Info")
            Text("Email: harry.potter@gmail.com", style = MaterialTheme.typography.bodySmall, color = Color.White)
        }
    }
}

@Composable
fun LanguageDropdown(
    current: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        TextButton(
            onClick = { expanded = true },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF2C2C2C), shape = MaterialTheme.shapes.medium)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(current, color = Color.White)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color(0xFF2C2C2C))
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, color = Color.White) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.titleMedium,
        color = Color.White,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}
