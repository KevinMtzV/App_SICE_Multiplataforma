package com.example.app_sice_multiplataforma.ui.screens

import com.example.app_sice_multiplataforma.data.local.getDatabaseBuilder
import com.example.app_sice_multiplataforma.data.network.SICENETService
import com.example.app_sice_multiplataforma.data.network.createHttpClient
import com.example.app_sice_multiplataforma.data.repository.DefaultSNRepository
import com.example.app_sice_multiplataforma.data.repository.SNRepository
import com.example.app_sice_multiplataforma.util.DesktopConnectivityChecker
import io.ktor.client.engine.okhttp.*
import java.text.SimpleDateFormat
import java.util.*

class DesktopSNViewModel : SharedSNViewModel(
    snRepository = createRepository(),
    connectivityChecker = DesktopConnectivityChecker(),
    getCurrentDate = {
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
    }
) {
    companion object {
        private fun createRepository(): SNRepository {
            val database = getDatabaseBuilder().fallbackToDestructiveMigration(true).build()
            val ktorClient = createHttpClient(OkHttp.create())
            val sicenetService = SICENETService(ktorClient)
            return DefaultSNRepository(sicenetService, database.sicenetDao())
        }
    }
}
