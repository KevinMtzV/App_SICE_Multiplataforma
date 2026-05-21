package com.example.app_sice_multiplataforma.data.network

import io.ktor.client.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

fun createHttpClient(engine: io.ktor.client.engine.HttpClientEngine): HttpClient {
    return HttpClient(engine) {
        install(HttpCookies)
        install(HttpRedirect) {
            checkHttpMethod = false
        }
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }
    }
}
