package com.example.app_sice_multiplataforma.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.ConstructedBy
import androidx.room.RoomDatabaseConstructor
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
    version = 4,
    exportSchema = false
)
@ConstructedBy(SicenetDatabaseConstructor::class)
abstract class SicenetDatabase : RoomDatabase() {
    abstract fun sicenetDao(): SicenetDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object SicenetDatabaseConstructor : RoomDatabaseConstructor<SicenetDatabase>

fun getRoomDatabase(
    builder: RoomDatabase.Builder<SicenetDatabase>
): SicenetDatabase {
    return builder
        .fallbackToDestructiveMigration(true)
        .build()
}
