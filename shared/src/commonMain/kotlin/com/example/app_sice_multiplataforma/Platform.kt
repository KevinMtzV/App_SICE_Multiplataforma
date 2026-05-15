package com.example.app_sice_multiplataforma

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform