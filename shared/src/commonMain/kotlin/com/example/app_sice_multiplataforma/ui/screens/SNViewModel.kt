package com.example.app_sice_multiplataforma.ui.screens

import com.example.app_sice_multiplataforma.model.*
import kotlinx.coroutines.flow.StateFlow

sealed interface SNUiState {
    data class Success(
        val data: ProfileStudent,
        val kardex: List<KardexItem> = emptyList(),
        val califUnidades: List<CalificacionParcial> = emptyList(),
        val califFinales: List<CalificacionFinal> = emptyList(),
        val cargaAcademica: List<MateriaCarga> = emptyList(),
        val esOffline: Boolean = false,
        val ultimaSincro: String = ""
    ) : SNUiState
    data class Error(val mensaje: String = "", val esRedBlockeada: Boolean = false) : SNUiState
    object Loading : SNUiState
    object Idle : SNUiState
}

/**
 * Interface base para el ViewModel que será consumida por las pantallas.
 * Esto permite que Android use su ViewModel de arquitectura y Desktop use uno simple,
 * pero ambos compartan el mismo estado para las pantallas.
 */
interface ISNViewModel {
    val uiState: StateFlow<SNUiState>
    fun loginYConsultarPerfil(matricula: String, contrasenia: String, tipo: String)
    fun consultarKardex()
    fun consultarCargaAcademica()
    fun consultarCalificacionesUnidades()
    fun consultarCalificacionesFinales()
    fun logout()
}
