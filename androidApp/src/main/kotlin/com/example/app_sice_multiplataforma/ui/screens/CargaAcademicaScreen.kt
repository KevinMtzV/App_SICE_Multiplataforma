package com.example.app_sice_multiplataforma.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_sice_multiplataforma.model.MateriaCarga

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CargaAcademicaScreen(
    carga: List<MateriaCarga>,
    modifier: Modifier = Modifier
) {
    val dias = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes")
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val colorPrimario = MaterialTheme.colorScheme.primary
    val colorFondo = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)

    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
        // --- TABS CON ESTILO DE PÍLDORA ---
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = colorPrimario,
            contentColor = Color.White,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    height = 4.dp,
                    color = MaterialTheme.colorScheme.tertiaryContainer
                )
            },
            divider = {}
        ) {
            dias.forEachIndexed { index, dia ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = dia.take(3).uppercase(),
                            fontWeight = if (selectedTabIndex == index) FontWeight.Black else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    }
                )
            }
        }

        // --- ENCABEZADO DE DÍA ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorPrimario)
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                color = Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "Horario de ${dias[selectedTabIndex]}",
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // --- LISTADO DE MATERIAS ---
        val materiasFiltradas = when (selectedTabIndex) {
            0 -> carga.filter { it.lunes.isNotBlank() }
            1 -> carga.filter { it.martes.isNotBlank() }
            2 -> carga.filter { it.miercoles.isNotBlank() }
            3 -> carga.filter { it.jueves.isNotBlank() }
            4 -> carga.filter { it.viernes.isNotBlank() }
            else -> emptyList()
        }

        if (materiasFiltradas.isEmpty()) {
            EmptyState()
        } else {
            LazyColumn(
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(materiasFiltradas) { materia ->
                    CargaCardModern(materia, selectedTabIndex)
                }
            }
        }
    }
}

@Composable
fun CargaCardModern(materia: MateriaCarga, diaSeleccionado: Int) {
    val infoDelDia = when (diaSeleccionado) {
        0 -> materia.lunes
        1 -> materia.martes
        2 -> materia.miercoles
        3 -> materia.jueves
        4 -> materia.viernes
        else -> ""
    }

    // Extracción segura del horario y aula
    val horario = infoDelDia.substringBefore(" Aula:").trim()
    val aula = if (infoDelDia.contains("Aula:")) infoDelDia.substringAfter("Aula: ").trim() else "N/A"

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // --- TIMELINE (HORA A LA IZQUIERDA) ---
        Column(
            modifier = Modifier.width(70.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = horario.take(5), // Toma los primeros 5 caracteres (ej. 07:00)
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(60.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(MaterialTheme.colorScheme.primary, Color.Transparent)
                        )
                    )
            )
        }

        // --- TARJETA DE MATERIA ---
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = materia.materia,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Docente
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = materia.docente, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Aula Badge
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "AULA: $aula",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Schedule, null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
            Text("No hay clases programadas", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
        }
    }
}