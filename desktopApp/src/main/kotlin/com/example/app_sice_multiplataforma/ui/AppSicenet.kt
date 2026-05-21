package com.example.app_sice_multiplataforma.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_sice_multiplataforma.ui.screens.*

// ─────────────────── Constantes de color ───────────────────
private val Purple      = Color(0xFF4A2C5D)
private val PurpleLight = Color(0xFF6B3E8A)
private val Sidebar     = Color(0xFF1E1A24)   // sidebar oscuro — contraste profesional
private val SidebarHov  = Color(0xFF2D2535)
private val AccentGold  = Color(0xFFFFC107)
private val BgContent   = Color(0xFFF4F2F8)

// ─────────────────── Rutas ─────────────────────────────────
enum class Screen(val label: String, val icon: ImageVector, val group: String) {
    Profile        ("Mi Perfil",        Icons.Default.AccountCircle,        "ACADÉMICO"),
    Carga          ("Horario / Carga",  Icons.Default.CalendarMonth,        "ACADÉMICO"),
    Kardex         ("Kardex",           Icons.Default.HistoryEdu,           "ACADÉMICO"),
    Parciales      ("Parciales",        Icons.Default.Spellcheck,           "EVALUACIONES"),
    Finales        ("Finales",          Icons.Default.AssignmentTurnedIn,   "EVALUACIONES"),
}

@Composable
fun AppSicenet(viewModel: DesktopSNViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var current by remember { mutableStateOf(Screen.Profile) }

    LaunchedEffect(uiState) {
        if (uiState is SNUiState.Success && current == Screen.Profile) {
            current = Screen.Profile
        }
    }

    when (val state = uiState) {
        is SNUiState.Success -> MainLayout(state, current, viewModel,
            onNav = { screen ->
                current = screen
                when (screen) {
                    Screen.Kardex    -> viewModel.consultarKardex()
                    Screen.Carga     -> viewModel.consultarCargaAcademica()
                    Screen.Parciales -> viewModel.consultarCalificacionesUnidades()
                    Screen.Finales   -> viewModel.consultarCalificacionesFinales()
                    else             -> Unit
                }
            },
            onLogout = { viewModel.logout() }
        )
        else -> HomeScreen(
            snUiState = state,
            onLoginClick = { m, p, t -> viewModel.loginYConsultarPerfil(m, p, t) },
            modifier = Modifier.fillMaxSize()
        )
    }
}

// ──────────────── Layout principal post-login ───────────────
@Composable
private fun MainLayout(
    state: SNUiState.Success,
    current: Screen,
    viewModel: DesktopSNViewModel,
    onNav: (Screen) -> Unit,
    onLogout: () -> Unit
) {
    Row(Modifier.fillMaxSize()) {

        // ── Sidebar ─────────────────────────────────────────
        Sidebar(state, current, onNav, onLogout)

        // ── Área de contenido ────────────────────────────────
        Column(Modifier.weight(1f).fillMaxHeight().background(BgContent)) {

            // Topbar
            Topbar(state, current)

            // Banner offline
            if (state.esOffline && state.ultimaSincro.isNotBlank()) {
                Surface(color = Color(0xFFFFF3E0), modifier = Modifier.fillMaxWidth()) {
                    Row(
                        Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.WifiOff, null, tint = Color(0xFFE65100), modifier = Modifier.size(16.dp))
                        Text(
                            "Modo sin conexión · Datos del ${state.ultimaSincro}",
                            color = Color(0xFFE65100), fontSize = 12.sp, fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Pantalla activa
            Box(Modifier.weight(1f).padding(20.dp)) {
                when (current) {
                    Screen.Profile   -> ProfileScreen(state.data)
                    Screen.Kardex    -> KardexScreen(state.kardex)
                    Screen.Carga     -> CargaAcademicaScreen(state.cargaAcademica)
                    Screen.Parciales -> CalificacionesScreen(state.califUnidades)
                    Screen.Finales   -> FinalesScreen(state.califFinales)
                }
            }
        }
    }
}

// ──────────────── Sidebar oscuro ────────────────────────────
@Composable
private fun Sidebar(
    state: SNUiState.Success,
    current: Screen,
    onNav: (Screen) -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(240.dp)
            .fillMaxHeight()
            .background(Sidebar)
    ) {
        // Logo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .background(Purple),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Default.School, null, tint = AccentGold, modifier = Modifier.size(22.dp))
                Text("SICENET", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp, letterSpacing = 2.sp)
            }
        }

        // Avatar / usuario
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier.size(42.dp).clip(CircleShape).background(PurpleLight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    state.data.nombre.firstOrNull()?.toString() ?: "?",
                    color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp
                )
            }
            Column {
                Text(
                    state.data.nombre.split(" ").take(2).joinToString(" ").ifBlank { "Estudiante" },
                    color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp,
                    maxLines = 1
                )
                Text(state.data.matricula, color = Color.Gray, fontSize = 11.sp)
            }
        }

        HorizontalDivider(color = Color.White.copy(alpha = 0.08f), modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(Modifier.height(8.dp))

        // Navegación agrupada
        var lastGroup = ""
        Screen.entries.forEach { screen ->
            if (screen.group != lastGroup) {
                lastGroup = screen.group
                Text(
                    screen.group,
                    color = Color.Gray.copy(alpha = 0.6f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 4.dp),
                    letterSpacing = 1.5.sp
                )
            }
            SidebarItem(screen, selected = current == screen) { onNav(screen) }
        }

        Spacer(Modifier.weight(1f))

        // Estado sync
        if (state.ultimaSincro.isNotBlank() && !state.esOffline) {
            Row(
                Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Default.CloudDone, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(12.dp))
                Text("Sync: ${state.ultimaSincro}", color = Color.Gray, fontSize = 10.sp)
            }
        }

        HorizontalDivider(color = Color.White.copy(alpha = 0.08f), modifier = Modifier.padding(horizontal = 16.dp))

        // Logout
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onLogout)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(Icons.Default.Logout, null, tint = Color(0xFFEF5350), modifier = Modifier.size(18.dp))
            Text("Cerrar sesión", color = Color(0xFFEF5350), fontSize = 13.sp)
        }
    }
}

@Composable
private fun SidebarItem(screen: Screen, selected: Boolean, onClick: () -> Unit) {
    val bg  = if (selected) Purple.copy(alpha = 0.6f) else Color.Transparent
    val fg  = if (selected) Color.White else Color.Gray.copy(alpha = 0.8f)
    val bar = if (selected) Purple else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Indicador de selección
        Box(
            Modifier
                .width(3.dp)
                .height(16.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(if (selected) AccentGold else Color.Transparent)
        )
        Icon(screen.icon, null, tint = if (selected) AccentGold else fg, modifier = Modifier.size(18.dp))
        Text(screen.label, color = fg, fontSize = 13.sp, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal)
    }
}

// ──────────────── Topbar ────────────────────────────────────
@Composable
private fun Topbar(state: SNUiState.Success, current: Screen) {
    Surface(
        color = Color.White,
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 14.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(current.label, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1A1A2E))
                Text(current.group, color = Color.Gray, fontSize = 11.sp)
            }
            // Carrera chip
            Surface(
                color = Color(0xFFF3E5F5),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.School, null, tint = Purple, modifier = Modifier.size(14.dp))
                    Text(state.data.carrera.take(30), color = Purple, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}
