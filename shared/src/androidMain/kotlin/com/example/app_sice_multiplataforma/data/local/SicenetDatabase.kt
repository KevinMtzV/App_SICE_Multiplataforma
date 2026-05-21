@file:JvmName("AndroidSicenetDatabase")
package com.example.app_sice_multiplataforma.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun getDatabaseBuilder(ctx: Context): RoomDatabase.Builder<SicenetDatabase> {
    val appContext = ctx.applicationContext
    val dbFile = appContext.getDatabasePath("sicenet_database")
    return Room.databaseBuilder<SicenetDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}
