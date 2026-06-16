package com.example.teamup

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class SesionViewModel : ViewModel() { // adaptado clase sesion javafx y derivados al view model de androide

    private var socket: Socket? = null
    private var salida: PrintWriter? = null

    var ipServidor by mutableStateOf("192.168.1.50")
    var puertoServidor by mutableStateOf("12345")
    var ipHttp by mutableStateOf("192.168.1.50")
    var puertoHttp by mutableStateOf("8080")
    var loginExitoso by mutableStateOf(false)
    var mensajeAlertaServidor by mutableStateOf("")

    val partidos = mutableStateListOf<PartidoSimplificado>()
    val inventarioJugador = mutableStateListOf<CosmeticoSimplificado>()
    val usuariosRanking = mutableStateListOf<UsuarioSimplificado>()

    fun cargarConfiguracionInicial(context: Context) {
        val gestor = ConfiguracionManager(context)
        viewModelScope.launch {
            gestor.flujoConfiguracion.collect { config ->
                ipServidor = config.ipSocket
                puertoServidor = config.puertoSocket
                ipHttp = config.ipHttp
                puertoHttp = config.puertoHttp
            }
        }
    }

    fun conectarAlServidor(ip: String, puerto: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (socket != null && !socket!!.isClosed) {
                    socket!!.close()
                }
                socket = Socket(ip, puerto)
                salida = PrintWriter(socket?.getOutputStream(), true)
                val lector = BufferedReader(InputStreamReader(socket?.getInputStream()))
                while (true) {
                    val linea = lector.readLine() ?: break
                    procesarBuzon(linea)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    mensajeAlertaServidor = "Error de conexion"
                }
            }
        }
    }

    fun enviarMensaje(jsonMensaje: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                salida?.println(jsonMensaje)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    // funcion buzon en todas partes, cliente java, servidor,  androide :)
    private suspend fun procesarBuzon(mensajeJson: String) {
        try {
            val json = JSONObject(mensajeJson)
            val opcion = json.optString("opcion")
            val status = json.optString("status")
            val mensajeServidor = json.optString("mensaje")

            withContext(Dispatchers.Main) {
                if (mensajeServidor.isNotEmpty() && mensajeServidor != "null") {
                    mensajeAlertaServidor = mensajeServidor
                }

                val datos = json.optJSONObject("datos")

                when (opcion) {
                    "iniciarSesion" -> {
                        loginExitoso = true
                    }

                    "obtenerPartidos", "verPartidos", "verMisPartidos", "obtenerPartidosUsuario" -> {
                        val arrayPartidos = datos?.getJSONArray("partidos") ?: return@withContext
                        partidos.clear()
                        for (i in 0 until arrayPartidos.length()) {
                            val p = arrayPartidos.getJSONObject(i)
                            partidos.add(PartidoSimplificado(
                                idPartido = p.getInt("idPartido"),
                                tituloPartido = p.getString("tituloPartido"),
                                ubicacion = p.getString("ubicacion"),
                                ciudad = p.getString("ciudad"),
                                precio = p.getDouble("precio"),
                                dia = p.getInt("dia"),
                                mes = p.getInt("mes"),
                                anio = p.getInt("anio"),
                                hora = p.getInt("hora"),
                                minutos = p.getInt("minutos"),
                                estado = p.getString("estado"),
                                soloVerificados = p.getBoolean("soloVerificados"),
                                nombreUsuario = p.getString("nombreUsuario"),
                                idUsuario = p.getInt("idUsuario"),
                                fotoUsuario = p.getString("fotoUsuario")
                            ))
                        }
                    }

                    "ranking" -> {
                        val arrayRanking = datos?.optJSONArray("jugadores") ?: return@withContext
                        usuariosRanking.clear()
                        for (i in 0 until arrayRanking.length()) {
                            val j = arrayRanking.optJSONObject(i) ?: continue
                            usuariosRanking.add(UsuarioSimplificado(
                                nombre = j.optString("nombre", ""),
                                rango = j.optString("rango", ""),
                                puntos = j.optInt("puntos", 0),
                                reputacion = j.optInt("reputacion", 0),
                                goles = j.optInt("goles", 0),
                                asistencias = j.optInt("asistencias", 0),
                                mvps = j.optInt("mvps", 0),
                                verificado = j.optBoolean("verificado", false)
                            ))
                        }
                    }

                    "abandonarPartido" -> {
                        if (status == "perfecto") {
                            val idPartidoAbandonado = datos?.optInt("idPartido", -1) ?: -1
                            if (idPartidoAbandonado != -1) {
                                val partidoAEliminar = partidos.find { it.idPartido == idPartidoAbandonado }
                                if (partidoAEliminar != null) {
                                    partidos.remove(partidoAEliminar)
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                salida?.println("{\"tipo\":\"salirAplicacion\"}")
                socket?.close()
            } catch (e: Exception) { }
        }
    }
}