package com.example.movilidadmacas

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

@Composable
fun FavoritosScreen(navController: NavHostController) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val favoritos = remember { mutableStateListOf<Parada>() }

    LaunchedEffect(Unit) {
        if (userId != null) {
            val dbRef = FirebaseDatabase.getInstance().getReference("favoritos").child(userId)
            dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    favoritos.clear()
                    val paradaIds = snapshot.children.mapNotNull { it.key }

                    // Obtener los datos reales de las paradas favoritas
                    val paradasRef = FirebaseDatabase.getInstance().getReference("paradas")
                    paradasRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            for (paradaSnap in dataSnapshot.children) {
                                val parada = paradaSnap.getValue(Parada::class.java)
                                if (parada != null && parada.id in paradaIds) {
                                    favoritos.add(parada)
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Paradas Favoritas",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn {
            items(favoritos) { parada ->
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .clickable {
                            val ruta = Screen.DetalleParada.createRoute(
                                parada.id,
                                parada.nombre,
                                parada.rutas,
                                parada.horarios
                            )
                            navController.navigate(ruta)
                        }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(parada.nombre, style = MaterialTheme.typography.titleMedium)
                        Text("Rutas: ${parada.rutas.joinToString(", ")}")
                    }
                }
            }
        }
    }
}