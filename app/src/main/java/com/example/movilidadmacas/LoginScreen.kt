package com.example.movilidadmacas

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await


@Composable
fun LoginScreen(navController: NavHostController) {
    val context = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }
    val oneTapClient = remember { Identity.getSignInClient(context) }
    val showError = remember { mutableStateOf<String?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
            val idToken = credential.googleIdToken
            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(firebaseCredential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        navController.navigate("inicio") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        showError.value = "Error al iniciar sesión con Firebase"
                    }
                }
        } else {
            showError.value = "Error en el inicio de sesión"
        }
    }

    LaunchedEffect(Unit) {
        if (auth.currentUser == null) {
            val signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(
                    BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId("209731271478-u9h0jfmr1ip1ij9drvsoc2p3ljf5lt5e.apps.googleusercontent.com")
                        .setFilterByAuthorizedAccounts(false)
                        .build()
                ).build()

            try {
                val result = oneTapClient.beginSignIn(signInRequest).await()
                launcher.launch(IntentSenderRequest.Builder(result.pendingIntent.intentSender).build())
            } catch (e: Exception) {
                Log.e("LoginScreen", "Error al iniciar sesión: ${e.message}")
                showError.value = "No se pudo iniciar sesión"
            }
        } else {
            // Ya estaba logueado, ir a Inicio directamente
            navController.navigate("inicio") {
                popUpTo("login") { inclusive = true }
            }
        }
    }


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Iniciando sesión con Google...")
        showError.value?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }
    }
}
