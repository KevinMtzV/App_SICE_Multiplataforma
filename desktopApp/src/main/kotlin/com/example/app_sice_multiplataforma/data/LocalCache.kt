package com.example.app_sice_multiplataforma.data

import com.example.app_sice_multiplataforma.model.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class LocalCache {

    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }
    private val cacheFile: File

    init {
        val appDir = File(System.getProperty("user.home"), ".sice_app")
        appDir.mkdirs()
        cacheFile = File(appDir, "cache.json")
    }

    private fun load(): CacheData {
        return if (cacheFile.exists()) {
            try {
                json.decodeFromString<CacheData>(cacheFile.readText())
            } catch (e: Exception) {
                CacheData()
            }
        } else {
            CacheData()
        }
    }

    private fun save(data: CacheData) {
        cacheFile.writeText(json.encodeToString(data))
    }

    fun getPerfil(): ProfileStudent? = load().perfil

    fun insertPerfil(perfil: ProfileStudent) {
        val data = load()
        save(data.copy(perfil = perfil, ultimaSincro = fechaActual()))
    }

    fun getKardex(): List<KardexItem> = load().kardex

    fun insertKardex(lista: List<KardexItem>) {
        save(load().copy(kardex = lista, ultimaSincro = fechaActual()))
    }

    fun getCarga(): List<MateriaCarga> = load().carga

    fun insertCarga(lista: List<MateriaCarga>) {
        save(load().copy(carga = lista, ultimaSincro = fechaActual()))
    }

    fun getParciales(): List<CalificacionParcial> = load().parciales

    fun insertParciales(lista: List<CalificacionParcial>) {
        save(load().copy(parciales = lista, ultimaSincro = fechaActual()))
    }

    fun getFinales(): List<CalificacionFinal> = load().finales

    fun insertFinales(lista: List<CalificacionFinal>) {
        save(load().copy(finales = lista, ultimaSincro = fechaActual()))
    }

    fun getUltimaSincro(): String = load().ultimaSincro

    fun clearAll() {
        cacheFile.delete()
    }

    private fun fechaActual(): String {
        return SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
    }
}
