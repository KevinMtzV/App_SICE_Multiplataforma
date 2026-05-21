package com.example.app_sice_multiplataforma.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_sice_multiplataforma.model.CalificacionFinal

@Composable
fun FinalesScreen(
    finales: List<CalificacionFinal>,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize().background(Color.White)) {
        if (finales.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Sin calificaciones finales", color = Color.Gray)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Calificaciones Finales",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFF4A2C5D),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                items(finales) { materia ->
                    FinalItemCard(materia)
                }
            }
        }
    }
}

@Composable
fun FinalItemCard(materia: CalificacionFinal) {
    val calif = materia.calificacion.trim().toIntOrNull() ?: 0
    val esAprobada = calif >= 70
    val colorEstatus = if (esAprobada) Color(0xFF388E3C) else Color(0xFFD32F2F)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Assignment,
                contentDescription = null,
                tint = Color(0xFF4A2C5D),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = materia.materia,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4A2C5D)
                )
                Text(
                    text = materia.acreditacion,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
            Surface(
                color = colorEstatus.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = materia.calificacion,
                    color = colorEstatus,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }
    }
}
