package com.example.movilidadmacas

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleParadaScreen(
    nombre: String,
    rutas: List<String>,
    horarios: List<String>,
    navController: NavHostController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles de la Parada") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(
                text = nombre,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text("🚌 Rutas disponibles:", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            rutas.forEach {
                Text("• $it", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("⏰ Horarios estimados:", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            horarios.forEach {
                Text("• $it", fontSize = 16.sp)
            }
        }
    }
}