package com.example.app_sice_multiplataforma

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.example.app_sice_multiplataforma.ui.AppSicenet
import com.example.app_sice_multiplataforma.ui.screens.DesktopSNViewModel

fun main() = application {
    val windowState = rememberWindowState(width = 1100.dp, height = 750.dp)

    Window(
        onCloseRequest = ::exitApplication,
        title = "SICENET - TecNM",
        state = windowState
    ) {
        val viewModel = remember { DesktopSNViewModel() }

        DisposableEffect(Unit) {
            onDispose { viewModel.onDispose() }
        }

        MaterialTheme {
            AppSicenet(viewModel = viewModel)
        }
    }
}