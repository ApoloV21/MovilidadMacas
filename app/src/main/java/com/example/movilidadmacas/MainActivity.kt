package com.example.movilidadmacas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.movilidadmacas.ui.theme.MovilidadMacasTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Evita que se creen múltiples instancias de la actividad
        if (!isTaskRoot) {
            finish()
            return
        }

        setContent {
            MovilidadMacasTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Controlador de navegación
                    val navController = rememberNavController()
                    AppNavGraph(navController)
                }
            }
        }
    }
}