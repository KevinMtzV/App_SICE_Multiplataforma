package com.example.app_sice_multiplataforma.data.repository

import com.example.app_sice_multiplataforma.data.local.SicenetDao
import com.example.app_sice_multiplataforma.data.network.SICENETService
import com.example.app_sice_multiplataforma.model.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

interface SNRepository {
    suspend fun profile(m: String, p: String): ProfileStudent
    suspend fun getKardex(lineamiento: String = "1"): List<KardexItem>
    suspend fun getCalificacionesUnidades(): List<CalificacionParcial>
    suspend fun getCalificacionesFinales(modoEducativo: Int = 1): List<CalificacionFinal>
    suspend fun getCargaAcademica(): List<MateriaCarga>

    // Métodos DB
    suspend fun getLocalPerfil(): ProfileStudent?
    suspend fun savePerfil(perfil: ProfileStudent)
    suspend fun saveKardex(lista: List<KardexItem>)
    suspend fun getLocalKardex(): List<KardexItem>
    suspend fun saveCarga(lista: List<MateriaCarga>)
    suspend fun getLocalCarga(): List<MateriaCarga>
    suspend fun saveParciales(lista: List<CalificacionParcial>)
    suspend fun getLocalParciales(): List<CalificacionParcial>
    suspend fun saveFinales(lista: List<CalificacionFinal>)
    suspend fun getLocalFinales(): List<CalificacionFinal>

    suspend fun acceso(m: String, p: String, t: String): String
}

class DefaultSNRepository(
    private val api: SICENETService,
    private val dao: SicenetDao
) : SNRepository {

    private val json = Json { ignoreUnknownKeys = true }

    private fun limpiarJson(jsonStr: String): String {
        return jsonStr
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

    override suspend fun profile(m: String, p: String): ProfileStudent {
        val res = api.getPerfil(m, p)
        val jsonLimpio = limpiarJson(extraerJson(res, "{", "}"))
        return json.decodeFromString(jsonLimpio)
    }

    override suspend fun getKardex(lineamiento: String): List<KardexItem> {
        val res = api.getKardex(lineamiento)
        val jsonLimpio = limpiarJson(extraerJson(res, "[", "]"))
        return json.decodeFromString(jsonLimpio)
    }

    override suspend fun getCalificacionesUnidades(): List<CalificacionParcial> {
        val res = api.getCalifUnidades()
        val jsonLimpio = limpiarJson(extraerJson(res, "[", "]"))
        return json.decodeFromString(jsonLimpio)
    }

    override suspend fun getCalificacionesFinales(modoEducativo: Int): List<CalificacionFinal> {
        val res = api.getCalifFinales(modoEducativo)
        val jsonLimpio = limpiarJson(extraerJson(res, "[", "]"))
        return json.decodeFromString(jsonLimpio)
    }

    override suspend fun getCargaAcademica(): List<MateriaCarga> {
        val res = api.getCargaAcademica()
        val jsonLimpio = limpiarJson(extraerJson(res, "[", "]"))
        return json.decodeFromString(jsonLimpio)
    }

    override suspend fun getLocalPerfil() = dao.getPerfil()
    override suspend fun savePerfil(perfil: ProfileStudent) {
        dao.deletePerfil()
        dao.insertPerfil(perfil)
    }
    override suspend fun saveKardex(lista: List<KardexItem>) = dao.syncKardex(lista)
    override suspend fun getLocalKardex() = dao.getAllKardex()
    override suspend fun saveCarga(lista: List<MateriaCarga>) = dao.syncCarga(lista)
    override suspend fun getLocalCarga() = dao.getCarga()
    override suspend fun saveParciales(lista: List<CalificacionParcial>) = dao.syncParciales(lista)
    override suspend fun getLocalParciales() = dao.getParciales()
    override suspend fun saveFinales(lista: List<CalificacionFinal>) = dao.syncFinales(lista)
    override suspend fun getLocalFinales() = dao.getFinales()

    override suspend fun acceso(m: String, p: String, t: String) = api.acceso(m, p, t)
}
