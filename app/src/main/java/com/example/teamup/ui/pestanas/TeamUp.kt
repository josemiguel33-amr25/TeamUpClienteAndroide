package com.example.teamup.ui.pestanas

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.teamup.SesionViewModel
import com.example.teamup.ConfiguracionManager
import com.example.teamup.ConfiguracionData
import com.example.teamup.R
import com.example.teamup.ui.pestanas.PantallaPartidos
import com.example.teamup.ui.pestanas.PantallaMisPartidos
import com.example.teamup.ui.pestanas.PantallaRanking

import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperior(
    titulo: String,
    navegarAtras: Boolean,
    onAtrasClick: () -> Unit,
    onAjustesClick: (() -> Unit)? = null
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = titulo,
                style = MaterialTheme.typography.displayLarge,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = Color(0xFF10203B),
            navigationIconContentColor = MaterialTheme.colorScheme.primary,
            titleContentColor = Color(0xFFFFD700)
        ),
        navigationIcon = {
            if (navegarAtras) {
                IconButton(onClick = onAtrasClick) {
                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = null)
                }
            } else {
                Image(
                    painter = painterResource(R.drawable.logomejorado),
                    contentDescription = null,
                    modifier = Modifier.size(104.dp).padding(start = 24.dp)
                )
            }
        },
        actions = {
            if (onAjustesClick != null) {
                IconButton(onClick = onAjustesClick) {
                    Icon(imageVector = Icons.Filled.Settings, contentDescription = null, tint = Color(0xFFFFD700))
                }
            }
        }
    )
}

@Composable
fun TeamUpApp(tamanioVentana: WindowWidthSizeClass, viewModel: SesionViewModel) {
    val navControllerRaiz = rememberNavController()

    NavHost(navController = navControllerRaiz, startDestination = "bienvenida") {
        composable("bienvenida") {
            PantallaBienvenida(
                onPantallaTocada = { navControllerRaiz.navigate("login") },
                viewModel = viewModel
            )
        }
        composable("login") {
            PantallaLogin(
                viewModel = viewModel,
                onAtras = { navControllerRaiz.popBackStack() },
                onLoginCorrecto = {
                    navControllerRaiz.navigate("principal") {
                        popUpTo("bienvenida") { inclusive = true }
                    }
                }
            )
        }
        composable("principal") {
            ContenedorPrincipalApp(viewModel = viewModel)
        }
    }
}

@Composable
fun PantallaBienvenida(onPantallaTocada: () -> Unit, viewModel: SesionViewModel) {
    var mostrarAjustes by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            BarraSuperior(
                titulo = "TeamUp",
                navegarAtras = false,
                onAtrasClick = {},
                onAjustesClick = { mostrarAjustes = true }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .clickable { onPantallaTocada() }
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.logomejorado),
                    contentDescription = null,
                    modifier = Modifier.size(250.dp)
                )
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "TOCA PARA EMPEZAR",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color(0xFFFFD700)
                )
            }

            if (mostrarAjustes) {
                DialogoConfiguracion(
                    viewModel = viewModel,
                    onCerrar = { mostrarAjustes = false }
                )
            }
        }
    }
}

@Composable
fun DialogoConfiguracion(viewModel: SesionViewModel, onCerrar: () -> Unit) {
    val contexto = androidx.compose.ui.platform.LocalContext.current
    val gestorConfig = remember { ConfiguracionManager(contexto) }
    val alcanceCorrutina = rememberCoroutineScope()

    var ipS by remember { mutableStateOf(viewModel.ipServidor) }
    var puertoS by remember { mutableStateOf(viewModel.puertoServidor) }
    var ipH by remember { mutableStateOf(viewModel.ipHttp) }
    var puertoH by remember { mutableStateOf(viewModel.puertoHttp) }

    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text("Configuración de Red") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = ipS, onValueChange = { ipS = it }, label = { Text("IP Sockets") })
                OutlinedTextField(value = puertoS, onValueChange = { puertoS = it }, label = { Text("Puerto Sockets") })
                OutlinedTextField(value = ipH, onValueChange = { ipH = it }, label = { Text("IP HTTP") })
                OutlinedTextField(value = puertoH, onValueChange = { puertoH = it }, label = { Text("Puerto HTTP") })
            }
        },
        confirmButton = {
            Button(onClick = {
                viewModel.ipServidor = ipS
                viewModel.puertoServidor = puertoS
                viewModel.ipHttp = ipH
                viewModel.puertoHttp = puertoH

                alcanceCorrutina.launch {
                    gestorConfig.guardarConfiguracion(ConfiguracionData(ipS, puertoS, ipH, puertoH))
                }

                viewModel.conectarAlServidor(ipS, puertoS.toInt())
                onCerrar()
            }) {
                Text("Guardar y Conectar")
            }
        }
    )
}

@Composable
fun PantallaLogin(viewModel: SesionViewModel, onAtras: () -> Unit, onLoginCorrecto: () -> Unit) {
    var correo by remember { mutableStateOf("") }
    var contrasenia by remember { mutableStateOf("") }

    LaunchedEffect(viewModel.loginExitoso) {
        if (viewModel.loginExitoso) {
            onLoginCorrecto()
        }
    }

    Scaffold(
        topBar = {
            BarraSuperior(
                titulo = "Iniciar Sesión",
                navegarAtras = true,
                onAtrasClick = onAtras
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = contrasenia,
                onValueChange = { contrasenia = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button( // remember y generar token por default en android no he programado la logica del remembertoken
                onClick = {

                    val json = """{
            "tipo": "iniciarSesion",
            "data": {
                "correo": "$correo",
                "contrasenia": "$contrasenia",
                "remember": "no",
                "generarToken": "no"
            }
        }""".replace("\n", "").replace("  ", "")
                    viewModel.enviarMensaje(json)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Entrar")
            }
        }
    }
}

@Composable
fun ContenedorPrincipalApp(viewModel: SesionViewModel) {
    val navControllerTab = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    var seleccionado by remember { mutableStateOf(0) }

    LaunchedEffect(viewModel.mensajeAlertaServidor) {
        if (viewModel.mensajeAlertaServidor.isNotEmpty()) {
            snackbarHostState.showSnackbar(viewModel.mensajeAlertaServidor)
            viewModel.mensajeAlertaServidor = ""
        }
    }

    val rutas = listOf("partidos", "mis_partidos", "ranking")
    val titulos = listOf("Partidos", "Mis Partidos", "Ranking")

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF0F192D)) {
                rutas.forEachIndexed { indice, ruta ->
                    NavigationBarItem(
                        selected = seleccionado == indice,
                        onClick = {
                            seleccionado = indice
                            navControllerTab.navigate(ruta) {
                                popUpTo(navControllerTab.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Text(
                                ruta.take(2).uppercase(),
                                color = if (seleccionado == indice) Color(0xFFFFD700) else Color.White
                            )
                        },
                        label = {
                            Text(
                                titulos[indice],
                                color = if (seleccionado == indice) Color(0xFFFFD700) else Color.White
                            )
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navControllerTab,
            startDestination = "partidos",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("partidos") { PantallaPartidos(viewModel) }
            composable("mis_partidos") { PantallaMisPartidos(viewModel) }
            composable("ranking") { PantallaRanking(viewModel) }
        }
    }
}