package com.example.app_sice_multiplataforma.data.local

import android.database.Cursor
import androidx.room.*
import com.example.app_sice_multiplataforma.model.CalificacionFinal
import com.example.app_sice_multiplataforma.model.CalificacionParcial
import com.example.app_sice_multiplataforma.model.KardexItem
import com.example.app_sice_multiplataforma.model.MateriaCarga
import com.example.app_sice_multiplataforma.model.ProfileStudent

@Dao
interface SicenetDao {

    // --- PERFIL ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerfil(perfil: ProfileStudent)

    @Query("SELECT * FROM perfil LIMIT 1")
    suspend fun getPerfil(): ProfileStudent?

    @Query("DELETE FROM perfil")
    suspend fun deletePerfil()

    // --- KÁRDEX ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKardex(items: List<KardexItem>)

    @Query("SELECT * FROM kardex")
    suspend fun getAllKardex(): List<KardexItem>

    @Query("DELETE FROM kardex")
    suspend fun deleteKardex()

    // --- CARGA ACADÉMICA ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCarga(items: List<MateriaCarga>)

    @Query("SELECT * FROM carga_academica")
    suspend fun getCarga(): List<MateriaCarga>

    @Query("DELETE FROM carga_academica")
    suspend fun deleteCarga()

    // --- CALIFICACIONES PARCIALES ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParciales(items: List<CalificacionParcial>)

    @Query("SELECT * FROM parciales")
    suspend fun getParciales(): List<CalificacionParcial>

    @Query("DELETE FROM parciales")
    suspend fun deleteParciales()

    // --- CALIFICACIONES FINALES ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFinales(items: List<CalificacionFinal>)

    @Query("SELECT * FROM finales")
    suspend fun getFinales(): List<CalificacionFinal>

    @Query("DELETE FROM finales")
    suspend fun deleteFinales()

    // --- CONSULTAS PARA EL CONTENT PROVIDER ---
    @Query("SELECT * FROM kardex")
    fun getKardexCursor(): Cursor

    @Query("SELECT * FROM carga_academica")
    fun getCargaCursor(): Cursor

    @Query("SELECT * FROM parciales")
    fun getParcialesCursor(): Cursor

    @Query("SELECT * FROM finales")
    fun getFinalesCursor(): Cursor

    // --- FUNCIONES DE SINCRONIZACIÓN (EVITA DUPLICADOS) ---
    // Estas funciones aseguran que la tabla se limpie antes de insertar lo nuevo

    @Transaction
    suspend fun syncKardex(items: List<KardexItem>) {
        deleteKardex()
        insertKardex(items)
    }

    @Transaction
    suspend fun syncCarga(items: List<MateriaCarga>) {
        deleteCarga()
        insertCarga(items)
    }

    @Transaction
    suspend fun syncParciales(items: List<CalificacionParcial>) {
        deleteParciales()
        insertParciales(items)
    }

    @Transaction
    suspend fun syncFinales(items: List<CalificacionFinal>) {
        deleteFinales()
        insertFinales(items)
    }
}