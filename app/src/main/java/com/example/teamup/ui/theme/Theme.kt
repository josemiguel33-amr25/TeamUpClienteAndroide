package com.example.teamup.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val EsquemaColoresTeamUp = darkColorScheme(
    primary = AzulPrimario,
    secondary = CelesteClaro,
    tertiary = OroDorado,
    background = AzulFondoOscuro,
    surface = FondoTarjeta,
    onPrimary = BlancoGris,
    onSecondary = AzulFondoOscuro,
    onBackground = BlancoGris,
    onSurface = BlancoGris,
    error = RojoError,
    outline = OroViejo
)

@Composable
fun TeamUpTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EsquemaColoresTeamUp,
        content = content
    )
}