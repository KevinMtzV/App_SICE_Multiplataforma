package com.example.app_sice_multiplataforma.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_sice_multiplataforma.model.KardexItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KardexScreen(
    kardexList: List<KardexItem>,
    modifier: Modifier = Modifier
) {
    val semestresDisponibles = remember(kardexList) {
        kardexList.mapNotNull { it.semestre }.distinct().sortedBy { it.toIntOrNull() ?: 0 }
    }

    var semestreSeleccionado by remember { mutableStateOf<String?>(null) }

    val listaFiltrada = if (semestreSeleccionado == null) {
        kardexList
    } else {
        kardexList.filter { it.semestre == semestreSeleccionado }
    }

    // --- CÁLCULOS RÁPIDOS ---
    val promedio = remember(listaFiltrada) {
        if (listaFiltrada.isEmpty()) 0.0
        else listaFiltrada.map { it.calificacion }.average()
    }
    val aprobadas = listaFiltrada.count { it.calificacion >= 70 }

    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {

        // --- RESUMEN DE KARDEX ---
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Row(
                modifier = Modifier.padding(20.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Promedio Actual", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.labelMedium)
                    Text(
                        text = String.format("%.2f", promedio),
                        color = Color.White,
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Black
                    )
                }
                Surface(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.School, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("$aprobadas/${listaFiltrada.size} Pasadas", color = Color.White, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }

        Text(
            text = "Filtrar por Semestre",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = semestreSeleccionado == null,
                    onClick = { semestreSeleccionado = null },
                    label = { Text("General") },
                    shape = RoundedCornerShape(12.dp)
                )
            }
            items(semestresDisponibles) { semestre ->
                FilterChip(
                    selected = semestreSeleccionado == semestre,
                    onClick = { semestreSeleccionado = semestre },
                    label = { Text("Semestre $semestre") },
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        // --- LISTA DE MATERIAS ---
        if (listaFiltrada.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Sin registros", color = Color.Gray)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 20.dp, start = 20.dp, end = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(listaFiltrada) { materia ->
                    KardexItemCard(materia)
                }
            }
        }
    }
}

@Composable
fun KardexItemCard(item: KardexItem) {
    val esAprobada = item.calificacion >= 70
    val statusColor = if (esAprobada) Color(0xFF4CAF50) else Color(0xFFF44336)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
        border = BorderStroke(0.5.dp, Color.LightGray.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono de estatus lateral
            Icon(
                imageVector = if (esAprobada) Icons.Default.CheckCircle else Icons.Default.Cancel,
                contentDescription = null,
                tint = statusColor,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.materia,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${item.periodo ?: ""} ${item.anio ?: ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            // Calificación con estilo de "Badge"
            Surface(
                color = statusColor.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = item.calificacion.toString(),
                    color = statusColor,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }
    }
}