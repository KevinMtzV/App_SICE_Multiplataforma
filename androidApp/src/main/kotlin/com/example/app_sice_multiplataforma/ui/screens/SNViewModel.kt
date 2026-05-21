package com.example.app_sice_multiplataforma.ui.screens

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.preference.PreferenceManager
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.work.WorkInfo
import com.example.app_sice_multiplataforma.MarsPhotosApplication
import com.example.app_sice_multiplataforma.data.SNWMRepository
import com.example.app_sice_multiplataforma.data.repository.SNRepository
import com.example.app_sice_multiplataforma.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class AndroidSNViewModel(
    application: Application,
    private val snRepository: SNRepository,
    private val snWMRepository: SNWMRepository
) : AndroidViewModel(application) {

    private val _snUiState = MutableStateFlow<SNUiState>(SNUiState.Idle)
    val uiStateFlow = _snUiState.asStateFlow()

    var snUiState: SNUiState 
        get() = _snUiState.value
        set(value) { _snUiState.value = value }

    val workInfo: StateFlow<WorkInfo?> = snWMRepository.outputWorkInfo
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    init {
        // Monitorear WorkManager internamente para actualizar snUiState
        viewModelScope.launch {
            workInfo.collect { info ->
                when (info?.state) {
                    WorkInfo.State.FAILED -> {
                        val error = info.outputData.getString("error") ?: "Error de conexión"
                        snUiState = SNUiState.Error(error)
                    }
                    WorkInfo.State.SUCCEEDED -> {
                        cargarDatosDesdeLocal()
                    }
                    else -> Unit
                }
            }
        }
    }

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
                    perfilLocal = snRepository.getLocalPerfil()
                    intentos++
                }

                if (perfilLocal != null) {
                    snUiState = SNUiState.Success(
                        data = perfilLocal,
                        kardex = snRepository.getLocalKardex(),
                        cargaAcademica = snRepository.getLocalCarga(),
                        califUnidades = snRepository.getLocalParciales(),
                        califFinales = snRepository.getLocalFinales(),
                        esOffline = true,
                        ultimaSincro = "Carga Inicial Local"
                    )
                } else {
                    snUiState = SNUiState.Error("Error: No se encontró el perfil del estudiante en la base de datos local.")
                }
            } catch (e: Exception) {
                snUiState = SNUiState.Error("Error al cargar datos locales: ${e.message}")
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
                        snRepository.saveKardex(lista)
                        snUiState = currentState.copy(kardex = lista, esOffline = false)
                    } catch (e: Exception) {
                        Log.e("SICENET_DEBUG", "Error Kardex API: ${e.message}")
                    }
                } else {
                    val listaLocal = withContext(Dispatchers.IO) { snRepository.getLocalKardex() }
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
                        snRepository.saveCarga(lista)
                        snUiState = currentState.copy(cargaAcademica = lista, esOffline = false)
                    } catch (e: Exception) {
                        Log.e("SICENET_DEBUG", "Error Carga API: ${e.message}")
                    }
                } else {
                    val listaLocal = withContext(Dispatchers.IO) { snRepository.getLocalCarga() }
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
                        snRepository.saveParciales(lista)
                        snUiState = currentState.copy(califUnidades = lista, esOffline = false)
                    } catch (e: Exception) {
                        Log.e("SICENET_DEBUG", "Error Unidades API: ${e.message}")
                    }
                } else {
                    val listaLocal = withContext(Dispatchers.IO) { snRepository.getLocalParciales() }
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
                        snRepository.saveFinales(lista)
                        snUiState = currentState.copy(califFinales = lista, esOffline = false)
                    } catch (e: Exception) {
                        Log.e("SICENET_DEBUG", "Error Finales API: ${e.message}")
                    }
                } else {
                    val listaLocal = withContext(Dispatchers.IO) { snRepository.getLocalFinales() }
                    snUiState = currentState.copy(califFinales = listaLocal, esOffline = true, ultimaSincro = getFechaActual())
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MarsPhotosApplication)
                AndroidSNViewModel(
                    application = application,
                    snRepository = application.container.snRepository,
                    snWMRepository = application.snwmRepository
                )
            }
        }
    }
}
