package com.example.movilidadmacas

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "paradas")
data class ParadaEntity(
    @PrimaryKey val id: String,
    val nombre: String,
    val lat: Double,
    val lon: Double,
    val rutas: String,   // serializado como CSV: "R1,R2"
    val horarios: String // serializado como "inicio|fin|frecuencia"
)
