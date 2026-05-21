package com.example.app_sice_multiplataforma.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.work.WorkInfo
import com.example.app_sice_multiplataforma.ui.screens.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSicenet() {
    val snViewModel: AndroidSNViewModel = viewModel(factory = AndroidSNViewModel.Factory)
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val uiState = snViewModel.uiStateFlow.collectAsState().value
    val workInfo by snViewModel.workInfo.collectAsState()
    val context = LocalContext.current
    val isSuccess = uiState is SNUiState.Success

    // Sincronización: Cuando el login tiene éxito, cargamos la base de datos local
    LaunchedEffect(workInfo?.id, workInfo?.state) {
        when (workInfo?.state) {
            WorkInfo.State.SUCCEEDED -> {
                snViewModel.cargarDatosDesdeLocal()
            }
            WorkInfo.State.FAILED -> {
                val errorMsg = workInfo?.outputData?.getString("error") ?: "Error desconocido"
                snViewModel.snUiState = SNUiState.Error(errorMsg)
            }
            else -> Unit
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = isSuccess,
        drawerContent = {
            if (uiState is SNUiState.Success) {
                // Diseño moderno del Drawer con bordes redondeados
                ModalDrawerSheet(
                    drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp),
                    drawerContainerColor = MaterialTheme.colorScheme.surface,
                ) {
                    // --- ENCABEZADO DEL MENÚ (HEADER) ---
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF4A2C5D), // Morado Principal
                                        Color(0xFF4A2C5D).copy(alpha = 0.8f)
                                    )
                                )
                            )
                            .padding(20.dp),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        Column {
                            Surface(
                                modifier = Modifier.size(60.dp),
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.2f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = uiState.data.nombre.ifBlank { "Estudiante" },
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = uiState.data.matricula,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }

                    // --- ETIQUETA DE ÚLTIMA ACTUALIZACIÓN (REQUERIMIENTO 2.b) EN EL MENÚ ---
                    if (uiState.esOffline && uiState.ultimaSincro.isNotBlank()) {
                        Surface(
                            color = Color(0xFFFFF3E0), // Naranja suave de advertencia
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudOff,
                                    contentDescription = "Sin conexión",
                                    tint = Color(0xFFE65100),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Modo Offline - Datos del:\n${uiState.ultimaSincro}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFFE65100),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    } else if (uiState.ultimaSincro.isNotBlank()) {
                        // Si hay internet pero queremos mostrar cuándo fue la última vez que se guardó
                        Surface(
                            color = Color(0xFFE8F5E9), // Verde suave de éxito
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudDone,
                                    contentDescription = "Sincronizado",
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Actualizado: ${uiState.ultimaSincro}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF2E7D32),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // --- SECCIÓN ACADÉMICA ---
                    Text(
                        text = "ACADÉMICO",
                        modifier = Modifier.padding(horizontal = 28.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF4A2C5D), // Morado Principal
                        fontWeight = FontWeight.Bold
                    )

                    DrawerMenuItem(
                        label = "Mi Perfil",
                        icon = Icons.Default.AccountCircle,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("profile")
                        }
                    )

                    DrawerMenuItem(
                        label = "Horario / Carga",
                        icon = Icons.Default.DateRange,
                        onClick = {
                            scope.launch { drawerState.close() }
                            snViewModel.consultarCargaAcademica()
                            navController.navigate("carga")
                        }
                    )

                    DrawerMenuItem(
                        label = "Kardex",
                        icon = Icons.Default.HistoryEdu,
                        onClick = {
                            scope.launch { drawerState.close() }
                            snViewModel.consultarKardex()
                            navController.navigate("kardex")
                        }
                    )

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 28.dp, vertical = 8.dp), thickness = 0.5.dp, color = Color(0xFFF3E5F5))

                    // --- SECCIÓN CALIFICACIONES ---
                    Text(
                        text = "EVALUACIONES",
                        modifier = Modifier.padding(horizontal = 28.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF4A2C5D), // Morado Principal
                        fontWeight = FontWeight.Bold
                    )

                    DrawerMenuItem(
                        label = "Parciales",
                        icon = Icons.Default.Spellcheck,
                        onClick = {
                            scope.launch { drawerState.close() }
                            snViewModel.consultarCalificacionesUnidades()
                            navController.navigate("parciales")
                        }
                    )

                    DrawerMenuItem(
                        label = "Finales",
                        icon = Icons.Default.AssignmentTurnedIn,
                        onClick = {
                            scope.launch { drawerState.close() }
                            snViewModel.consultarCalificacionesFinales()
                            navController.navigate("finales")
                        }
                    )

                    Spacer(modifier = Modifier.weight(1f)) // Empuja el logout al fondo

                    // --- CERRAR SESIÓN ---
                    NavigationDrawerItem(
                        label = { Text("Cerrar Sesión", fontWeight = FontWeight.Bold) },
                        selected = false,
                        icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                        colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp),
                        onClick = {
                            scope.launch { drawerState.close() } // Cierra el menú al salir
                            snViewModel.logout(context)
                            navController.navigate("login") { popUpTo(0) { inclusive = true } }
                        }
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                if (isSuccess) {
                    CenterAlignedTopAppBar(
                        title = { Text("SICENET", fontWeight = FontWeight.Black, color = Color(0xFF4A2C5D)) },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.AutoMirrored.Filled.MenuOpen, contentDescription = "Menú", tint = Color(0xFF4A2C5D))
                            }
                        }
                    )
                }
            }
        ) { padding ->
            // --- AQUÍ ESTÁ EL TRUCO GLOBAL PARA MOSTRARLO EN TODAS LAS PANTALLAS ---
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // ETIQUETA VISIBLE EN TODAS LAS PANTALLAS (REQUERIMIENTO 2.b)
                if (uiState is SNUiState.Success && uiState.esOffline && uiState.ultimaSincro.isNotBlank()) {
                    Surface(
                        color = Color(0xFFFFF3E0), // Naranja suave para que resalte
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudOff,
                                contentDescription = null,
                                tint = Color(0xFFE65100),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Sin internet. Datos guardados el: ${uiState.ultimaSincro}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFE65100),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // El contenido de la pantalla (Kardex, Parciales, etc.)
                ContenidoNavegacion(navController, snViewModel, uiState, Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun DrawerMenuItem(label: String, icon: ImageVector, onClick: () -> Unit) {
    NavigationDrawerItem(
        label = { Text(label, fontWeight = FontWeight.Medium) },
        selected = false,
        icon = { Icon(icon, contentDescription = null, modifier = Modifier.size(22.dp)) },
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
        onClick = onClick,
        colors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = Color.Transparent,
            unselectedIconColor = Color.DarkGray
        )
    )
}

@Composable
fun ContenidoNavegacion(
    navController: NavHostController,
    snViewModel: AndroidSNViewModel,
    uiState: SNUiState,
    modifier: Modifier
) {
    NavHost(navController, startDestination = "login", modifier = modifier) {
        composable("login") {
            HomeScreen(
                snUiState = uiState,
                onLoginClick = { m, p, t -> snViewModel.loginYConsultarPerfil(m, p, t) }
            )
            // Navegación automática cuando uiState pasa a Success
            LaunchedEffect(uiState) {
                if (uiState is SNUiState.Success) {
                    navController.navigate("profile") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }
        }
        composable("profile") { if (uiState is SNUiState.Success) ProfileScreen(uiState.data) }
        composable("carga") { if (uiState is SNUiState.Success) CargaAcademicaScreen(uiState.cargaAcademica) }
        composable("kardex") { if (uiState is SNUiState.Success) KardexScreen(uiState.kardex) }
        composable("parciales") { if (uiState is SNUiState.Success) CalificacionesScreen(uiState.califUnidades) }
        composable("finales") { if (uiState is SNUiState.Success) FinalesScreen(uiState.califFinales) }
    }
}

//5q$S_B