package com.example.app_sice_multiplataforma.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.app_sice_multiplataforma.MarsPhotosApplication
import kotlinx.coroutines.delay
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class LoginWorker(ctx: Context, params: WorkerParameters)
    : CoroutineWorker(ctx, params) {

    private val container = (ctx.applicationContext as MarsPhotosApplication).container

    override suspend fun doWork(): Result {
        val matricula = inputData.getString("KEY_MATRICULA")?.uppercase() ?: ""
        val password = inputData.getString("KEY_PASSWORD") ?: ""
        val tipoUsuario = "ALUMNO"

        var currentAttempt = 0
        val maxAttempts = 3

        while (currentAttempt < maxAttempts) {
            try {
                Log.d("LoginWorker", "Intento $currentAttempt de login para $matricula")
                val loginResponse = container.snRepository.acceso(matricula, password, tipoUsuario)
                Log.d("LoginWorker", "Respuesta login: $loginResponse")
                
                if (loginResponse.contains("true")) {
                    Log.d("LoginWorker", "Login exitoso, descargando datos...")
                    val perfil = container.snRepository.profile(matricula, password)
                    val kardex = container.snRepository.getKardex("1")
                    val carga = container.snRepository.getCargaAcademica()
                    val parciales = container.snRepository.getCalificacionesUnidades()
                    val finales = container.snRepository.getCalificacionesFinales(1)

                    val json = Json { ignoreUnknownKeys = true }
                    return Result.success(workDataOf(
                        "KEY_PERFIL_JSON" to json.encodeToString(perfil),
                        "KEY_KARDEX_JSON" to json.encodeToString(kardex),
                        "KEY_CARGA_JSON" to json.encodeToString(carga),
                        "KEY_PARCIALES_JSON" to json.encodeToString(parciales),
                        "KEY_FINALES_JSON" to json.encodeToString(finales)
                    ))
                } else if (loginResponse.contains("false") || loginResponse.contains("No existe el usuario")) {
                    Log.w("LoginWorker", "Credenciales incorrectas: $loginResponse")
                    return Result.failure(workDataOf("error" to "Credenciales incorrectas. Verifica tu número de control y contraseña."))
                } else {
                    Log.w("LoginWorker", "Respuesta inesperada del servidor: $loginResponse")
                    return Result.failure(workDataOf("error" to "Servidor del Tec: Respuesta no reconocida."))
                }
            } catch (e: Exception) {
                Log.e("LoginWorker", "Error en intento $currentAttempt", e)
                currentAttempt++
                if (currentAttempt < maxAttempts) {
                    delay(2000)
                } else {
                    return Result.failure(workDataOf("error" to "Error de red: ${e.message ?: "Sin conexión con el servidor"}"))
                }
            }
        }
        return Result.failure(workDataOf("error" to "Error desconocido tras varios intentos"))
    }
}
