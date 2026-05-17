package com.example.app_sice_multiplataforma.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_sice_multiplataforma.model.KardexItem

private val Purple = Color(0xFF4A2C5D)

@Composable
fun KardexScreen(kardexList: List<KardexItem>, modifier: Modifier = Modifier) {
    val semestres = remember(kardexList) {
        kardexList.mapNotNull { it.semestre }.distinct().sortedBy { it.toIntOrNull() ?: 0 }
    }
    var filtro by remember { mutableStateOf<String?>(null) }
    val lista = if (filtro == null) kardexList else kardexList.filter { it.semestre == filtro }

    val promedio = if (lista.isEmpty()) 0.0 else lista.map { it.calificacion }.average()
    val aprobadas = lista.count { it.calificacion >= 70 }

    Column(modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {

        // ── Resumen row ──────────────────────────────────────
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard(Modifier.weight(1f), "Promedio General", String.format("%.2f", promedio),
                Icons.Default.TrendingUp, Purple)
            StatCard(Modifier.weight(1f), "Materias Aprobadas", "$aprobadas / ${lista.size}",
                Icons.Default.CheckCircle, Color(0xFF2E7D32))
            StatCard(Modifier.weight(1f), "Materias Reprobadas", "${lista.size - aprobadas}",
                Icons.Default.Cancel, Color(0xFFC62828))
            StatCard(Modifier.weight(1f), "Semestres", "${semestres.size}",
                Icons.Default.CalendarMonth, Color(0xFF1565C0))
        }

        // ── Filtros ──────────────────────────────────────────
        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(selected = filtro == null, onClick = { filtro = null },
                label = { Text("Todos (${kardexList.size})") }, shape = RoundedCornerShape(20.dp))
            semestres.forEach { s ->
                FilterChip(selected = filtro == s, onClick = { filtro = s },
                    label = { Text("Sem. $s") }, shape = RoundedCornerShape(20.dp))
            }
        }

        // ── Tabla ────────────────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth().weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column {
                // Header de tabla
                Row(
                    Modifier.fillMaxWidth().background(Color(0xFFF8F5FC))
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("MATERIA", Modifier.weight(3f), color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("SEMESTRE", Modifier.weight(1f), color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("PERÍODO", Modifier.weight(1f), color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Box(Modifier.width(80.dp), contentAlignment = Alignment.Center) {
                        Text("CALIF.", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
                HorizontalDivider()

                if (lista.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Sin registros", color = Color.Gray)
                    }
                } else {
                    LazyColumn {
                        items(lista) { item ->
                            KardexRow(item)
                            HorizontalDivider(color = Color(0xFFF0F0F0))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(modifier: Modifier, label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    Card(modifier = modifier, shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = color, modifier = Modifier.size(22.dp))
            }
            Column {
                Text(label, color = Color.Gray, fontSize = 10.sp)
                Text(value, color = color, fontSize = 18.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
private fun KardexRow(item: KardexItem) {
    val aprobada = item.calificacion >= 70
    val color = if (aprobada) Color(0xFF2E7D32) else Color(0xFFC62828)

    Row(
        Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(Modifier.weight(3f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(
                if (aprobada) Icons.Default.CheckCircle else Icons.Default.Cancel,
                null, tint = color, modifier = Modifier.size(16.dp)
            )
            Text(item.materia, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1A1A2E))
        }
        Text("Sem. ${item.semestre ?: "—"}", Modifier.weight(1f), fontSize = 12.sp, color = Color.Gray)
        Text("${item.periodo ?: ""} ${item.anio ?: ""}".trim(), Modifier.weight(1f), fontSize = 12.sp, color = Color.Gray)
        Box(Modifier.width(80.dp), contentAlignment = Alignment.Center) {
            Surface(color = color.copy(alpha = 0.12f), shape = RoundedCornerShape(6.dp)) {
                Text(
                    item.calificacion.toString(),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                    color = color, fontWeight = FontWeight.Black, fontSize = 14.sp
                )
            }
        }
    }
}
