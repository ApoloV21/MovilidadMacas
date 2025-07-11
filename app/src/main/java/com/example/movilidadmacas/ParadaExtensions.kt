package com.example.movilidadmacas

fun Parada.toEntity(): ParadaEntity {
    return ParadaEntity(
        id = id,
        nombre = nombre,
        lat = lat,
        lon = lon,
        rutas = rutas.joinToString(";"),
        horarios = "${horarios.inicio}|${horarios.fin}|${horarios.frecuencia}"
    )
}

fun ParadaEntity.toParada(): Parada {
    val partesHorarios = horarios.split("|")
    val horariosObj = Horarios(
        inicio = partesHorarios.getOrNull(0) ?: "",
        fin = partesHorarios.getOrNull(1) ?: "",
        frecuencia = partesHorarios.getOrNull(2) ?: ""
    )

    return Parada(
        id = id,
        nombre = nombre,
        lat = lat,
        lon = lon,
        rutas = rutas.split(";").filter { it.isNotBlank() },
        horarios = horariosObj
    )
}
