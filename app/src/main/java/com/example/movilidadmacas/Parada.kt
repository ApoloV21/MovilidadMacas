package com.example.movilidadmacas

import com.google.firebase.database.IgnoreExtraProperties

// Asegúrate de incluir el campo 'id' y usar @IgnoreExtraProperties
@IgnoreExtraProperties
data class Parada(
    var id: String = "",  // Clave de Firebase
    val nombre: String = "",
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    val rutas: List<String> = emptyList(),
    val horarios: List<String> = emptyList(),
    @Transient var isFavorita: Boolean = false
) {
    constructor() : this("") // Constructor vacío obligatorio para Firebase
}