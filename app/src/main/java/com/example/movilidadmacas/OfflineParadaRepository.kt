package com.example.movilidadmacas

import android.content.Context

class OfflineParadaRepository(context: Context) {
    private val paradaDao = AppDatabase.getDatabase(context).paradaDao()

    suspend fun guardarParadasLocalmente(paradas: List<Parada>) {
        val entidades = paradas.map {
            ParadaEntity(
                id = it.id,
                nombre = it.nombre,
                lat = it.lat,
                lon = it.lon,
                rutas = it.rutas.joinToString(","),

                // Convertir objeto Horarios a texto tipo: "12:00|20:00|12 min"
                horarios = "${it.horarios.inicio}|${it.horarios.fin}|${it.horarios.frecuencia}"
            )
        }
        paradaDao.deleteAllParadas()
        paradaDao.insertParadas(entidades)
    }

    suspend fun obtenerParadasLocalmente(): List<Parada> {
        return paradaDao.getAllParadas().map {
            val partes = it.horarios.split("|")
            val horariosObj = Horarios(
                inicio = partes.getOrNull(0) ?: "",
                fin = partes.getOrNull(1) ?: "",
                frecuencia = partes.getOrNull(2) ?: ""
            )

            Parada(
                id = it.id,
                nombre = it.nombre,
                lat = it.lat,
                lon = it.lon,
                rutas = it.rutas.split(","),
                horarios = horariosObj
            )
        }
    }
}
