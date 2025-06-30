package com.example.movilidadmacas

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.preference.PreferenceManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.*
import kotlinx.coroutines.delay
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import androidx.compose.runtime.LaunchedEffect
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.util.BoundingBox
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.graphics.Color
import android.util.Log
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.draw.shadow
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val ORS_API_KEY = "5b3ce3597851110001cf624827a7fadd6b1e4aaab404f048cce98d64"

@Composable

fun MapScreen(navController: NavHostController) {
    var paradaSeleccionada by remember { mutableStateOf<Parada?>(null) }
    val context = LocalContext.current
    val paradas = remember { mutableStateListOf<Parada>() }
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val locationPermissionGranted = remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        locationPermissionGranted.value = isGranted
    }

    // Añade esto con tus declaraciones de variables existentes:
    var routePolyline by remember { mutableStateOf<Polyline?>(null) }
    var travelTime by remember { mutableStateOf<String?>(null) }
    var showRouteButton by remember { mutableStateOf(false) }
    var ubicacionUsuario by remember { mutableStateOf<GeoPoint?>(null) }

    // Cargar configuración y datos
    LaunchedEffect(Unit) {
        Configuration.getInstance().load(
            context,
            PreferenceManager.getDefaultSharedPreferences(context)
        )
        if (!locationPermissionGranted.value) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        val db = FirebaseDatabase.getInstance().getReference("paradas")
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                paradas.clear()
                for (paradaSnap in snapshot.children) {
                    val parada = paradaSnap.getValue(Parada::class.java)
                    parada?.let { paradas.add(it) }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun drawRoute(start: GeoPoint, end: GeoPoint) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openrouteservice.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(RouteService::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = service.getRoute(
                    ORS_API_KEY,
                    "${start.longitude},${start.latitude}",
                    "${end.longitude},${end.latitude}"
                )

                withContext(Dispatchers.Main) {
                    val points = response.features[0].geometry.coordinates.map {
                        GeoPoint(it[1], it[0])
                    }

                    // Eliminar ruta anterior
                    routePolyline?.let { mapView?.overlays?.remove(it) }

                    // Crear nueva ruta
                    val newPolyline = Polyline(mapView).apply {
                        setPoints(points)
                        color = Color.BLUE
                        width = 8f
                    }

                    mapView?.overlays?.add(newPolyline)
                    routePolyline = newPolyline

                    // Calcular tiempo estimado
                    val duration = (response.features[0].properties.segments[0].duration / 60).toInt()
                    travelTime = "$duration minutos"

                    // Ajustar vista del mapa
                    mapView?.zoomToBoundingBox(BoundingBox.fromGeoPoints(points), false, 50)
                }
            } catch (e: Exception) {
                Log.e("RouteError", "Error: ${e.message}")
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // Mapa
        AndroidView(
            factory = {
                MapView(context).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    controller.setZoom(15.0)
                    controller.setCenter(GeoPoint(-2.3086, -78.1111))
                    mapView = this
                }
            },
            modifier = Modifier.fillMaxSize(),
            onRelease = { mapView -> mapView.onPause() }
        )

        // Campo de búsqueda
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Buscar parada o ruta") },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .shadow(8.dp, RoundedCornerShape(16.dp)),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
            ),
            shape = RoundedCornerShape(16.dp),
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Buscar")
            },
            trailingIcon = {
                if (searchQuery.isNotBlank()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Borrar")
                    }
                }
            }
        )

        // Lista de resultados filtrados
        val paradasFiltradas = paradas.filter {
            searchQuery.isNotBlank() &&
                    (
                            it.nombre?.contains(searchQuery, ignoreCase = true) == true ||
                                    it.rutas?.any { ruta -> ruta?.contains(searchQuery, ignoreCase = true) == true } == true
                            )
        }


        if (searchQuery.isNotBlank()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 80.dp)
                    .align(Alignment.TopCenter)
            ) {
                items(paradasFiltradas) { parada ->
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .fillMaxWidth()
                            .clickable {
                                val punto = GeoPoint(parada.lat, parada.lon)
                                mapView?.controller?.animateTo(punto, 17.0, 1000L) // Zoom 17 en 1 segundo

                                val marker = Marker(mapView)
                                marker.position = punto
                                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                marker.title = parada.nombre
                                marker.subDescription = "Rutas: ${parada.rutas.joinToString(", ")}\nHorarios: ${parada.horarios.joinToString(", ")}"
                                marker.showInfoWindow()
                                mapView?.overlays?.add(marker)
                                mapView?.invalidate()

                                // ⏳ Marcar para navegar después
                                paradaSeleccionada = parada
                            },
                            colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text(parada.nombre, style = MaterialTheme.typography.titleMedium)
                            Text("Rutas: ${parada.rutas.joinToString(", ")}", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        paradaSeleccionada?.let { parada ->
            LaunchedEffect(parada) {
                delay(1500L)
                showRouteButton = true
            }

            if (showRouteButton) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    ExtendedFloatingActionButton(
                        onClick = {
                            val userLocation = ubicacionUsuario
                            if (userLocation != null) {
                                val stopLocation = GeoPoint(parada.lat, parada.lon)
                                drawRoute(userLocation, stopLocation)
                            } else {
                                // Mostrar alerta de que no hay ubicación disponible
                                Log.e("Ruta", "Ubicación del usuario no disponible")
                            }
                        },
                        icon = { Icon(Icons.Filled.Directions, "Trazar ruta") },
                        text = { Text("Trazar Ruta") },
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    ExtendedFloatingActionButton(
                        onClick = {
                            val nombre = Uri.encode(parada.nombre)
                            val rutas = Uri.encode(parada.rutas.joinToString(","))
                            val horarios = Uri.encode(parada.horarios.joinToString(","))
                            navController.navigate("detalleParada/$nombre/$rutas/$horarios")
                        },
                        icon = { Icon(Icons.Filled.Info, "Detalles") },
                        text = { Text("Ver Detalles") }
                    )
                }
            }
        }


        // Mostrar marcadores en el mapa
        LaunchedEffect(paradas.size) {
            mapView?.let { map ->
                paradas.forEach { parada ->
                    val punto = GeoPoint(parada.lat, parada.lon)
                    val marcador = Marker(map)
                    marcador.position = punto
                    marcador.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    marcador.title = parada.nombre
                    marcador.setOnMarkerClickListener { _, _ ->
                        val nombre = Uri.encode(parada.nombre)
                        val rutas = Uri.encode(parada.rutas.joinToString(","))
                        val horarios = Uri.encode(parada.horarios.joinToString(","))
                        navController.navigate("detalleParada/$nombre/$rutas/$horarios")
                        true
                    }
                    marcador.subDescription =
                        "Rutas: ${parada.rutas.joinToString(", ")}\nHorarios: ${
                            parada.horarios.joinToString(", ")
                        }"
                    map.overlays.add(marcador)
                }
                map.invalidate()
            }
        }

        // Mostrar ubicación actual
        LaunchedEffect(locationPermissionGranted.value) {
            if (locationPermissionGranted.value && mapView != null) {
                val fusedClient = LocationServices.getFusedLocationProviderClient(context)
                if (ActivityCompat.checkSelfPermission(
                        context, Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    fusedClient.lastLocation.addOnSuccessListener { location ->
                        if (location != null) {
                            val point = GeoPoint(location.latitude, location.longitude)
                            ubicacionUsuario = point // ✅ GUARDAR para usar luego
                            mapView?.controller?.animateTo(point)
                            val marker = Marker(mapView)
                            marker.position = point
                            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            marker.title = "Tu ubicación"
                            mapView?.overlays?.add(marker)
                            mapView?.invalidate()
                        }
                    }
                }
            }
        }
        travelTime?.let {
            Text(
                text = "Tiempo estimado: $it",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
                    .padding(8.dp)
            )
        }
    }
}
