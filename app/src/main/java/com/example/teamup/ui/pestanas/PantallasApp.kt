package com.example.teamup.ui.pestanas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.teamup.PartidoSimplificado
import com.example.teamup.UsuarioSimplificado
import com.example.teamup.SesionViewModel

@Composable
fun PantallaPartidos(viewModel: SesionViewModel) {
    var ciudadSeleccionada by remember { mutableStateOf("Madrid") }
    var soloVerificados by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = ciudadSeleccionada,
                onValueChange = { ciudadSeleccionada = it },
                label = { Text("Ciudad") },
                modifier = Modifier.weight(1f)
            )
            Checkbox(checked = soloVerificados, onCheckedChange = { soloVerificados = it })
            Text("Verificados")
        }

        Button(
            onClick = {
                val ciudadFinal = if (ciudadSeleccionada.isBlank()) "todas" else ciudadSeleccionada
                viewModel.partidos.clear()
                val json = """{"tipo":"partidos","data":{"tipoPartido":"verPartidos","ciudad":"$ciudadFinal","soloverificados":"${if(soloVerificados) "si" else "no"}"}}"""
                viewModel.enviarMensaje(json)
            },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Text("Filtrar Partidos")
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(viewModel.partidos) { partido ->
                TarjetaPartido(partido, viewModel)
            }
        }
    }
}

@Composable
fun TarjetaPartido(partido: PartidoSimplificado, viewModel: SesionViewModel) {
    var equipoSeleccionado by remember { mutableStateOf("equipo1") }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = partido.tituloPartido, style = MaterialTheme.typography.titleLarge)
            Text("📍 ${partido.ubicacion} | 🌍 ${partido.ciudad}")

            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = equipoSeleccionado == "equipo1", onClick = { equipoSeleccionado = "equipo1" })
                Text("Eq 1")
                RadioButton(selected = equipoSeleccionado == "equipo2", onClick = { equipoSeleccionado = "equipo2" })
                Text("Eq 2")
            }

            Button(onClick = {
                val json = """{"tipo":"partidos","data":{"tipoPartido":"unirsePartido","idPartido":"${partido.idPartido}","equipo":"$equipoSeleccionado"}}"""
                viewModel.enviarMensaje(json)
            }) {
                Text("Unirse")
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PantallaMisPartidos(viewModel: SesionViewModel) {
    var estadoSeleccionado by remember { mutableStateOf("abierto") }
    val estados = listOf("abierto", "terminado", "lleno", "completado")

    LaunchedEffect(Unit) {
        viewModel.partidos.clear()
        val json = """{"tipo":"partidos","data":{"tipoPartido":"verMisPartidos","estado":"$estadoSeleccionado"}}"""
        viewModel.enviarMensaje(json)
    }

    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        Text("Estado:", style = MaterialTheme.typography.labelLarge)

        FlowRow(
            modifier = Modifier.padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            estados.forEach { estado ->
                FilterChip(
                    selected = estadoSeleccionado == estado,
                    onClick = {
                        estadoSeleccionado = estado
                        viewModel.partidos.clear()
                        val json = """{"tipo":"partidos","data":{"tipoPartido":"verMisPartidos","estado":"$estado"}}"""
                        viewModel.enviarMensaje(json)
                    },
                    label = { Text(estado.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.labelSmall) }
                )
            }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(viewModel.partidos) { partido ->
                TarjetaMisPartidos(partido, viewModel)
            }
        }
    }
}

@Composable
fun TarjetaMisPartidos(partido: PartidoSimplificado, viewModel: SesionViewModel) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = partido.tituloPartido, style = MaterialTheme.typography.titleLarge)
            Text("📍 ${partido.ubicacion} | 🌍 ${partido.ciudad}")
            Text("📅 ${partido.dia}/${partido.mes}/${partido.anio} ${partido.hora}:${partido.minutos}h")

            Button(
                onClick = {
                    val json = """{"tipo":"partidos","data":{"tipoPartido":"abandonarPartido","idPartido":"${partido.idPartido}"}}"""
                    viewModel.enviarMensaje(json)
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Abandonar")
            }
        }
    }
}


@Composable
fun PantallaRanking(viewModel: SesionViewModel) {
    var rangoSeleccionado by remember { mutableStateOf("1") } // 1=Bronce, 2=Plata, 3=Oro, 4=Elite
    var ordenSeleccionado by remember { mutableStateOf("mayor") }
    val listaRanking = viewModel.usuariosRanking

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Filtros
        Row(verticalAlignment = Alignment.CenterVertically) {
            DropdownMenuFiltro(rangoSeleccionado) { rangoSeleccionado = it }
            Spacer(modifier = Modifier.width(8.dp))
            // Selector de Orden
            Button(onClick = { ordenSeleccionado = if(ordenSeleccionado == "mayor") "menor" else "mayor" }) {
                Text("Orden: $ordenSeleccionado")
            }
        }

        Button(
            onClick = {
                val json = """{"tipo":"ranking","data":{"rango":"$rangoSeleccionado","mayorMenor":"$ordenSeleccionado"}}"""
                viewModel.enviarMensaje(json)
            },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) { Text("Recargar Ranking") }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(listaRanking) { jugador ->
                FilaRanking(jugador, viewModel)
            }
        }
    }
}

@Composable
fun FilaRanking(jugador: UsuarioSimplificado, viewModel: SesionViewModel) {
    val nombreFormateado = jugador.nombre.lowercase().replace(" ", "_")
    val urlFoto = "http://${viewModel.ipHttp}:${viewModel.puertoHttp}/fotosPerfil/$nombreFormateado.png"

    val nombreRango = when (jugador.rango) {
        "1" -> "Bronce"
        "2" -> "Plata"
        "3" -> "Oro"
        "4" -> "Elite"
        else -> jugador.rango
    }

    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = urlFoto,
                contentDescription = null,
                modifier = Modifier.size(60.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = jugador.nombre, style = MaterialTheme.typography.titleLarge)
                Text(
                    text = "Pts: ${jugador.puntos} | Rep: ${jugador.reputacion} | Rango: $nombreRango",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text("⚽ Goles: ${jugador.goles}", style = MaterialTheme.typography.bodyMedium)
                Text("🎯 Asist.: ${jugador.asistencias}", style = MaterialTheme.typography.bodyMedium)
                Text("🏆 MVPs: ${jugador.mvps}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuFiltro(rangoActual: String, onRangoSeleccionado: (String) -> Unit) {
    var expandido by remember { mutableStateOf(false) }
    val rangos = mapOf("1" to "Bronce", "2" to "Plata", "3" to "Oro", "4" to "Elite")

    Box {
        OutlinedButton(onClick = { expandido = true }) {
            Text("Rango: ${rangos[rangoActual]}")
        }
        DropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
            rangos.forEach { (id, nombre) ->
                DropdownMenuItem(
                    text = { Text(nombre) },
                    onClick = {
                        onRangoSeleccionado(id)
                        expandido = false
                    }
                )
            }
        }
    }
}