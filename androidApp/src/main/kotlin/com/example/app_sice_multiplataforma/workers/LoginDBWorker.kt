package com.example.app_sice_multiplataforma.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.app_sice_multiplataforma.model.KardexItem
import com.example.app_sice_multiplataforma.model.MateriaCarga
import com.example.app_sice_multiplataforma.model.ProfileStudent
import com.example.app_sice_multiplataforma.MarsPhotosApplication
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class LoginDBWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    private val container = (ctx.applicationContext as MarsPhotosApplication).container

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val gson = Gson()
        val perfilJson = inputData.getString("KEY_PERFIL_JSON")
        val kardexJson = inputData.getString("KEY_KARDEX_JSON")
        val cargaJson = inputData.getString("KEY_CARGA_JSON")

        try {
            val localRepo = container.dbLocalRepository
            val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

            // Guardar Perfil (OBLIGATORIO)
            perfilJson?.let {
                val p = gson.fromJson(it, ProfileStudent::class.java)
                localRepo.insertPerfil(p)
            }

            // Guardar Kardex
            kardexJson?.let {
                val list: List<KardexItem> = gson.fromJson(it, object : TypeToken<List<KardexItem>>() {}.type)
                localRepo.insertKardex(list.map { k -> k.copy(lastUpdated = fecha) })
            }

            // Guardar Carga
            cargaJson?.let {
                val list: List<MateriaCarga> = gson.fromJson(it, object : TypeToken<List<MateriaCarga>>() {}.type)
                localRepo.insertCarga(list.map { c -> c.copy(lastUpdated = fecha) })
            }

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}