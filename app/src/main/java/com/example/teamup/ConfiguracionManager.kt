package com.example.teamup

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "configuracion")

data class ConfiguracionData(
    val ipSocket: String,
    val puertoSocket: String,
    val ipHttp: String,
    val puertoHttp: String
)

class ConfiguracionManager(private val context: Context) { // properties en androide
    private val IPS_SOCKET = stringPreferencesKey("ip_socket")
    private val PUERTO_SOCKET = stringPreferencesKey("puerto_socket")
    private val IPS_HTTP = stringPreferencesKey("ip_http")
    private val PUERTO_HTTP = stringPreferencesKey("puerto_http")

    val flujoConfiguracion = context.dataStore.data.map { prefs ->
        ConfiguracionData(
            ipSocket = prefs[IPS_SOCKET] ?: "192.168.1.50",
            puertoSocket = prefs[PUERTO_SOCKET] ?: "12345",
            ipHttp = prefs[IPS_HTTP] ?: "192.168.1.50",
            puertoHttp = prefs[PUERTO_HTTP] ?: "8080"
        )
    }

    suspend fun guardarConfiguracion(c: ConfiguracionData) {
        context.dataStore.edit { prefs ->
            prefs[IPS_SOCKET] = c.ipSocket
            prefs[PUERTO_SOCKET] = c.puertoSocket
            prefs[IPS_HTTP] = c.ipHttp
            prefs[PUERTO_HTTP] = c.puertoHttp
        }
    }
}