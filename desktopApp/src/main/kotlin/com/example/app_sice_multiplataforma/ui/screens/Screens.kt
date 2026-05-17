package com.example.app_sice_multiplataforma.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_sice_multiplataforma.model.*

private val Purple = Color(0xFF4A2C5D)

// ════════════════════════════════════════════════════════════
//  CARGA ACADÉMICA
// ════════════════════════════════════════════════════════════
@Composable
fun CargaAcademicaScreen(carga: List<MateriaCarga>, modifier: Modifier = Modifier) {
    val dias = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes")
    var tab by remember { mutableIntStateOf(0) }

    Column(modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {

        // Tabs como botones pill
        Row(
            Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(14.dp)).padding(6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            dias.forEachIndexed { i, dia ->
                val sel = tab == i
                Surface(
                    modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp)).clickable { tab = i },
                    color = if (sel) Purple else Color.Transparent
                ) {
                    Text(
                        dia.take(3).uppercase(),
                        modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        color = if (sel) Color.White else Color.Gray,
                        fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 12.sp
                    )
                }
            }
        }

        val filtradas = when (tab) {
            0 -> carga.filter { it.lunes.isNotBlank() }
            1 -> carga.filter { it.martes.isNotBlank() }
            2 -> carga.filter { it.miercoles.isNotBlank() }
            3 -> carga.filter { it.jueves.isNotBlank() }
            4 -> carga.filter { it.viernes.isNotBlank() }
            else -> emptyList()
        }

        if (filtradas.isEmpty()) {
            EmptyState("No hay clases programadas el ${dias[tab]}", Icons.Default.EventBusy)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxSize()) {
                items(filtradas) { m -> CargaCard(m, tab) }
            }
        }
    }
}

