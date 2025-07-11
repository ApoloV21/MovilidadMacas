package com.example.movilidadmacas

import androidx.room.*

@Dao
interface ParadaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParadas(paradas: List<ParadaEntity>)

    @Query("SELECT * FROM paradas")
    suspend fun getAllParadas(): List<ParadaEntity>

    @Query("DELETE FROM paradas")
    suspend fun deleteAllParadas()
}
