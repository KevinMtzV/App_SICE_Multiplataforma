package com.example.app_sice_multiplataforma.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_sice_multiplataforma.network.SicenetConfig

private val Purple      = Color(0xFF4A2C5D)
private val PurpleLight = Color(0xFF7B4FA6)
private val AccentGold  = Color(0xFFFFC107)

@Composable
fun HomeScreen(
    snUiState: SNUiState,
    onLoginClick: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxSize()) {
        // ── Panel izquierdo branding ───────────────────────
        Box(
            Modifier.width(400.dp).fillMaxHeight()
                .background(Brush.verticalGradient(listOf(Purple, PurpleLight))),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(40.dp)
            ) {
                Surface(shape = RoundedCornerShape(28.dp), color = Color.White.copy(alpha = 0.15f), modifier = Modifier.size(90.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.School, null, tint = Color.White, modifier = Modifier.size(52.dp))
                    }
                }
                Text("SICENET", color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Black, letterSpacing = 4.sp)
                Text("Tecnológico Nacional de México", color = Color.White.copy(alpha = 0.75f), fontSize = 13.sp, textAlign = TextAlign.Center)
                Spacer(Modifier.height(8.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
                Spacer(Modifier.height(4.dp))
                listOf(
                    Icons.Default.HistoryEdu         to "Kardex completo",
                    Icons.Default.DateRange          to "Carga académica",
                    Icons.Default.Spellcheck         to "Calificaciones parciales",
                    Icons.Default.AssignmentTurnedIn to "Calificaciones finales"
                ).forEach { (ic, lbl) ->
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(ic, null, tint = AccentGold, modifier = Modifier.size(16.dp))
                        Text(lbl, color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text("v1.0 · Edición Escritorio", color = Color.White.copy(alpha = 0.35f), fontSize = 11.sp)
            }
        }

        // ── Panel derecho formulario ───────────────────────
        Box(
            Modifier.weight(1f).fillMaxHeight().background(Color(0xFFF4F2F8)),
            contentAlignment = Alignment.Center
        ) {
            when (val s = snUiState) {
                is SNUiState.Loading -> LoadingPanel()
                else -> LoginForm(
                    errorState = snUiState as? SNUiState.Error,
                    onLogin = onLoginClick
                )
            }
        }
    }
}

@Composable
private fun LoadingPanel() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
        CircularProgressIndicator(color = Purple, strokeWidth = 3.dp, modifier = Modifier.size(48.dp))
        Text("Conectando con SICENET…", color = Purple, fontWeight = FontWeight.Medium)
        Text("Esto puede tardar hasta 30 segundos", color = Color.Gray, fontSize = 12.sp)
    }
}

@Composable
private fun LoginForm(errorState: SNUiState.Error?, onLogin: (String, String, String) -> Unit) {
    var matricula   by remember { mutableStateOf("") }
    var contrasenia by remember { mutableStateOf("") }
    var verPass     by remember { mutableStateOf(false) }

    val doLogin = { onLogin(matricula.trim().uppercase(), contrasenia, "ALUMNO") }

    Card(
        modifier = Modifier.widthIn(max = 460.dp).fillMaxWidth().padding(32.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(Modifier.padding(40.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {

            // Cabecera
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("Bienvenido", color = Color.Gray, fontSize = 12.sp)
                Text("Inicia sesión", color = Purple, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
            HorizontalDivider(color = Color(0xFFF3E5F5))

            // Matrícula
            OutlinedTextField(
                value = matricula,
                onValueChange = { matricula = it.uppercase().filter { c -> c != ' ' } },
                label = { Text("Número de Control") },
                leadingIcon = { Icon(Icons.Default.Badge, null, tint = Purple) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Purple, focusedLabelColor = Purple)
            )

            // Contraseña
            OutlinedTextField(
                value = contrasenia,
                onValueChange = { contrasenia = it },   // ← SIN toUpperCase, es case-sensitive
                label = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, null, tint = Purple) },
                trailingIcon = {
                    IconButton(onClick = { verPass = !verPass }) {
                        Icon(if (verPass) Icons.Default.VisibilityOff else Icons.Default.Visibility, null, tint = Color.Gray)
                    }
                },
                visualTransformation = if (verPass) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth().onKeyEvent { e ->
                    if (e.key == Key.Enter && e.type == KeyEventType.KeyUp) { doLogin(); true } else false
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Purple, focusedLabelColor = Purple)
            )

            // ── Bloque de error con instrucción según tipo ──
            if (errorState != null) {
                if (errorState.esRedBlockeada) {
                    // Error de red/WAF → instrucción específica del hotspot
                    Surface(color = Color(0xFFFFF8E1), shape = RoundedCornerShape(12.dp)) {
                        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.WifiOff, null, tint = Color(0xFFE65100), modifier = Modifier.size(20.dp))
                                Text("Red bloqueada por SICENET", color = Color(0xFFE65100), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Text(
                                "El servidor solo acepta la red del campus.\n\n" +
                                "✅ Solución rápida:\n" +
                                "1. En tu celular: Ajustes → Punto de acceso → Activar\n" +
                                "2. Conecta tu laptop al WiFi del celular\n" +
                                "3. Intenta iniciar sesión de nuevo",
                                color = Color(0xFF5D4037), fontSize = 12.sp, lineHeight = 18.sp
                            )
                        }
                    }
                } else {
                    // Error de credenciales
                    Surface(color = Color(0xFFFFEBEE), shape = RoundedCornerShape(12.dp)) {
                        Row(Modifier.padding(12.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Icon(Icons.Default.ErrorOutline, null, tint = Color(0xFFC62828), modifier = Modifier.size(20.dp))
                            Text(errorState.mensaje, color = Color(0xFFC62828), fontSize = 12.sp, lineHeight = 17.sp)
                        }
                    }
                }
            }

            // Botón
            Button(
                onClick = doLogin,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Purple)
            ) {
                Icon(Icons.Default.Login, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("INICIAR SESIÓN", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }

            // Nota fija
            Text(
                "⚠ Requiere la red del campus TecNM o el hotspot de tu celular",
                color = Color.Gray, fontSize = 11.sp, textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
