package com.example.app_sice_multiplataforma.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.app_sice_multiplataforma.MarsPhotosApplication
import com.google.gson.Gson
import kotlinx.coroutines.delay

class LoginWorker(ctx: Context, params: WorkerParameters)
    : CoroutineWorker(ctx, params) {

    private val container = (ctx.applicationContext as MarsPhotosApplication).container

    override suspend fun doWork(): Result {
        val matricula = inputData.getString("KEY_MATRICULA") ?: ""
        val password = inputData.getString("KEY_PASSWORD") ?: ""
        val tipoUsuario = "ALUMNO"

        var currentAttempt = 0
        val maxAttempts = 3 // Intentaremos 3 veces si hay error de red

        while (currentAttempt < maxAttempts) {
            try {
                // 1. Intentar Acceso
                val loginResponse = container.snRepository.acceso(matricula, password, tipoUsuario)
                Log.d("SICENET_DEBUG", "Intento ${currentAttempt + 1} - Respuesta Login: $loginResponse")

                if (loginResponse.contains("true")) {
                    // 2. Si el login es exitoso, descargar TODO lo demás de una vez
                    val perfil = container.snRepository.profile(matricula, password)
                    val kardex = container.snRepository.getKardex("1")
                    val carga = container.snRepository.getCargaAcademica()
                    val parciales = container.snRepository.getCalificacionesUnidades()
                    val finales = container.snRepository.getCalificacionesFinales(1)

                    val gson = Gson()
                    return Result.success(workDataOf(
                        "KEY_PERFIL_JSON" to gson.toJson(perfil),
                        "KEY_KARDEX_JSON" to gson.toJson(kardex),
                        "KEY_CARGA_JSON" to gson.toJson(carga),
                        "KEY_PARCIALES_JSON" to gson.toJson(parciales),
                        "KEY_FINALES_JSON" to gson.toJson(finales)
                    ))
                } else {
                    return Result.failure(workDataOf("error" to "Credenciales incorrectas"))
                }

            } catch (e: Exception) {
                currentAttempt++
                Log.e("SICENET_DEBUG", "Error de red (Intento $currentAttempt): ${e.message}")
                if (currentAttempt < maxAttempts) {
                    delay(2000) // Esperar 2 segundos antes de reintentar
                }
            }
        }

        return Result.failure(workDataOf("error" to "No se pudo conectar con el servidor tras $maxAttempts intentos"))
    }
}