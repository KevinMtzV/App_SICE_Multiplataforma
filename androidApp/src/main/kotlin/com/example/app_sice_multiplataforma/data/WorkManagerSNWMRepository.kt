package com.example.app_sice_multiplataforma.data

import android.content.Context
import androidx.work.*
import com.example.app_sice_multiplataforma.workers.LoginDBWorker
import com.example.app_sice_multiplataforma.workers.LoginWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

class WorkManagerSNWMRepository(ctx: Context) : SNWMRepository {

    private val workManager = WorkManager.getInstance(ctx)

    // REQUERIMIENTO: Monitorear el estatus del primer Worker
    override val outputWorkInfo: Flow<WorkInfo>
        get() = workManager.getWorkInfosByTagFlow("EsteQuieroMonitorear")
            .mapNotNull { workInfoList ->
                // El error 'List is empty' era porque usabas .first()
                // .firstOrNull() evita que la app truene al iniciar
                workInfoList.firstOrNull()
            }

    override fun login(m: String, p: String) {
        // Datos de entrada para el primer Worker
        val dataIn = workDataOf(
            "KEY_MATRICULA" to m,
            "KEY_PASSWORD" to p
        )

        // 1. Primer Worker: Descarga (Etiquetado para monitoreo)
        val loginRequest = OneTimeWorkRequestBuilder<LoginWorker>()
            .setInputData(dataIn)
            .addTag("EsteQuieroMonitorear") // <--- El tag que pide el maestro
            .build()

        // 2. Segundo Worker: Guarda en DB
        val saveDbRequest = OneTimeWorkRequestBuilder<LoginDBWorker>()
            .build()

        // ENCADENAMIENTO (Punto 2 de la rúbrica)
        workManager
            .beginUniqueWork(
                "SincronizacionSicenet",
                ExistingWorkPolicy.REPLACE,
                loginRequest
            )
            .then(saveDbRequest)
            .enqueue()
    }

    override fun profile() { /* Implementar similar a login */ }
    override fun cargaAcademica() { /* Implementar similar a login */ }
}