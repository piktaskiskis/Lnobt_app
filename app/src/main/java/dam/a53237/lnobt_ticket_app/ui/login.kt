package dam.a53237.lnobt_ticket_app.ui

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") } //harry.potter@gmail.com
    var password by remember { mutableStateOf("") } //patronum
    val scrollState = rememberScrollState()

    val backgroundColor = Color(0xFF813636) // dark maroon
    val inputColor = Color(0xFFD2BEBE)
    val textColor = Color.White

    val auth = FirebaseAuth.getInstance()

    // Google Sign-In setup
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken("911993680112-m3vhf1gvelfs8tlviufdlo7tlm2kkhvq.apps.googleusercontent.com")
        .requestEmail()
        .build()

    val googleSignInClient = GoogleSignIn.getClient(context, gso)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d("GoogleSignIn", "Result code: ${result.resultCode}") // ✅ log here

        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        if (task.isSuccessful) {
            val account = task.result
            Log.d("GoogleSignIn", "ID Token: ${account.idToken}")
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential).addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    navController.navigate("home")
                } else {
                    Toast.makeText(context, "Google login failed", Toast.LENGTH_SHORT).show()
                    Log.e("GoogleSignIn", "Firebase auth failed", authTask.exception)
                }
            }
        } else {
            Toast.makeText(context, "Google Sign-In canceled", Toast.LENGTH_SHORT).show()
            Log.e("GoogleSignIn", "Sign-In failed", task.exception)
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF571919))
                .verticalScroll(scrollState)
                .imePadding()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Welcome Text
            Text(
                text = "Welcome back",
                color = textColor,
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Text(
                text = "to LNOBT ticket app -\nthe easiest and fastest way\nto buy a ticket to your\nfavorite shows.",
                color = textColor,
                fontSize = 24.sp,
                lineHeight = 23.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Email Input
            TextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Your email", color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(50),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = inputColor,
                    focusedContainerColor = inputColor,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.Black
                )
            )

            // Password Input
            TextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Your password", color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(50),
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = inputColor,
                    focusedContainerColor = inputColor,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.Black
                )
            )

            // Forgot Password
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Forgot password?",
                    fontSize = 12.sp,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(top = 4.dp, end = 6.dp)
                )
            }

            // Email/Password Sign In
            OutlinedButton(
                onClick = {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                navController.navigate("home")
                            } else {
                                Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                },
                border = BorderStroke(1.dp, Color.White),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .height(50.dp)
            ) {
                Text("Sign in", color = Color.White)
            }

            // Google Sign-In Button
            OutlinedButton(
                onClick = {
                    val signInIntent = googleSignInClient.signInIntent
                    launcher.launch(signInIntent)
                },
                border = BorderStroke(1.dp, Color.White),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 18.dp)
                    .height(50.dp)
            ) {
                Text("Sign in with Google", color = Color.White)
            }

            // Sign up option
            Row(horizontalArrangement = Arrangement.Center) {
                Text("Don't have an account?", fontSize = 12.sp, color = Color.White)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "Sign up",
                    fontSize = 12.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        navController.navigate("register")
                    }
                )
            }
        }
    }
}
