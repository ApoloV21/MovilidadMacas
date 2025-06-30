package com.example.movilidadmacas

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

sealed class Screen(val route: String) {
    object Inicio : Screen("inicio")
    object Mapa : Screen("mapa")
    object DetalleParada : Screen("detalleParada") {
        fun createRoute(nombre: String, rutas: List<String>, horarios: List<String>) =
            "detalleParada/${Uri.encode(nombre)}/${Uri.encode(rutas.joinToString(","))}/${Uri.encode(horarios.joinToString(","))}"
    }
    object Login : Screen("login")
    object Register : Screen("register")
}

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController, startDestination = "auth") {
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.Register.route) { RegisterScreen(navController) }
        composable(Screen.Inicio.route) { InicioScreen(navController) }
        composable(Screen.Mapa.route) { MapScreen(navController) }
        composable("auth") { AuthScreen(navController) }
        composable(
            route = "detalleParada/{nombre}/{rutas}/{horarios}",
            arguments = listOf(
                navArgument("nombre") { type = NavType.StringType },
                navArgument("rutas") { type = NavType.StringType },
                navArgument("horarios") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val nombre = backStackEntry.arguments?.getString("nombre")?.let { Uri.decode(it) } ?: ""
            val rutas = backStackEntry.arguments?.getString("rutas")?.let { Uri.decode(it).split(",") } ?: emptyList()
            val horarios = backStackEntry.arguments?.getString("horarios")?.let { Uri.decode(it).split(",") } ?: emptyList()

            DetalleParadaScreen(nombre, rutas, horarios, navController)
        }
    }
}