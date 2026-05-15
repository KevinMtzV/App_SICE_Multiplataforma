package com.example.app_sice_multiplataforma.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_sice_multiplataforma.model.ProfileStudent

@Composable
fun ProfileScreen(
    student: ProfileStudent,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    // Usamos valor directo para mantener la armonía
    val primaryColor = Color(0xFF4A2C5D)

    Box(modifier = modifier.fillMaxSize().background(Color.White)) {
        // --- FONDO DECORATIVO SUPERIOR ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(primaryColor, primaryColor.copy(alpha = 0.7f))
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(140.dp))

            // --- FOTO DE PERFIL / AVATAR ---
            Surface(
                modifier = Modifier.size(140.dp),
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 10.dp,
                border = BorderStroke(4.dp, Color.White)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier.size(120.dp),
                        tint = primaryColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- NOMBRE Y MATRÍCULA ---
            Text(
                text = student.nombre.ifBlank { "Estudiante" },
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                color = primaryColor
            )

            Surface(
                color = Color(0xFFF3E5F5),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = student.matricula,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = primaryColor,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- TARJETA DE INFORMACIÓN DETALLADA ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = BorderStroke(1.dp, Color(0xFFF3E5F5))
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "RESUMEN ACADÉMICO",
                        style = MaterialTheme.typography.labelMedium,
                        color = primaryColor,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )

                    ModernInfoRow(
                        label = "Carrera",
                        value = student.carrera,
                        icon = Icons.Default.School,
                        iconColor = primaryColor
                    )

                    Divider(modifier = Modifier.padding(vertical = 16.dp), thickness = 0.5.dp, color = Color(0xFFF3E5F5))

                    // --- LÓGICA DE ESTATUS CORREGIDA ---
                    // Convertimos lo que llegue ("VI", "VIGENTE", "ACT", etc.) a mayúsculas para evaluarlo
                    val estatusSicenet = student.estatus?.uppercase()?.trim() ?: ""
                    val isActivo = estatusSicenet in listOf("VI", "VIGENTE", "ACT", "ACTIVO", "INSCRITO", "TRUE", "1")

                    val statusColor = if (isActivo) Color(0xFF2E7D32) else Color(0xFFD32F2F)
                    val statusText = if (isActivo) "VIGENTE / ACTIVO" else "INACTIVO"

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        StatusIcon(
                            icon = if (isActivo) Icons.Default.Verified else Icons.Default.Warning,
                            color = statusColor
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Estatus",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.Gray
                            )
                            Text(
                                text = statusText,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = statusColor
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- PIE DE PÁGINA ---
            Text(
                text = "Información obtenida de SICENET Oficial",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun StatusIcon(icon: ImageVector, color: Color) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(color.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(26.dp))
    }
}

@Composable
fun ModernInfoRow(label: String, value: String, icon: ImageVector, iconColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(iconColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(26.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                lineHeight = 20.sp,
                color = Color(0xFF4A2C5D)
            )
        }
    }
}