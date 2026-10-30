package com.example.myapplication.ui.main

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.EsfocarApplication
import com.example.myapplication.data.TipoDocumento
import com.example.myapplication.ui.auth.AuthViewModel
import com.example.myapplication.ui.documentos.DocumentosScreen
import com.example.myapplication.ui.documentos.DocumentosViewModel
import com.example.myapplication.ui.navigation.Rutas
import kotlinx.coroutines.launch

private data class Destino(val ruta: String, val titulo: String, val icono: ImageVector)

private val destinos = listOf(
    Destino(Rutas.INICIO, "Inicio", Icons.Filled.Home),
    Destino(Rutas.DOCUMENTOS, "Documentos", Icons.AutoMirrored.Filled.List),
    Destino(Rutas.PERFIL, "Perfil", Icons.Filled.Person)
)

/**
 * Pantalla principal con: menú lateral (Drawer), barra superior, barra de navegación inferior
 * y un NavHost interno para Inicio / Documentos / Perfil / Cerrar sesión.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    authViewModel: AuthViewModel,
    onSesionCerrada: () -> Unit
) {
    val context = LocalContext.current
    val container = (context.applicationContext as EsfocarApplication).container

    // ViewModel de documentos: vive mientras dure esta pantalla (se destruye al cerrar sesión)
    val docViewModel: DocumentosViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                DocumentosViewModel(container.documentoRepository, container.session.userId)
            }
        }
    )
    val usuario by authViewModel.usuarioActual.collectAsStateWithLifecycle()
    val documentos by docViewModel.documentos.collectAsStateWithLifecycle()

    val innerNav = rememberNavController()
    val backStackEntry by innerNav.currentBackStackEntryAsState()
    val rutaActual = backStackEntry?.destination?.route

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var menuAbierto by remember { mutableStateOf(false) }
    var mostrarAcercaDe by remember { mutableStateOf(false) }

    // Mensajes (snackbar) que emite el ViewModel de documentos
    LaunchedEffect(Unit) {
        docViewModel.mensajes.collect { snackbarHostState.showSnackbar(it) }
    }

    // Selector de archivos nativo del sistema (PDF / Word)
    val selectorArchivos = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) docViewModel.onArchivoSeleccionado(uri)
    }

    fun navegarA(ruta: String) {
        innerNav.navigate(ruta) {
            popUpTo(innerNav.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun cerrarMenuYNavegar(ruta: String) {
        scope.launch { drawerState.close() }
        navegarA(ruta)
    }

    val titulo = when (rutaActual) {
        Rutas.INICIO -> "Inicio"
        Rutas.DOCUMENTOS -> "Mis documentos"
        Rutas.PERFIL -> "Mi perfil"
        Rutas.LOGOUT -> "Cerrar sesión"
        else -> "ESFOCAR Estudio"
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                // Cabecera del menú con los datos del usuario
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(24.dp)
                ) {
                    Avatar(nombre = usuario?.nombre ?: "?", tamano = 56.dp)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        usuario?.nombre ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        usuario?.correo ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(Modifier.height(12.dp))
                destinos.forEach { destino ->
                    NavigationDrawerItem(
                        label = { Text(destino.titulo) },
                        icon = { Icon(destino.icono, contentDescription = null) },
                        selected = rutaActual == destino.ruta,
                        onClick = { cerrarMenuYNavegar(destino.ruta) },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
                HorizontalDivider(Modifier.padding(vertical = 8.dp, horizontal = 16.dp))
                NavigationDrawerItem(
                    label = { Text("Acerca de") },
                    icon = { Icon(Icons.Filled.Info, contentDescription = null) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        mostrarAcercaDe = true
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("Cerrar sesión") },
                    icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null) },
                    selected = rutaActual == Rutas.LOGOUT,
                    onClick = { cerrarMenuYNavegar(Rutas.LOGOUT) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        AnimatedContent(
                            targetState = titulo,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "tituloBarra"
                        ) { texto -> Text(texto) }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Abrir menú")
                        }
                    },
                    actions = {
                        IconButton(onClick = { menuAbierto = true }) {
                            Icon(Icons.Filled.MoreVert, contentDescription = "Más opciones")
                        }
                        DropdownMenu(expanded = menuAbierto, onDismissRequest = { menuAbierto = false }) {
                            DropdownMenuItem(
                                text = { Text("Mi perfil") },
                                leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                                onClick = { menuAbierto = false; navegarA(Rutas.PERFIL) }
                            )
                            DropdownMenuItem(
                                text = { Text("Acerca de") },
                                leadingIcon = { Icon(Icons.Filled.Info, contentDescription = null) },
                                onClick = { menuAbierto = false; mostrarAcercaDe = true }
                            )
                            DropdownMenuItem(
                                text = { Text("Cerrar sesión") },
                                leadingIcon = {
                                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                                },
                                onClick = { menuAbierto = false; navegarA(Rutas.LOGOUT) }
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            },
            bottomBar = {
                NavigationBar {
                    destinos.forEach { destino ->
                        NavigationBarItem(
                            selected = rutaActual == destino.ruta,
                            onClick = { navegarA(destino.ruta) },
                            icon = { Icon(destino.icono, contentDescription = destino.titulo) },
                            label = { Text(destino.titulo) }
                        )
                    }
                }
            },
            floatingActionButton = {
                AnimatedVisibility(
                    visible = rutaActual == Rutas.DOCUMENTOS,
                    enter = scaleIn() + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    ExtendedFloatingActionButton(
                        onClick = { selectorArchivos.launch(TipoDocumento.MIMES_PERMITIDOS) },
                        icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                        text = { Text("Subir") }
                    )
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            NavHost(
                navController = innerNav,
                startDestination = Rutas.INICIO,
                modifier = Modifier.padding(innerPadding),
                enterTransition = { fadeIn() + slideInVertically(initialOffsetY = { it / 20 }) },
                exitTransition = { fadeOut() },
                popEnterTransition = { fadeIn() },
                popExitTransition = { fadeOut() }
            ) {
                composable(Rutas.INICIO) {
                    InicioScreen(
                        nombre = usuario?.nombre ?: "",
                        totalDocumentos = documentos.size,
                        onIrADocumentos = { navegarA(Rutas.DOCUMENTOS) }
                    )
                }
                composable(Rutas.DOCUMENTOS) {
                    DocumentosScreen(viewModel = docViewModel)
                }
                composable(Rutas.PERFIL) {
                    PerfilScreen(
                        usuario = usuario,
                        totalDocumentos = documentos.size,
                        onCerrarSesion = { navegarA(Rutas.LOGOUT) }
                    )
                }
                composable(Rutas.LOGOUT) {
                    LogoutScreen(
                        onCancelar = { navegarA(Rutas.INICIO) },
                        onConfirmar = {
                            authViewModel.cerrarSesion()
                            onSesionCerrada()
                        }
                    )
                }
            }
        }
    }

    if (mostrarAcercaDe) {
        AlertDialog(
            onDismissRequest = { mostrarAcercaDe = false },
            title = { Text("ESFOCAR Estudio") },
            text = {
                Text(
                    "MVP académico de la asignatura DSY1105 (Desarrollo de Aplicaciones Móviles). " +
                        "Herramienta de estudio, repaso y autoevaluación. " +
                        "Usa únicamente datos ficticios y funciona sin conexión a internet."
                )
            },
            confirmButton = {
                TextButton(onClick = { mostrarAcercaDe = false }) { Text("Cerrar") }
            }
        )
    }
}
