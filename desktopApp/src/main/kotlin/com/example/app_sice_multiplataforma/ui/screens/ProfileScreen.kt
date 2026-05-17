package com.example.app_sice_multiplataforma.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_sice_multiplataforma.model.ProfileStudent

private val Purple = Color(0xFF4A2C5D)

@Composable
fun ProfileScreen(student: ProfileStudent, modifier: Modifier = Modifier) {
    val scroll = rememberScrollState()
    val estatusSicenet = student.estatus?.uppercase()?.trim() ?: ""
    val isActivo = estatusSicenet in listOf("VI", "VIGENTE", "ACT", "ACTIVO", "INSCRITO", "TRUE", "1")
    val statusColor = if (isActivo) Color(0xFF2E7D32) else Color(0xFFC62828)

    Column(
        modifier = modifier.fillMaxSize().verticalScroll(scroll).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // ── Header Card ──────────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Purple),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Row(
                modifier = Modifier.padding(28.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Avatar con inicial
                Box(
                    modifier = Modifier.size(80.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        student.nombre.firstOrNull()?.toString() ?: "?",
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(student.nombre.ifBlank { "Estudiante" },
                        color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Surface(color = Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp)) {
                        Text(
                            student.matricula,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // ── Info Cards en grid ───────────────────────────────
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            InfoCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.School,
                label = "Carrera",
                value = student.carrera.ifBlank { "—" },
                iconColor = Purple
            )
            InfoCard(
                modifier = Modifier.weight(1f),
                icon = if (isActivo) Icons.Default.Verified else Icons.Default.Warning,
                label = "Estatus",
                value = if (isActivo) "Vigente / Activo" else "Inactivo",
                iconColor = statusColor,
                valueColor = statusColor
            )
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            InfoCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Badge,
                label = "Matrícula",
                value = student.matricula.ifBlank { "—" },
                iconColor = Color(0xFF1565C0)
            )
            InfoCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Domain,
                label = "Institución",
                value = "TecNM Campus Guanajuato",
                iconColor = Color(0xFF2E7D32)
            )
        }
    }
}

@Composable
private fun InfoCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String,
    iconColor: Color,
    valueColor: Color = Color(0xFF1A1A2E)
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(iconColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = iconColor, modifier = Modifier.size(26.dp))
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(label, color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                Text(value, color = valueColor, fontSize = 15.sp, fontWeight = FontWeight.Bold, lineHeight = 20.sp)
            }
        }
    }
}
