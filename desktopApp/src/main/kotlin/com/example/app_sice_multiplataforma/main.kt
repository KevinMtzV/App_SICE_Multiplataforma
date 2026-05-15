package com.example.app_sice_multiplataforma

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "App_SICE_Multiplataforma",
    ) {
        App()
    }
}