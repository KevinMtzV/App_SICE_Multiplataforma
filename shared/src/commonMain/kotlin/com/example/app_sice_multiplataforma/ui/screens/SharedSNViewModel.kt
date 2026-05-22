package com.example.app_sice_multiplataforma.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_sice_multiplataforma.data.repository.SNRepository
import com.example.app_sice_multiplataforma.model.*
import com.example.app_sice_multiplataforma.util.ConnectivityChecker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class SharedSNViewModel(
    private val snRepository: SNRepository,
    private val connectivityChecker: ConnectivityChecker,
    private val getCurrentDate: () -> String
) : ViewModel(), ISNViewModel {

    private val _uiState = MutableStateFlow<SNUiState>(SNUiState.Idle)
    override val uiState: StateFlow<SNUiState> = _uiState.asStateFlow()

    override fun loginYConsultarPerfil(matricula: String, contrasenia: String, tipo: String) {
        if (matricula.isBlank() || contrasenia.isBlank()) {
            _uiState.value = SNUiState.Error("Ingresa tu matrícula y contraseña.")
            return
        }
        _uiState.value = SNUiState.Loading
        viewModelScope.launch {
            if (!connectivityChecker.hasInternet()) {
                val local = snRepository.getLocalPerfil()
                _uiState.value = if (local != null) {
                    SNUiState.Success(
                        data = local,
                        kardex = snRepository.getLocalKardex(),
                        califUnidades = snRepository.getLocalParciales(),
                        califFinales = snRepository.getLocalFinales(),
                        cargaAcademica = snRepository.getLocalCarga(),
                        esOffline = true,
                        ultimaSincro = "Carga Local (Offline)"
                    )
                } else {
                    SNUiState.Error("Sin conexión y sin datos locales.")
                }
                return@launch
            }

            try {
                val loginRes = withContext(Dispatchers.IO) { snRepository.acceso(matricula, contrasenia, tipo) }
                if (loginRes.contains("true")) {
                    val perfil = withContext(Dispatchers.IO) { snRepository.profile(matricula, contrasenia) }
                    snRepository.savePerfil(perfil)

                    val kardex = withContext(Dispatchers.IO) { snRepository.getKardex() }
                    val carga = withContext(Dispatchers.IO) { snRepository.getCargaAcademica() }
                    val parciales = withContext(Dispatchers.IO) { snRepository.getCalificacionesUnidades() }
                    val finales = withContext(Dispatchers.IO) { snRepository.getCalificacionesFinales() }

                    snRepository.saveKardex(kardex)
                    snRepository.saveCarga(carga)
                    snRepository.saveParciales(parciales)
                    snRepository.saveFinales(finales)

                    _uiState.value = SNUiState.Success(
                        perfil, kardex, parciales, finales, carga, false, getCurrentDate()
                    )
                } else {
                    _uiState.value = SNUiState.Error("Credenciales incorrectas.")
                }
            } catch (e: Exception) {
                _uiState.value = SNUiState.Error("Error: ${e.message}")
            }
        }
    }

    override fun consultarKardex() {
        val cur = _uiState.value as? SNUiState.Success ?: return
        viewModelScope.launch {
            if (connectivityChecker.hasInternet()) {
                try {
                    val res = withContext(Dispatchers.IO) { snRepository.getKardex() }
                    snRepository.saveKardex(res)
                    _uiState.value = cur.copy(kardex = res, esOffline = false)
                } catch (e: Exception) {
                    // Manejar error
                }
            } else {
                _uiState.value = cur.copy(kardex = snRepository.getLocalKardex(), esOffline = true, ultimaSincro = getCurrentDate())
            }
        }
    }

    override fun consultarCargaAcademica() {
        val cur = _uiState.value as? SNUiState.Success ?: return
        viewModelScope.launch {
            if (connectivityChecker.hasInternet()) {
                try {
                    val res = withContext(Dispatchers.IO) { snRepository.getCargaAcademica() }
                    snRepository.saveCarga(res)
                    _uiState.value = cur.copy(cargaAcademica = res, esOffline = false)
                } catch (e: Exception) {
                    // Manejar error
                }
            } else {
                _uiState.value = cur.copy(cargaAcademica = snRepository.getLocalCarga(), esOffline = true, ultimaSincro = getCurrentDate())
            }
        }
    }

    override fun consultarCalificacionesUnidades() {
        val cur = _uiState.value as? SNUiState.Success ?: return
        viewModelScope.launch {
            if (connectivityChecker.hasInternet()) {
                try {
                    val res = withContext(Dispatchers.IO) { snRepository.getCalificacionesUnidades() }
                    snRepository.saveParciales(res)
                    _uiState.value = cur.copy(califUnidades = res, esOffline = false)
                } catch (e: Exception) {
                    // Manejar error
                }
            } else {
                _uiState.value = cur.copy(califUnidades = snRepository.getLocalParciales(), esOffline = true, ultimaSincro = getCurrentDate())
            }
        }
    }

    override fun consultarCalificacionesFinales() {
        val cur = _uiState.value as? SNUiState.Success ?: return
        viewModelScope.launch {
            if (connectivityChecker.hasInternet()) {
                try {
                    val res = withContext(Dispatchers.IO) { snRepository.getCalificacionesFinales() }
                    snRepository.saveFinales(res)
                    _uiState.value = cur.copy(califFinales = res, esOffline = false)
                } catch (e: Exception) {
                    // Manejar error
                }
            } else {
                _uiState.value = cur.copy(califFinales = snRepository.getLocalFinales(), esOffline = true, ultimaSincro = getCurrentDate())
            }
        }
    }

    fun cargarDatosDesdeLocal() {
        _uiState.value = SNUiState.Loading
        viewModelScope.launch {
            try {
                val perfilLocal = withContext(Dispatchers.IO) { snRepository.getLocalPerfil() }
                if (perfilLocal != null) {
                    _uiState.value = SNUiState.Success(
                        data = perfilLocal,
                        kardex = snRepository.getLocalKardex(),
                        cargaAcademica = snRepository.getLocalCarga(),
                        califUnidades = snRepository.getLocalParciales(),
                        califFinales = snRepository.getLocalFinales(),
                        esOffline = true,
                        ultimaSincro = "Carga Inicial Local"
                    )
                } else {
                    _uiState.value = SNUiState.Error("No se encontraron datos locales.")
                }
            } catch (e: Exception) {
                _uiState.value = SNUiState.Error("Error al cargar datos locales: ${e.message}")
            }
        }
    }

    override fun logout() {
        _uiState.value = SNUiState.Idle
    }

    override fun onDispose() {
        viewModelScope.cancel()
    }
}
