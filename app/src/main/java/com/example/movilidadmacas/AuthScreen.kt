package com.example.movilidadmacas

import android.app.Activity
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun AuthScreen(navController: NavHostController) {
    val context = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }
    val oneTapClient = remember { Identity.getSignInClient(context) }
    val showError = remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
            val idToken = credential.googleIdToken
            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(firebaseCredential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        navController.navigate("inicio") {
                            popUpTo("auth") { inclusive = true }
                        }
                    } else {
                        showError.value = "Error al iniciar sesión con Firebase"
                    }
                }
        } else {
            showError.value = "Error en el inicio de sesión"
        }
    }

    val signInRequest = remember {
        BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId("209731271478-u9h0jfmr1ip1ij9drvsoc2p3ljf5lt5e.apps.googleusercontent.com") // ← Reemplaza por el correcto
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Bienvenido a Movilidad Macas", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(32.dp))

        // Botón Iniciar con Google
        Button(
            onClick = {
                scope.launch(Dispatchers.Main) {
                    try {
                        val result = oneTapClient.beginSignIn(signInRequest).await()
                        launcher.launch(IntentSenderRequest.Builder(result.pendingIntent.intentSender).build())
                    } catch (e: Exception) {
                        Log.e("AuthScreen", "Google SignIn Error: ${e.message}")
                        showError.value = "No se pudo iniciar sesión con Google"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(Icons.Default.AccountBox, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Iniciar sesión con Google")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón Iniciar con correo
        //OutlinedButton(
        //    onClick = { navController.navigate("login_email") },
        //    modifier = Modifier.fillMaxWidth(),
        //) {
        //    Icon(Icons.Default.Email, contentDescription = null)
        //    Spacer(modifier = Modifier.width(8.dp))
        //    Text("Iniciar sesión con correo")
        //}

        Spacer(modifier = Modifier.height(16.dp))

        // Botón Registrarse
        OutlinedButton(
            onClick = { navController.navigate("register") },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(Icons.Default.PersonAdd, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Registrarse")
        }

        Spacer(modifier = Modifier.height(24.dp))

        showError.value?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
    var showExitDialog by remember { mutableStateOf(false) }

    BackHandler(enabled = true) {
        showExitDialog = true
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Confirmar salida") },
            text = { Text("¿Está seguro de que quiere salir de la app?") },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    android.os.Process.killProcess(android.os.Process.myPid())
                }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

}
