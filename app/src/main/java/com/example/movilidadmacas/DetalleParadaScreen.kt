package com.example.movilidadmacas

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.ui.Alignment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleParadaScreen(
    id: String,
    nombre: String,
    rutas: List<String>,
    horarios: List<String>,
    navController: NavHostController
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val scope = rememberCoroutineScope()
    var isFavorita by remember { mutableStateOf(false) }

    // Consultar si ya está en favoritos
    LaunchedEffect(id, userId) {
        if (userId != null) {
            try {
                isFavorita = FavoritosRepository.esFavorita(userId, id)
            } catch (e: Exception) {
                Log.e("Favoritos", "Error al obtener favorito: ${e.message}")
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(text = "Detalles de la Parada", style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = nombre, style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(24.dp))

            Text("🚌 Rutas disponibles:", style = MaterialTheme.typography.titleMedium)
            rutas.forEach { ruta ->
                Text("• $ruta", style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("⏰ Horarios estimados:", style = MaterialTheme.typography.titleMedium)
            horarios.forEach { horario ->
                Text("• $horario", style = MaterialTheme.typography.bodyLarge)
            }
        }

        // ⭐ Botón para marcar como favorito
        FloatingActionButton(
            onClick = {
                if (userId != null) {
                    scope.launch {
                        try {
                            if (isFavorita) {
                                FavoritosRepository.eliminarDeFavoritos(userId, id)
                            } else {
                                FavoritosRepository.agregarAFavoritos(userId, id)
                            }
                            isFavorita = !isFavorita
                        } catch (e: Exception) {
                            Log.e("Favoritos", "Error al actualizar favoritos: ${e.message}")
                        }
                    }
                } else {
                    Log.e("Favoritos", "Usuario no autenticado")
                    Log.d("Favoritos", "userId = $userId")
                }
            },
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = if (isFavorita) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = if (isFavorita) "Eliminar de favoritos" else "Agregar a favoritos"
            )
        }
    }
}

