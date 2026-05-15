package com.example.app_sice_multiplataforma.ui.screens

import androidx.compose.foundation.BorderStroke
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
    // Fondo blanco puro para mantener la armonía de toda la aplicación
    Box(modifier = modifier.fillMaxSize().background(Color.White)) {
        if (finales.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "No hay calificaciones finales", color = Color.Gray)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Calificaciones Finales",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFF4A2C5D), // Valor directo: Morado Principal
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                items(finales) { item ->
                    MateriaCard(item)
                }
            }
        }
    }
}

@Composable
fun MateriaCard(item: CalificacionFinal) {
    val calif = item.calificacion ?: "0"
    val valorNumerico = calif.toIntOrNull() ?: 0
    val esReprobatoria = valorNumerico < 70

    Card(
        shape = RoundedCornerShape(16.dp),
        // Cambiamos a fondo blanco y borde morado suave para no saturar la vista
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFF3E5F5)), // Valor directo: Morado Suave
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono en el color principal para identidad visual
            Icon(
                imageVector = Icons.Default.Assignment,
                contentDescription = null,
                tint = Color(0xFF4A2C5D),
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.materia ?: "Materia Desconocida",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4A2C5D)
                )
                Text(
                    text = item.acreditacion ?: "Pendiente",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }

            // Badge de Calificación con colores de estatus
            Surface(
                color = if (esReprobatoria) Color(0xFFD32F2F) else Color(0xFF4A2C5D),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(width = 55.dp, height = 38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = calif,
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp
                    )
                }
            }
        }
    }
}