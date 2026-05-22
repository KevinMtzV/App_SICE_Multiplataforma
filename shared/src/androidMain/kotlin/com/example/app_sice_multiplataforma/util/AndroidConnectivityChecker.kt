package com.example.app_sice_multiplataforma.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class AndroidConnectivityChecker(private val context: Context) : ConnectivityChecker {
    override fun hasInternet(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