@Composable
private fun CargaCard(m: MateriaCarga, dia: Int) {
    val infoDelDia = when (dia) { 0->m.lunes; 1->m.martes; 2->m.miercoles; 3->m.jueves; 4->m.viernes; else->"" }
    val horario = infoDelDia.substringBefore(" Aula:").trim()
    val aula = if (infoDelDia.contains("Aula:")) infoDelDia.substringAfter("Aula: ").trim() else "N/A"

    Card(
        Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(Modifier.padding(0.dp)) {
            // Barra de color
            Box(Modifier.width(6.dp).fillMaxHeight().background(Purple, RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)))
            Column(Modifier.padding(16.dp).weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(m.materia, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1A1A2E))
                Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    IconLabel(Icons.Default.Person, m.docente)
                    IconLabel(Icons.Default.Schedule, horario)
                    IconLabel(Icons.Default.LocationOn, "Aula $aula")
                    if (m.grupo.isNotBlank()) IconLabel(Icons.Default.Group, "Grupo ${m.grupo}")
                }
            }
            // Créditos badge
            Box(Modifier.padding(12.dp)) {
                Surface(color = Color(0xFFF3E5F5), shape = RoundedCornerShape(10.dp)) {
                    Column(Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${m.creditos}", color = Purple, fontWeight = FontWeight.Black, fontSize = 20.sp)
                        Text("créditos", color = Purple, fontSize = 9.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun IconLabel(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Icon(icon, null, tint = Color.Gray, modifier = Modifier.size(13.dp))
        Text(text, color = Color.Gray, fontSize = 12.sp)
    }
}

// ════════════════════════════════════════════════════════════
//  CALIFICACIONES PARCIALES
// ════════════════════════════════════════════════════════════
@Composable
fun CalificacionesScreen(parciales: List<CalificacionParcial>, modifier: Modifier = Modifier) {
    if (parciales.isEmpty()) { EmptyState("Sin calificaciones parciales", Icons.Default.Spellcheck); return }

    Column(modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Resumen rápido
        val totalMaterias = parciales.size
        Text("$totalMaterias materias con calificaciones parciales",
            color = Color.Gray, fontSize = 13.sp)

        LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(parciales) { ParcialCard(it) }
        }
    }
}

@Composable
private fun ParcialCard(c: CalificacionParcial) {
    val periodos = listOf(
        "U1" to c.p1, "U2" to c.p2, "U3" to c.p3, "U4" to c.p4, "U5" to c.p5,
        "U6" to c.p6, "U7" to c.p7, "U8" to c.p8, "U9" to c.p9, "U10" to c.p10,
        "U11" to c.p11, "U12" to c.p12, "U13" to c.p13
    ).filter { !it.second.isNullOrBlank() }

    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(c.materia, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1A1A2E))
            if (periodos.isEmpty()) {
                Text("Sin calificaciones registradas", color = Color.Gray, fontSize = 12.sp)
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    periodos.forEach { (label, valor) ->
                        val num = valor?.toDoubleOrNull() ?: 0.0
                        val col = when { num >= 90 -> Color(0xFF2E7D32); num >= 70 -> Color(0xFF1565C0); else -> Color(0xFFC62828) }
                        Surface(color = col.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                            Column(Modifier.padding(horizontal = 10.dp, vertical = 6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(label, fontSize = 9.sp, color = Color.Gray)
                                Text(valor ?: "–", fontWeight = FontWeight.Black, color = col, fontSize = 15.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════════
//  CALIFICACIONES FINALES
// ════════════════════════════════════════════════════════════
@Composable
fun FinalesScreen(finales: List<CalificacionFinal>, modifier: Modifier = Modifier) {
    if (finales.isEmpty()) { EmptyState("Sin calificaciones finales", Icons.Default.AssignmentTurnedIn); return }

    val aprobadas = finales.count { (it.calificacion.toDoubleOrNull() ?: 0.0) >= 70 }
    val promedio  = finales.mapNotNull { it.calificacion.toDoubleOrNull() }.let {
        if (it.isEmpty()) 0.0 else it.average()
    }

    Column(modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Stats
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            MiniStat(Modifier.weight(1f), "Promedio", String.format("%.2f", promedio), Color(0xFF4A2C5D))
            MiniStat(Modifier.weight(1f), "Aprobadas", "$aprobadas", Color(0xFF2E7D32))
            MiniStat(Modifier.weight(1f), "Reprobadas", "${finales.size - aprobadas}", Color(0xFFC62828))
            MiniStat(Modifier.weight(1f), "Total", "${finales.size}", Color(0xFF1565C0))
        }

        // Tabla
        Card(Modifier.fillMaxWidth().weight(1f), shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
            Column {
                Row(Modifier.fillMaxWidth().background(Color(0xFFF8F5FC)).padding(horizontal = 20.dp, vertical = 10.dp)) {
                    Text("MATERIA", Modifier.weight(3f), color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("CALIFICACIÓN", Modifier.weight(1f), color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("ACREDITACIÓN", Modifier.weight(1f), color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                HorizontalDivider()
                LazyColumn {
                    items(finales) { f ->
                        FinalRow(f)
                        HorizontalDivider(color = Color(0xFFF0F0F0))
                    }
                }
            }
        }
    }
}

@Composable
private fun FinalRow(f: CalificacionFinal) {
    val num = f.calificacion.toDoubleOrNull() ?: 0.0
    val ok = num >= 70
    val color = if (ok) Color(0xFF2E7D32) else Color(0xFFC62828)
    Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Row(Modifier.weight(3f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(if (ok) Icons.Default.CheckCircle else Icons.Default.Cancel, null, tint = color, modifier = Modifier.size(15.dp))
            Text(f.materia, fontSize = 13.sp, color = Color(0xFF1A1A2E))
        }
        Box(Modifier.weight(1f)) {
            Surface(color = color.copy(alpha = 0.1f), shape = RoundedCornerShape(6.dp)) {
                Text(f.calificacion.ifBlank { "–" }, Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                    color = color, fontWeight = FontWeight.Black, fontSize = 14.sp)
            }
        }
        Text(f.acreditacion.ifBlank { "—" }, Modifier.weight(1f), color = Color.Gray, fontSize = 12.sp)
    }
}

@Composable
private fun MiniStat(modifier: Modifier, label: String, value: String, color: Color) {
    Card(modifier = modifier, shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(1.dp)) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(label, color = Color.Gray, fontSize = 11.sp)
            Text(value, color = color, fontSize = 22.sp, fontWeight = FontWeight.Black)
        }
    }
}

// ════════════════════════════════════════════════════════════
//  EMPTY STATE
// ════════════════════════════════════════════════════════════
@Composable
fun EmptyState(msg: String, icon: androidx.compose.ui.graphics.vector.ImageVector = Icons.Default.Inbox, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(icon, null, tint = Color.LightGray, modifier = Modifier.size(56.dp))
            Text(msg, color = Color.Gray, fontSize = 14.sp)
        }
    }
}
