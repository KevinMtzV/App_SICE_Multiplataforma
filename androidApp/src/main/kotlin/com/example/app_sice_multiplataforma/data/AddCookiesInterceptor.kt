package com.example.app_sice_multiplataforma.data

import android.content.Context
import android.preference.PreferenceManager
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class AddCookiesInterceptor(private val context: Context) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()

        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val cookies = preferences.getStringSet(PREF_COOKIES, mutableSetOf()) ?: mutableSetOf()

        if (cookies.isNotEmpty()) {
            val cookieString = cookies.joinToString(separator = "; ") { cookie ->
                cookie.substringBefore(";")
            }
            builder.header("Cookie", cookieString)
        }

        return chain.proceed(builder.build())
    }

    companion object {
        const val PREF_COOKIES = "PREF_COOKIES"
    }
}