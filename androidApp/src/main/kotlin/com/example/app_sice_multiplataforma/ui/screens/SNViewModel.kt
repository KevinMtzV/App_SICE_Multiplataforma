package com.example.app_sice_multiplataforma.ui.screens

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.app_sice_multiplataforma.MarsPhotosApplication
import com.example.app_sice_multiplataforma.data.repository.SNRepository
import com.example.app_sice_multiplataforma.util.AndroidConnectivityChecker
import com.example.app_sice_multiplataforma.util.ConnectivityChecker
import java.text.SimpleDateFormat
import java.util.*

class AndroidSNViewModel(
    snRepository: SNRepository,
    connectivityChecker: ConnectivityChecker
) : SharedSNViewModel(
    snRepository = snRepository,
    connectivityChecker = connectivityChecker,
    getCurrentDate = {
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
    }
) {
    // Si necesitas exponer el flow con el nombre que tenía antes para no romper la UI
    val uiStateFlow = uiState

    fun logout(context: android.content.Context) {
        val prefs = android.preference.PreferenceManager.getDefaultSharedPreferences(context)
        prefs.edit().remove("PREF_COOKIES").apply()
        super.logout()
    }

    override fun logout() {
        super.logout()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MarsPhotosApplication)
                AndroidSNViewModel(
                    snRepository = application.container.snRepository,
                    connectivityChecker = AndroidConnectivityChecker(application)
                )
            }
        }
    }
}
