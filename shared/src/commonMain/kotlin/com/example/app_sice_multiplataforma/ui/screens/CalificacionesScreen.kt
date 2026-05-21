package com.example.app_sice_multiplataforma.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_sice_multiplataforma.model.CalificacionParcial

@Composable
fun CalificacionesScreen(
    calificaciones: List<CalificacionParcial>,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize().background(Color.White)) {
        if (calificaciones.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Sin calificaciones parciales", color = Color.Gray)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Parciales por Unidad",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFF4A2C5D),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                items(calificaciones) { materia ->
                    MateriaExpandibleCard(materia)
                }
            }
        }
    }
}

@Composable
fun MateriaExpandibleCard(materia: CalificacionParcial) {
    var expanded by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(targetValue = if (expanded) 180f else 0f)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFF3E5F5)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = materia.materia,
                    color = Color(0xFF4A2C5D),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = Color(0xFF4A2C5D),
                    modifier = Modifier.rotate(rotationState)
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .background(Color(0xFFF3E5F5).copy(alpha = 0.5f))
                        .padding(16.dp)
                ) {
                    val todasLasUnidades = listOf(
                        "Unidad 1" to materia.p1, "Unidad 2" to materia.p2, "Unidad 3" to materia.p3,
                        "Unidad 4" to materia.p4, "Unidad 5" to materia.p5, "Unidad 6" to materia.p6,
                        "Unidad 7" to materia.p7, "Unidad 8" to materia.p8, "Unidad 9" to materia.p9,
                        "Unidad 10" to materia.p10, "Unidad 11" to materia.p11, "Unidad 12" to materia.p12
                    ).filter { !it.second.isNullOrBlank() }

                    if (todasLasUnidades.isEmpty()) {
                        Text(
                            text = "No hay unidades registradas",
                            color = Color.Gray,
                            fontSize = 13.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        todasLasUnidades.forEach { (label, calif) ->
                            UnidadRow(label, calif!!)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UnidadRow(label: String, calif: String) {
    val valorNumerico = calif.trim().toIntOrNull() ?: 0
    val esReprobado = valorNumerico < 70

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color(0xFF4A2C5D),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )

        Surface(
            color = if (esReprobado) Color(0xFFD32F2F) else Color(0xFF388E3C),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = calif.trim(),
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    }
}
