package com.example.movilidadmacas

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import org.osmdroid.util.GeoPoint

sealed class Screen(val route: String) {
    object Inicio : Screen("inicio")
    object Mapa : Screen("mapa")
    object DetalleParada : Screen("detalleParada") {
        fun createRoute(
            id: String,
            nombre: String,
            rutas: List<String>,
            inicio: String,
            fin: String,
            frecuencia: String,
            lat: Double,
            lon: Double
        ): String {
            return "detalleParada/${Uri.encode(id)}/${Uri.encode(nombre)}/${Uri.encode(rutas.joinToString(","))}/${Uri.encode(inicio)}/${Uri.encode(fin)}/${Uri.encode(frecuencia)}"
        }
    }
    object Login : Screen("login")
    object Register : Screen("register")
    object Favoritos : Screen("favoritos")

    object RutaMap : Screen("rutaMap/{lat}/{lon}") {
        fun createRoute(lat: Double, lon: Double): String = "rutaMap/$lat/$lon"
    }

}

@Composable
fun AppNavGraph(navController: NavHostController) {

    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val startDestination = if (currentUser != null) "inicio" else "auth"

    NavHost(navController, startDestination) {
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.Register.route) { RegisterScreen(navController) }
        composable(Screen.Inicio.route) { InicioScreen(navController) }
        composable(Screen.Mapa.route) { MapScreen(navController) }
        composable("loginEmail") { LoginEmailScreen(navController)}
        composable("auth") { AuthScreen(navController) }
        composable(
            route = "detalleParada/{id}/{nombre}/{rutas}/{inicio}/{fin}/{frecuencia}",
            arguments = listOf(
                navArgument("id") { type = NavType.StringType },
                navArgument("nombre") { type = NavType.StringType },
                navArgument("rutas") { type = NavType.StringType },
                navArgument("inicio") { type = NavType.StringType },
                navArgument("fin") { type = NavType.StringType },
                navArgument("frecuencia") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.let { Uri.decode(it) } ?: ""
            val nombre = backStackEntry.arguments?.getString("nombre")?.let { Uri.decode(it) } ?: ""
            val rutas = backStackEntry.arguments?.getString("rutas")?.let { Uri.decode(it).split(",") } ?: emptyList()
            val inicio = backStackEntry.arguments?.getString("inicio")?.let { Uri.decode(it) } ?: ""
            val fin = backStackEntry.arguments?.getString("fin")?.let { Uri.decode(it) } ?: ""
            val frecuencia = backStackEntry.arguments?.getString("frecuencia")?.let { Uri.decode(it) } ?: ""


            DetalleParadaScreen(id = id, nombre = nombre, rutas = rutas, inicio = inicio, fin = fin, frecuencia = frecuencia, navController = navController)

        }
        composable(Screen.Favoritos.route) {
            FavoritosScreen(navController)
        }
        composable(
            route = "rutaMap/{lat}/{lon}",
            arguments = listOf(
                navArgument("lat") { type = NavType.StringType },
                navArgument("lon") { type = NavType.StringType },
            )
        ) { backStackEntry ->
            val lat = backStackEntry.arguments?.getString("lat")?.toDoubleOrNull() ?: 0.0
            val lon = backStackEntry.arguments?.getString("lon")?.toDoubleOrNull() ?: 0.0

            MapScreen(navController, destino = GeoPoint(lat, lon))
        }
    }
}