package com.example.movilidadmacas

import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

object FavoritosRepository {

    // Verifica si una parada está en favoritos para un usuario
    suspend fun esFavorita(userId: String, paradaId: String): Boolean {
        val ref = FirebaseDatabase.getInstance()
            .getReference("favoritos")
            .child(userId)
            .child(paradaId)

        val snapshot = ref.get().await()
        return snapshot.exists()
    }

    // Agrega una parada a favoritos
    suspend fun agregarAFavoritos(userId: String, paradaId: String) {
        val ref = FirebaseDatabase.getInstance()
            .getReference("favoritos")
            .child(userId)
            .child(paradaId)

        ref.setValue(true).await()
    }

    // Elimina una parada de favoritos
    suspend fun eliminarDeFavoritos(userId: String, paradaId: String) {
        val ref = FirebaseDatabase.getInstance()
            .getReference("favoritos")
            .child(userId)
            .child(paradaId)

        ref.removeValue().await()
    }

    // Devuelve una lista con los IDs de las paradas favoritas del usuario
    suspend fun obtenerFavoritos(userId: String): List<String> {
        val ref = FirebaseDatabase.getInstance()
            .getReference("favoritos")
            .child(userId)

        val snapshot = ref.get().await()
        return snapshot.children.mapNotNull { it.key }
    }

    // Devuelve una lista de Parada completas desde el nodo /paradas
    suspend fun obtenerParadasFavoritas(userId: String): List<Parada> {
        val paradaRef = FirebaseDatabase.getInstance().getReference("paradas")
        val favoritosIds = obtenerFavoritos(userId)
        val snapshot = paradaRef.get().await()

        return favoritosIds.mapNotNull { id ->
            snapshot.child(id).getValue(Parada::class.java)
        }
    }
}
