package com.example.movilidadmacas

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.auth.api.identity.Identity
import androidx.compose.ui.platform.LocalContext

@Composable
fun InicioScreen(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val context = LocalContext.current
    val oneTapClient = Identity.getSignInClient(context)


    // Si no hay usuario, redirigir a login
    LaunchedEffect(currentUser) {
        if (currentUser == null) {
            navController.navigate("login") {
                popUpTo("inicio") { inclusive = true }
            }
        }
    }

    // Si el usuario es nulo, no mostrar la UI
    if (currentUser == null) return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val imagePainter = painterResource(R.drawable.logo_movilidad)

        Image(
            painter = imagePainter,
            contentDescription = "Logo Movilidad Macas",
            modifier = Modifier
                .size(150.dp)
                .padding(bottom = 32.dp),
            contentScale = ContentScale.Fit
        )

        Text(
            text = "Movilidad Macas",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = { navController.navigate("mapa") },
            modifier = Modifier.width(200.dp)
        ) {
            Text("Ver Mapa")
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Botón de cerrar sesión
        Button(onClick = {
            auth.signOut()
            oneTapClient.signOut().addOnCompleteListener {
                navController.navigate("auth") {
                    popUpTo("inicio") { inclusive = true }
                }
            }
        }) {
            Text("Cerrar sesión")
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
