package com.example.movilidadmacas

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface RouteService {
    @GET("v2/directions/foot-walking")
    suspend fun getRoute(
        @Header("Authorization") apiKey: String,
        @Query("start") start: String,
        @Query("end") end: String
    ): RouteResponse
}

data class RouteResponse(val features: List<Feature>)
data class Feature(val geometry: Geometry, val properties: Properties)
data class Geometry(val coordinates: List<List<Double>>)
data class Properties(val segments: List<Segment>)
data class Segment(val duration: Double, val distance: Double)
