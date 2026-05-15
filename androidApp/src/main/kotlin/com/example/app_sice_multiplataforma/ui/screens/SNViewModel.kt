package com.example.app_sice_multiplataforma.ui.screens

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.preference.PreferenceManager
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.work.WorkInfo
import com.example.app_sice_multiplataforma.MarsPhotosApplication
import com.example.app_sice_multiplataforma.data.SNRepository
import com.example.app_sice_multiplataforma.data.SNWMRepository
import com.example.app_sice_multiplataforma.model.CalificacionFinal
import com.example.app_sice_multiplataforma.model.CalificacionParcial
import com.example.app_sice_multiplataforma.model.KardexItem
import com.example.app_sice_multiplataforma.model.MateriaCarga
import com.example.app_sice_multiplataforma.model.ProfileStudent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

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
    object Error : SNUiState
    object Loading : SNUiState
    object Idle : SNUiState
}

class SNViewModel(
    application: Application,
    private val snRepository: SNRepository,
    private val dbRepository: SNRepository,
    private val snWMRepository: SNWMRepository
) : AndroidViewModel(application) {

    var snUiState: SNUiState by mutableStateOf(SNUiState.Idle)
        private set

    val workInfo: StateFlow<WorkInfo?> = snWMRepository.outputWorkInfo
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private fun getFechaActual(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date())
    }

    fun hayInternet(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun logout(context: Context) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefs.edit().remove("PREF_COOKIES").apply()
        snUiState = SNUiState.Idle
    }

    fun loginYConsultarPerfil(matricula: String, contrasenia: String, tipo: String) {
        snUiState = SNUiState.Loading
        snWMRepository.login(matricula, contrasenia)
    }

    fun cargarDatosDesdeLocal() {
        viewModelScope.launch {
            snUiState = SNUiState.Loading
            try {
                var perfilLocal: ProfileStudent? = null
                var intentos = 0
                while (perfilLocal == null && intentos < 5) {
                    delay(1000)
                    perfilLocal = dbRepository.getPerfil()
                    intentos++
                }

                if (perfilLocal != null) {
                    val kardex = dbRepository.getKardex()
                    val carga = dbRepository.getCargaAcademica()
                    val parciales = dbRepository.getCalificacionesUnidades()
                    val finales = dbRepository.getCalificacionesFinales(1)

                    snUiState = SNUiState.Success(
                        data = perfilLocal,
                        kardex = kardex,
                        cargaAcademica = carga,
                        califUnidades = parciales,
                        califFinales = finales,
                        esOffline = true,
                        ultimaSincro = "Carga Inicial Local"
                    )
                } else {
                    snUiState = SNUiState.Error
                }
            } catch (e: Exception) {
                snUiState = SNUiState.Error
            }
        }
    }
    fun consultarKardex() {
        val currentState = snUiState
        if (currentState is SNUiState.Success) {
            viewModelScope.launch {
                if (hayInternet(getApplication())) {
                    try {
                        val lista = withContext(Dispatchers.IO) { snRepository.getKardex() }
                        dbRepository.insertKardex(lista)
                        snUiState = currentState.copy(kardex = lista, esOffline = false)
                    } catch (e: Exception) {
                        Log.e("SICENET_DEBUG", "Error Kardex API: ${e.message}")
                    }
                } else {
                    // Modo Offline: Leer de Room
                    val listaLocal = withContext(Dispatchers.IO) { dbRepository.getKardex() }
                    snUiState = currentState.copy(kardex = listaLocal, esOffline = true, ultimaSincro = getFechaActual())
                }
            }
        }
    }

    fun consultarCargaAcademica() {
        val currentState = snUiState
        if (currentState is SNUiState.Success) {
            viewModelScope.launch {
                if (hayInternet(getApplication())) {
                    try {
                        val lista = withContext(Dispatchers.IO) { snRepository.getCargaAcademica() }
                        dbRepository.insertCarga(lista)
                        snUiState = currentState.copy(cargaAcademica = lista, esOffline = false)
                    } catch (e: Exception) {
                        Log.e("SICENET_DEBUG", "Error Carga API: ${e.message}")
                    }
                } else {
                    val listaLocal = withContext(Dispatchers.IO) { dbRepository.getCargaAcademica() }
                    snUiState = currentState.copy(cargaAcademica = listaLocal, esOffline = true, ultimaSincro = getFechaActual())
                }
            }
        }
    }

    fun consultarCalificacionesUnidades() {
        val currentState = snUiState
        if (currentState is SNUiState.Success) {
            viewModelScope.launch {
                if (hayInternet(getApplication())) {
                    try {
                        val lista = withContext(Dispatchers.IO) { snRepository.getCalificacionesUnidades() }
                        dbRepository.insertParciales(lista)
                        snUiState = currentState.copy(califUnidades = lista, esOffline = false)
                    } catch (e: Exception) {
                        Log.e("SICENET_DEBUG", "Error Unidades API: ${e.message}")
                    }
                } else {
                    val listaLocal = withContext(Dispatchers.IO) { dbRepository.getCalificacionesUnidades() }
                    snUiState = currentState.copy(califUnidades = listaLocal, esOffline = true, ultimaSincro = getFechaActual())
                }
            }
        }
    }

    fun consultarCalificacionesFinales() {
        val currentState = snUiState
        if (currentState is SNUiState.Success) {
            viewModelScope.launch {
                if (hayInternet(getApplication())) {
                    try {
                        val lista = withContext(Dispatchers.IO) { snRepository.getCalificacionesFinales(1) }

                        if (lista.isNotEmpty()) {
                            withContext(Dispatchers.IO) { dbRepository.insertFinales(lista) }
                        }

                        snUiState = currentState.copy(califFinales = lista, esOffline = false)
                    } catch (e: Exception) {
                        Log.e("SICENET_DEBUG", "Error Finales API: ${e.message}")
                    }
                } else {
                    val listaLocal = withContext(Dispatchers.IO) { dbRepository.getCalificacionesFinales(1) }
                    snUiState = currentState.copy(califFinales = listaLocal, esOffline = true, ultimaSincro = getFechaActual())
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MarsPhotosApplication)
                SNViewModel(
                    application = application,
                    snRepository = application.container.snRepository,
                    dbRepository = application.container.dbLocalRepository,
                    snWMRepository = application.snwmRepository
                )
            }
        }
    }
}