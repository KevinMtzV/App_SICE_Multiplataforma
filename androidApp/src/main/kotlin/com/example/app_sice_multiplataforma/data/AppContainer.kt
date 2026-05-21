package com.example.app_sice_multiplataforma.data

import android.content.Context
import com.example.app_sice_multiplataforma.data.local.SicenetDatabase
import com.example.app_sice_multiplataforma.data.local.getDatabaseBuilder
import com.example.app_sice_multiplataforma.data.network.SICENETService
import com.example.app_sice_multiplataforma.data.network.createHttpClient
import com.example.app_sice_multiplataforma.data.repository.DefaultSNRepository
import com.example.app_sice_multiplataforma.data.repository.SNRepository
import io.ktor.client.engine.okhttp.*
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

interface AppContainer {
    val snRepository: SNRepository
}

class DefaultAppContainer(private val applicationContext: Context) : AppContainer {

    // --- 1. BASE DE DATOS (ROOM) ---
    private val database: SicenetDatabase by lazy {
        val builder = getDatabaseBuilder(applicationContext)
        builder.fallbackToDestructiveMigration(true)
        builder.build()
    }

    // --- 2. CLIENTE HTTP (KTOR con OKHTTP) ---
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AddCookiesInterceptor(applicationContext))
        .addInterceptor(ReceivedCookiesInterceptor(applicationContext))
        .connectTimeout(120, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .build()

    private val ktorClient = createHttpClient(OkHttp.create {
        preconfigured = okHttpClient
    })

    // --- 3. SERVICIOS ---
    private val sicenetService = SICENETService(ktorClient)

    // --- 4. REPOSITORIO ---
    override val snRepository: SNRepository by lazy {
        DefaultSNRepository(sicenetService, database.sicenetDao())
    }
}
