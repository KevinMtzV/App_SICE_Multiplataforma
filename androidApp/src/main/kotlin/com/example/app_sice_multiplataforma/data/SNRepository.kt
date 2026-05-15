package com.example.app_sice_multiplataforma.data

import com.example.app_sice_multiplataforma.data.local.SicenetDao
import com.example.app_sice_multiplataforma.model.CalificacionFinal
import com.example.app_sice_multiplataforma.model.CalificacionParcial
import com.example.app_sice_multiplataforma.model.KardexItem
import com.example.app_sice_multiplataforma.model.MateriaCarga
import com.example.app_sice_multiplataforma.model.ProfileStudent
import com.example.app_sice_multiplataforma.model.Usuario
import com.example.app_sice_multiplataforma.network.SICENETWService
import com.example.app_sice_multiplataforma.network.bodyAlumno
import com.example.app_sice_multiplataforma.network.bodyCalifFinales
import com.example.app_sice_multiplataforma.network.bodyCargaAcademica
import com.example.app_sice_multiplataforma.network.bodyKardex
import com.example.app_sice_multiplataforma.network.bodyVacio
import com.example.app_sice_multiplataforma.network.bodyacceso
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.RequestBody.Companion.toRequestBody

interface SNRepository {
    // --- Métodos de Red ---
    suspend fun acceso(m: String, p: String, t: String): String
    suspend fun accesoObjeto(m: String, p: String): Usuario
    suspend fun profile(m: String, p: String): ProfileStudent
    suspend fun getKardex(lineamiento: String = "1"): List<KardexItem>
    suspend fun getCalificacionesUnidades(): List<CalificacionParcial>
    suspend fun getCalificacionesFinales(modoEducativo: Int = 1): List<CalificacionFinal>
    suspend fun getCargaAcademica(): List<MateriaCarga>

    // --- Métodos de Base de Datos ---
    suspend fun getPerfil(): ProfileStudent?
    suspend fun insertPerfil(perfil: ProfileStudent)
    suspend fun insertKardex(lista: List<KardexItem>)
    suspend fun insertCarga(lista: List<MateriaCarga>)
    suspend fun insertParciales(lista: List<CalificacionParcial>)
    suspend fun insertFinales(lista: List<CalificacionFinal>)
}

