package com.example.app_sice_multiplataforma.ui.screens

import com.example.app_sice_multiplataforma.data.local.getDatabaseBuilder
import com.example.app_sice_multiplataforma.data.network.SICENETService
import com.example.app_sice_multiplataforma.data.network.createHttpClient
import com.example.app_sice_multiplataforma.data.repository.DefaultSNRepository
import com.example.app_sice_multiplataforma.data.repository.SNRepository
import com.example.app_sice_multiplataforma.model.*
import io.ktor.client.engine.okhttp.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.net.InetSocketAddress
import java.net.Socket
import java.text.SimpleDateFormat
import java.util.*

class DesktopSNViewModel : ISNViewModel {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    private val database by lazy {
        getDatabaseBuilder().fallbackToDestructiveMigration(true).build()
    }
    
    private val ktorClient = createHttpClient(OkHttp.create())
    private val sicenetService = SICENETService(ktorClient)
    
    private val snRepository: SNRepository = DefaultSNRepository(
        sicenetService, 
        database.sicenetDao()
    )

    private val _uiState = MutableStateFlow<SNUiState>(SNUiState.Idle)
    override val uiState: StateFlow<SNUiState> = _uiState.asStateFlow()

    private fun getFechaActual(): String {
        return SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
    }

    fun hayInternet(): Boolean = try {
        Socket().use { it.connect(InetSocketAddress("sicenet.surguanajuato.tecnm.mx", 443), 5000); true }
    } catch (e: Exception) { false }

    override fun loginYConsultarPerfil(matricula: String, contrasenia: String, tipo: String) {
        if (matricula.isBlank() || contrasenia.isBlank()) {
            _uiState.value = SNUiState.Error("Ingresa tu matrícula y contraseña.")
            return
        }
        _uiState.value = SNUiState.Loading
        scope.launch {
            if (!hayInternet()) {
                val local = snRepository.getLocalPerfil()
                _uiState.value = if (local != null) {
                    SNUiState.Success(
                        local, 
                        snRepository.getLocalKardex(), 
                        snRepository.getLocalParciales(),
                        snRepository.getLocalFinales(), 
                        snRepository.getLocalCarga(), 
                        true, 
                        "Carga Local (Offline)"
                    )
                } else {
                    SNUiState.Error("Sin conexión y sin datos locales.")
                }
                return@launch
            }

            try {
                val loginRes = snRepository.acceso(matricula, contrasenia, tipo)
                if (loginRes.contains("true")) {
                    val perfil = snRepository.profile(matricula, contrasenia)
                    snRepository.savePerfil(perfil)
                    
                    val kardex = snRepository.getKardex()
                    val carga = snRepository.getCargaAcademica()
                    val parciales = snRepository.getCalificacionesUnidades()
                    val finales = snRepository.getCalificacionesFinales()
                    
                    snRepository.saveKardex(kardex)
                    snRepository.saveCarga(carga)
                    snRepository.saveParciales(parciales)
                    snRepository.saveFinales(finales)

                    _uiState.value = SNUiState.Success(
                        perfil, kardex, parciales, finales, carga, false, getFechaActual()
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
        scope.launch {
            if (hayInternet()) {
                val res = snRepository.getKardex()
                snRepository.saveKardex(res)
                _uiState.value = cur.copy(kardex = res, esOffline = false)
            } else {
                _uiState.value = cur.copy(kardex = snRepository.getLocalKardex(), esOffline = true)
            }
        }
    }

    override fun consultarCargaAcademica() {
        val cur = _uiState.value as? SNUiState.Success ?: return
        scope.launch {
            if (hayInternet()) {
                val res = snRepository.getCargaAcademica()
                snRepository.saveCarga(res)
                _uiState.value = cur.copy(cargaAcademica = res, esOffline = false)
            } else {
                _uiState.value = cur.copy(cargaAcademica = snRepository.getLocalCarga(), esOffline = true)
            }
        }
    }

    override fun consultarCalificacionesUnidades() {
        val cur = _uiState.value as? SNUiState.Success ?: return
        scope.launch {
            if (hayInternet()) {
                val res = snRepository.getCalificacionesUnidades()
                snRepository.saveParciales(res)
                _uiState.value = cur.copy(califUnidades = res, esOffline = false)
            } else {
                _uiState.value = cur.copy(califUnidades = snRepository.getLocalParciales(), esOffline = true)
            }
        }
    }

    override fun consultarCalificacionesFinales() {
        val cur = _uiState.value as? SNUiState.Success ?: return
        scope.launch {
            if (hayInternet()) {
                val res = snRepository.getCalificacionesFinales()
                snRepository.saveFinales(res)
                _uiState.value = cur.copy(califFinales = res, esOffline = false)
            } else {
                _uiState.value = cur.copy(califFinales = snRepository.getLocalFinales(), esOffline = true)
            }
        }
    }

    override fun logout() {
        _uiState.value = SNUiState.Idle
    }
    
    fun onDispose() {
        scope.cancel()
    }
}
