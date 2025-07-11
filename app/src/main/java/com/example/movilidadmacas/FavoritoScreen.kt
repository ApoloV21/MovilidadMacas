package com.example.movilidadmacas

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritosScreen(navController: NavHostController) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val database = FirebaseDatabase.getInstance().reference

    var favoritas by remember { mutableStateOf<List<Parada>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(userId) {
        if (userId != null) {
            database.child("favoritos").child(userId).get().addOnSuccessListener { snapshot ->
                val ids = snapshot.children.mapNotNull { it.key }

                val paradasList = mutableListOf<Parada>()
                val total = ids.size
                var loaded = 0

                ids.forEach { paradaId ->
                    database.child("paradas").child(paradaId).get().addOnSuccessListener { paradaSnap ->
                        paradaSnap?.let {
                            val parada = it.getValue(Parada::class.java)
                            parada?.let {
                                it.id = paradaId
                                paradasList.add(it)
                            }
                        }
                        loaded++
                        if (loaded == total) {
                            favoritas = paradasList
                            loading = false
                        }
                    }.addOnFailureListener {
                        loaded++
                        if (loaded == total) {
                            favoritas = paradasList
                            loading = false
                        }
                    }
                }

                if (ids.isEmpty()) loading = false

            }.addOnFailureListener {
                loading = false
                Log.e("Favoritos", "Error al obtener favoritos: ${it.message}")
            }
        } else {
            loading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Paradas Favoritas") }
            )
        }
    ) { padding ->
        if (loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (favoritas.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No tienes paradas favoritas aún.")
            }
        } else {
            LazyColumn(
                contentPadding = padding,
                modifier = Modifier.padding(16.dp)
            ) {
                items(favoritas) { parada ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = parada.nombre, style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("🚌 Rutas: ${parada.rutas.joinToString()}")
                            Text("⏰ Horario: ${parada.horarios.inicio} - ${parada.horarios.fin}  •  Cada ${parada.horarios.frecuencia}")

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                                Button(
                                    onClick = {
                                        navController.navigate(
                                            Screen.DetalleParada.createRoute(
                                                parada.id,
                                                parada.nombre,
                                                parada.rutas,
                                                parada.horarios.inicio,
                                                parada.horarios.fin,
                                                parada.horarios.frecuencia,
                                                parada.lat,
                                                parada.lon
                                            )
                                        )
                                    },
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    Text("Detalles")
                                }

                                Button(
                                    onClick = {
                                        navController.navigate(Screen.RutaMap.createRoute(parada.lat, parada.lon))
                                    }
                                ) {
                                    Text("Ir")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
