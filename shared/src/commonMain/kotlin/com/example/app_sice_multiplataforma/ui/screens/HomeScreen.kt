package com.example.app_sice_multiplataforma.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(
    snUiState: SNUiState,
    onLoginClick: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val gradient = Brush.verticalGradient(
        colors = listOf(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.surface)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(gradient),
        contentAlignment = Alignment.Center
    ) {
        when (snUiState) {
            is SNUiState.Loading -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(strokeWidth = 4.dp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Iniciando sesión...", style = MaterialTheme.typography.bodyMedium)
                }
            }
            is SNUiState.Success -> {
                // No mostrar nada mientras se navega al perfil
            }
            else -> {
                LoginContent(
                    errorMsg = (snUiState as? SNUiState.Error)?.mensaje,
                    onLoginClick = onLoginClick
                )
            }
        }
    }
}

@Composable
private fun LoginContent(
    errorMsg: String?,
    onLoginClick: (String, String, String) -> Unit
) {
    var matricula by remember { mutableStateOf("") }
    var contrasenia by remember { mutableStateOf("") }
    val isError = !errorMsg.isNullOrBlank()
    val tipoUsuario = "ALUMNO"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "SICENET",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "TECNM",
                style = MaterialTheme.typography.labelLarge,
                letterSpacing = 4.sp,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(40.dp))

            OutlinedTextField(
                value = matricula,
                onValueChange = { matricula = it.uppercase().trim() },
                label = { Text("Número de Control") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = contrasenia,
                onValueChange = { contrasenia = it },
                label = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { onLoginClick(matricula, contrasenia, tipoUsuario) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(4.dp)
            ) {
                Text(
                    text = "INICIAR SESIÓN",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            if (isError) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = errorMsg ?: "Acceso denegado. Revisa tus datos.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
