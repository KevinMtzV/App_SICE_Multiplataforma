package com.example.app_sice_multiplataforma.ui.screens

import com.example.app_sice_multiplataforma.data.LocalCache
import com.example.app_sice_multiplataforma.model.*
import com.example.app_sice_multiplataforma.network.SicenetService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.net.InetSocketAddress
import java.net.Socket

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
    data class Error(val mensaje: String, val esRedBlockeada: Boolean = false) : SNUiState
    object Loading : SNUiState
    object Idle : SNUiState
}

class SNViewModel {
    private val service = SicenetService()
    private val cache   = LocalCache()
    private val scope   = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _uiState = MutableStateFlow<SNUiState>(SNUiState.Idle)
    val uiState: StateFlow<SNUiState> = _uiState

    fun hayInternet(): Boolean = try {
        Socket().use { it.connect(InetSocketAddress("sicenet.surguanajuato.tecnm.mx", 443), 5000); true }
    } catch (e: Exception) { false }

    fun loginYConsultarPerfil(matricula: String, contrasenia: String, tipo: String = "ALUMNO") {
        if (matricula.isBlank() || contrasenia.isBlank()) {
            _uiState.value = SNUiState.Error("Ingresa tu matrícula y contraseña.")
            return
        }
        _uiState.value = SNUiState.Loading
        scope.launch {

            // Sin red → offline
            if (!hayInternet()) {
                val local = cache.getPerfil()
                _uiState.value = if (local != null) {
                    SNUiState.Success(local, cache.getKardex(), cache.getParciales(),
                        cache.getFinales(), cache.getCarga(), true, cache.getUltimaSincro())
                } else {
                    SNUiState.Error(
                        "Sin conexión al servidor SICENET.\n\nSolución: activa el hotspot de tu celular y conecta la laptop a esa red.",
                        esRedBlockeada = true
                    )
                }
                return@launch
            }

            // Con red — intentar login
            val loginOk: Boolean
            try {
                loginOk = service.acceso(matricula, contrasenia, tipo)
            } catch (e: Exception) {
                val msg = e.message ?: ""
                val bloqueado = "BLOCKED_403" in msg
                _uiState.value = SNUiState.Error(
                    if (bloqueado)
                        "Red bloqueada por SICENET (WAF 403).\n\nSolución: activa el hotspot de tu celular y conecta la laptop a esa red."
                    else
                        "Error de conexión: $msg",
                    esRedBlockeada = bloqueado
                )
                return@launch
            }

            if (!loginOk) {
                _uiState.value = SNUiState.Error(
                    "Matrícula o contraseña incorrectos.\nVerifica tus datos en sicenet.surguanajuato.tecnm.mx"
                )
                return@launch
            }

            // Login OK → cargar perfil y datos
            val perfil = try {
                service.getPerfil(matricula, contrasenia)
            } catch (e: Exception) { null }

            if (perfil == null || perfil.matricula.isBlank()) {
                _uiState.value = SNUiState.Error("Login exitoso pero no se obtuvo el perfil. Intenta de nuevo.")
                return@launch
            }
            cache.insertPerfil(perfil)

            val k = async { runCatching { service.getKardex() }.getOrDefault(emptyList()) }
            val c = async { runCatching { service.getCargaAcademica() }.getOrDefault(emptyList()) }
            val p = async { runCatching { service.getCalifUnidades() }.getOrDefault(emptyList()) }
            val f = async { runCatching { service.getCalifFinales() }.getOrDefault(emptyList()) }

            val kardex    = k.await()
            val carga     = c.await()
            val parciales = p.await()
            val finales   = f.await()

            if (kardex.isNotEmpty())    cache.insertKardex(kardex)
            if (carga.isNotEmpty())     cache.insertCarga(carga)
            if (parciales.isNotEmpty()) cache.insertParciales(parciales)
            if (finales.isNotEmpty())   cache.insertFinales(finales)

            _uiState.value = SNUiState.Success(
                perfil, kardex, parciales, finales, carga, false, cache.getUltimaSincro()
            )
        }
    }

    fun consultarKardex() { val cur = _uiState.value as? SNUiState.Success ?: return; scope.launch {
        if (hayInternet()) runCatching { service.getKardex() }.onSuccess { cache.insertKardex(it); _uiState.value = cur.copy(kardex = it, esOffline = false) }
        else _uiState.value = cur.copy(kardex = cache.getKardex(), esOffline = true, ultimaSincro = cache.getUltimaSincro()) } }

    fun consultarCargaAcademica() { val cur = _uiState.value as? SNUiState.Success ?: return; scope.launch {
        if (hayInternet()) runCatching { service.getCargaAcademica() }.onSuccess { cache.insertCarga(it); _uiState.value = cur.copy(cargaAcademica = it, esOffline = false) }
        else _uiState.value = cur.copy(cargaAcademica = cache.getCarga(), esOffline = true, ultimaSincro = cache.getUltimaSincro()) } }

    fun consultarCalificacionesUnidades() { val cur = _uiState.value as? SNUiState.Success ?: return; scope.launch {
        if (hayInternet()) runCatching { service.getCalifUnidades() }.onSuccess { cache.insertParciales(it); _uiState.value = cur.copy(califUnidades = it, esOffline = false) }
        else _uiState.value = cur.copy(califUnidades = cache.getParciales(), esOffline = true, ultimaSincro = cache.getUltimaSincro()) } }

    fun consultarCalificacionesFinales() { val cur = _uiState.value as? SNUiState.Success ?: return; scope.launch {
        if (hayInternet()) runCatching { service.getCalifFinales() }.onSuccess { if (it.isNotEmpty()) cache.insertFinales(it); _uiState.value = cur.copy(califFinales = it, esOffline = false) }
        else _uiState.value = cur.copy(califFinales = cache.getFinales(), esOffline = true, ultimaSincro = cache.getUltimaSincro()) } }

    fun logout() { service.clearCookies(); _uiState.value = SNUiState.Idle }
    fun onDispose() { scope.cancel() }
}
