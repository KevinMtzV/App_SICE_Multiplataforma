package com.example.app_sice_multiplataforma

import android.app.Application
import com.example.app_sice_multiplataforma.data.AppContainer
import com.example.app_sice_multiplataforma.data.DefaultAppContainer
import com.example.app_sice_multiplataforma.data.SNWMRepository
import com.example.app_sice_multiplataforma.data.WorkManagerSNWMRepository

class MarsPhotosApplication : Application() {
    /** AppContainer instance used by the rest of the classes to obtain dependencies */
    lateinit var container: AppContainer

    // REQUERIMIENTO: Declarar el repositorio de WorkManager para que el ViewModel lo vea
    lateinit var snwmRepository: SNWMRepository

    override fun onCreate() {
        super.onCreate()
        // Inicializamos el contenedor normal
        container = DefaultAppContainer(this)

        // Inicializamos el repositorio de WorkManager pasándole el contexto de la app
        snwmRepository = WorkManagerSNWMRepository(this)
    }
}