class NetworSNRepository(
    private val snApiService: SICENETWService
) : SNRepository {

    private fun limpiarJson(json: String): String {
        return json
            .replace("&quot;", "\"")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("\\\"", "\"")
            .trim()
    }

    private fun extraerJson(xmlString: String, inicio: String, fin: String): String {
        val startIndex = xmlString.indexOf(inicio)
        val endIndex = xmlString.lastIndexOf(fin)
        return if (startIndex != -1 && endIndex != -1) {
            xmlString.substring(startIndex, endIndex + 1)
        } else {
            if (inicio == "[") "[]" else "{}"
        }
    }

    override suspend fun acceso(m: String, p: String, t: String): String {
        return try {
            val res = snApiService.acceso(bodyacceso.format(m, p, t).toRequestBody())
            res.string()
        } catch (e: Exception) { "Error: ${e.message}" }
    }

    override suspend fun accesoObjeto(m: String, p: String): Usuario = Usuario(matricula = m)

    override suspend fun profile(m: String, p: String): ProfileStudent {
        return try {
            val response = snApiService.getPerfil(bodyAlumno.format(m, p).toRequestBody())
            val jsonLimpio = limpiarJson(extraerJson(response.string(), "{", "}"))
            if (!jsonLimpio.contains("{")) throw Exception("JSON inválido")
            Gson().fromJson(jsonLimpio, ProfileStudent::class.java)
        } catch (e: Exception) {
            // CORRECCIÓN: Usamos "Inactivo" (String) en lugar de false (Boolean)
            ProfileStudent("Error", m, "Inactivo", "Desconocida")
        }
    }

    override suspend fun getKardex(lineamiento: String): List<KardexItem> {
        return try {
            val response = snApiService.getKardex(bodyKardex.format(lineamiento).toRequestBody())
            val jsonLimpio = limpiarJson(extraerJson(response.string(), "[", "]"))
            if (jsonLimpio == "[]" || !jsonLimpio.contains("{")) return emptyList()
            val itemType = object : TypeToken<List<KardexItem>>() {}.type
            Gson().fromJson(jsonLimpio, itemType)
        } catch (e: Exception) { emptyList() }
    }

    override suspend fun getCalificacionesUnidades(): List<CalificacionParcial> {
        return try {
            val res = snApiService.getCalifUnidades(bodyVacio.toRequestBody())
            val jsonLimpio = limpiarJson(extraerJson(res.string(), "[", "]"))
            if (jsonLimpio == "[]" || !jsonLimpio.contains("{")) return emptyList()
            val itemType = object : TypeToken<List<CalificacionParcial>>() {}.type
            Gson().fromJson(jsonLimpio, itemType)
        } catch (e: Exception) { emptyList() }
    }

    override suspend fun getCalificacionesFinales(modoEducativo: Int): List<CalificacionFinal> {
        return try {
            val response = snApiService.getCalifFinales(bodyCalifFinales.format(modoEducativo).toRequestBody())
            val jsonLimpio = limpiarJson(extraerJson(response.string(), "[", "]"))
            if (jsonLimpio == "[]" || !jsonLimpio.contains("{")) return emptyList()
            val itemType = object : TypeToken<List<CalificacionFinal>>() {}.type
            Gson().fromJson(jsonLimpio, itemType)
        } catch (e: Exception) { emptyList() }
    }

    override suspend fun getCargaAcademica(): List<MateriaCarga> {
        return try {
            val response = snApiService.getCargaAcademica(bodyCargaAcademica.toRequestBody())
            val jsonLimpio = limpiarJson(extraerJson(response.string(), "[", "]"))
            if (jsonLimpio == "[]" || !jsonLimpio.contains("{")) return emptyList()
            val itemType = object : TypeToken<List<MateriaCarga>>() {}.type
            Gson().fromJson(jsonLimpio, itemType)
        } catch (e: Exception) { emptyList() }
    }

    // Métodos DB vacíos para la Red
    override suspend fun getPerfil(): ProfileStudent? = null
    override suspend fun insertPerfil(perfil: ProfileStudent) {}
    override suspend fun insertKardex(lista: List<KardexItem>) {}
    override suspend fun insertCarga(lista: List<MateriaCarga>) {}
    override suspend fun insertParciales(lista: List<CalificacionParcial>) {}
    override suspend fun insertFinales(lista: List<CalificacionFinal>) {}
}

class DBLocalSNRepository(private val dao: SicenetDao) : SNRepository {

    // --- Red---
    override suspend fun acceso(m: String, p: String, t: String): String = ""
    override suspend fun accesoObjeto(m: String, p: String): Usuario = Usuario("")
    override suspend fun profile(m: String, p: String): ProfileStudent = throw NotImplementedError()

    // --- Consultas Locales ---
    override suspend fun getPerfil(): ProfileStudent? = dao.getPerfil()
    override suspend fun getKardex(lineamiento: String): List<KardexItem> = dao.getAllKardex()
    override suspend fun getCalificacionesUnidades(): List<CalificacionParcial> = dao.getParciales()
    override suspend fun getCalificacionesFinales(modoEducativo: Int): List<CalificacionFinal> = dao.getFinales()
    override suspend fun getCargaAcademica(): List<MateriaCarga> = dao.getCarga()

    // --- Inserciones Locales---
    override suspend fun insertPerfil(perfil: ProfileStudent) {
        dao.deletePerfil() // Aseguramos que solo haya un perfil
        dao.insertPerfil(perfil)
    }

    // Utilizamos los métodos de sincronización que borran antes de insertar
    override suspend fun insertKardex(lista: List<KardexItem>) = dao.syncKardex(lista)

    override suspend fun insertCarga(lista: List<MateriaCarga>) = dao.syncCarga(lista)

    override suspend fun insertParciales(lista: List<CalificacionParcial>) = dao.syncParciales(lista)

    override suspend fun insertFinales(lista: List<CalificacionFinal>) = dao.syncFinales(lista)
}