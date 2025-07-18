package com.example.movilidadmacas

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ParadaEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun paradaDao(): ParadaDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "paradas_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
