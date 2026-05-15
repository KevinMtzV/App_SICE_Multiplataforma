package com.example.app_sice_multiplataforma.data

import android.content.Context
import android.preference.PreferenceManager
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class ReceivedCookiesInterceptor(private val context: Context) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse: Response = chain.proceed(chain.request())
        val cookieHeaders = originalResponse.headers("Set-Cookie")

        if (cookieHeaders.isNotEmpty()) {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val existingCookies = preferences.getStringSet("PREF_COOKIES", mutableSetOf()) ?: mutableSetOf()

            // Creamos una copia para poder modificar sin afectar la referencia original
            val updatedCookies = HashSet<String>(existingCookies)

            for (header in cookieHeaders) {
                val cookieName = header.substringBefore("=")
                // Eliminamos la versión vieja de esta cookie si ya existía para no duplicar
                updatedCookies.removeAll { it.startsWith("$cookieName=") }
                // Agregamos la nueva versión que mandó el servidor
                updatedCookies.add(header)
            }

            preferences.edit()
                .putStringSet("PREF_COOKIES", updatedCookies)
                .apply()
        }

        return originalResponse
    }
}