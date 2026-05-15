package com.example.app_sice_multiplataforma.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.app_sice_multiplataforma.model.CalificacionFinal
import com.example.app_sice_multiplataforma.model.CalificacionParcial
import com.example.app_sice_multiplataforma.model.KardexItem
import com.example.app_sice_multiplataforma.model.MateriaCarga
import com.example.app_sice_multiplataforma.model.ProfileStudent

@Database(
    entities = [
        KardexItem::class,
        MateriaCarga::class,
        CalificacionParcial::class,
        CalificacionFinal::class,
        ProfileStudent::class
    ],
    version = 3,
    exportSchema = false
)
abstract class SicenetDatabase : RoomDatabase() {
    abstract fun sicenetDao(): SicenetDao

    companion object {
        @Volatile
        private var Instance: SicenetDatabase? = null

        fun getDatabase(context: Context): SicenetDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    SicenetDatabase::class.java,
                    "sicenet_database"
                )
                    .fallbackToDestructiveMigration()
                    .build().also { Instance = it }
            }
        }
    }
}