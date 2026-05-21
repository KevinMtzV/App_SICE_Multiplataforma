@file:JvmName("JvmSicenetDatabase")
package com.example.app_sice_multiplataforma.data.local

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import java.io.File

fun getDatabaseBuilder(): RoomDatabase.Builder<SicenetDatabase> {
    val dbFile = File(System.getProperty("java.io.tmpdir"), "sicenet_database.db")
    return Room.databaseBuilder<SicenetDatabase>(
        name = dbFile.absolutePath,
    ).setDriver(BundledSQLiteDriver())
}
