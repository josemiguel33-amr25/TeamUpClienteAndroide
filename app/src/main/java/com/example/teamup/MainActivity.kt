package com.example.teamup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel // IMPORTANTE
import com.example.teamup.ui.pestanas.TeamUpApp
import com.example.teamup.ui.theme.TeamUpTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TeamUpTheme {
                val sesionViewModel: SesionViewModel = viewModel()
                val contexto = androidx.compose.ui.platform.LocalContext.current

                LaunchedEffect(Unit) { sesionViewModel.cargarConfiguracionInicial(contexto) }

                LaunchedEffect(sesionViewModel.ipServidor) {
                    if (sesionViewModel.ipServidor.isNotEmpty()) {
                        sesionViewModel.conectarAlServidor(sesionViewModel.ipServidor, sesionViewModel.puertoServidor.toInt())
                    }
                }

                val windowSize = calculateWindowSizeClass(this)
                TeamUpApp(tamanioVentana = windowSize.widthSizeClass, viewModel = sesionViewModel)
            }
        }
    }
}