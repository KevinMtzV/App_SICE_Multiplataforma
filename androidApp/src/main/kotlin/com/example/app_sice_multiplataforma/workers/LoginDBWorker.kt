package com.example.app_sice_multiplataforma.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.app_sice_multiplataforma.model.*
import com.example.app_sice_multiplataforma.MarsPhotosApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

class LoginDBWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    private val container = (ctx.applicationContext as MarsPhotosApplication).container

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val json = Json { ignoreUnknownKeys = true }
        val perfilJson = inputData.getString("KEY_PERFIL_JSON")
        val kardexJson = inputData.getString("KEY_KARDEX_JSON")
        val cargaJson = inputData.getString("KEY_CARGA_JSON")
        val parcialesJson = inputData.getString("KEY_PARCIALES_JSON")
        val finalesJson = inputData.getString("KEY_FINALES_JSON")

        try {
            val repo = container.snRepository

            perfilJson?.let {
                val p: ProfileStudent = json.decodeFromString(it)
                repo.savePerfil(p)
            } ?: throw Exception("JSON de perfil es nulo")

            kardexJson?.let {
                val list: List<KardexItem> = json.decodeFromString(it)
                repo.saveKardex(list)
            }

            cargaJson?.let {
                val list: List<MateriaCarga> = json.decodeFromString(it)
                repo.saveCarga(list)
            }

            parcialesJson?.let {
                val list: List<CalificacionParcial> = json.decodeFromString(it)
                repo.saveParciales(list)
            }

            finalesJson?.let {
                val list: List<CalificacionFinal> = json.decodeFromString(it)
                repo.saveFinales(list)
            }

            Result.success()
        } catch (e: Exception) {
            Result.failure(workDataOf("error" to (e.message ?: "Error desconocido en LoginDBWorker")))
        }
    }
}
