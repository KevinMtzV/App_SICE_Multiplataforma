package com.example.app_sice_multiplataforma.data

import android.content.Context
import com.example.app_sice_multiplataforma.data.local.SicenetDatabase
import com.example.app_sice_multiplataforma.network.MarsApiService
import com.example.app_sice_multiplataforma.network.SICENETWService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.util.concurrent.TimeUnit

interface AppContainer {
    val marsPhotosRepository: MarsPhotosRepository
    val snRepository: SNRepository          // Red
    val dbLocalRepository: SNRepository     // Local (Room)
}

class DefaultAppContainer(private val applicationContext: Context) : AppContainer {

    private val baseUrl = "https://android-kotlin-fun-mars-server.appspot.com/"
    private val baseUrlSN = "https://sicenet.surguanajuato.tecnm.mx/"

    // --- 1. BASE DE DATOS (ROOM) ---
    private val database: SicenetDatabase by lazy {
        SicenetDatabase.getDatabase(applicationContext)
    }

    // --- 2. CLIENTE HTTP ---
    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(AddCookiesInterceptor(applicationContext))
            .addInterceptor(ReceivedCookiesInterceptor(applicationContext))
            .connectTimeout(120, TimeUnit.SECONDS) // 30 segundos de paciencia para conectar
            .readTimeout(120, TimeUnit.SECONDS)    // 30 segundos para recibir datos
            .writeTimeout(120, TimeUnit.SECONDS)   // 30 segundos para enviar datos
            .build()
    }

    // --- 3. RETROFIT BUILDERS ---
    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrl)
        .build()

    private val retrofitSN: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrlSN)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .addConverterFactory(SimpleXmlConverterFactory.createNonStrict())
        .build()

    // --- 4. SERVICES ---
    private val retrofitService: MarsApiService by lazy {
        retrofit.create(MarsApiService::class.java)
    }

    private val retrofitServiceSN: SICENETWService by lazy {
        retrofitSN.create(SICENETWService::class.java)
    }

    // --- 5. REPOSITORIOS ---
    override val marsPhotosRepository: NetworkMarsPhotosRepository by lazy {
        NetworkMarsPhotosRepository(retrofitService)
    }

    override val snRepository: NetworSNRepository by lazy {
        NetworSNRepository(retrofitServiceSN)
    }

    override val dbLocalRepository: SNRepository by lazy {
        DBLocalSNRepository(database.sicenetDao())
    }
}