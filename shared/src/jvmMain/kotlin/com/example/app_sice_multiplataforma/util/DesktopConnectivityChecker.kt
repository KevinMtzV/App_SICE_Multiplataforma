package com.example.app_sice_multiplataforma.util

import java.net.InetSocketAddress
import java.net.Socket

class DesktopConnectivityChecker : ConnectivityChecker {
    override fun hasInternet(): Boolean = try {
        Socket().use { it.connect(InetSocketAddress("sicenet.surguanajuato.tecnm.mx", 443), 5000); true }
    } catch (e: Exception) {
        false
    }
}
