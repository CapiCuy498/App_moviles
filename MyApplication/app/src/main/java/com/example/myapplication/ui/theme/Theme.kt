package com.example.myapplication.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Verde80,
    onPrimary = OnVerde80,
    primaryContainer = VerdeContenedor80,
    onPrimaryContainer = OnVerdeContenedor80,
    secondary = Secundario80,
    onSecondary = OnSecundario80,
    secondaryContainer = SecundarioContenedor80,
    onSecondaryContainer = OnSecundarioContenedor80,
    tertiary = Dorado80,
    onTertiary = OnDorado80,
    tertiaryContainer = DoradoContenedor80,
    onTertiaryContainer = OnDoradoContenedor80,
    background = Fondo80,
    onBackground = Color(0xFFE0E4DE),
    surface = Fondo80,
    onSurface = Color(0xFFE0E4DE),
    surfaceVariant = SuperficieVariante80,
    onSurfaceVariant = OnSuperficieVariante80,
    outline = Contorno80,
    surfaceContainerLowest = Color(0xFF0B0F0C),
    surfaceContainerLow = Color(0xFF171C18),
    surfaceContainer = Color(0xFF1B201C),
    surfaceContainerHigh = Color(0xFF252B26),
    surfaceContainerHighest = Color(0xFF303631)
)

private val LightColorScheme = lightColorScheme(
    primary = Verde40,
    onPrimary = Color.White,
    primaryContainer = VerdeContenedor40,
    onPrimaryContainer = OnVerdeContenedor40,
    secondary = Secundario40,
    onSecondary = Color.White,
    secondaryContainer = SecundarioContenedor40,
    onSecondaryContainer = OnSecundarioContenedor40,
    tertiary = Dorado40,
    onTertiary = Color.White,
    tertiaryContainer = DoradoContenedor40,
    onTertiaryContainer = OnDoradoContenedor40,
    background = Fondo40,
    onBackground = Color(0xFF181D19),
    surface = Fondo40,
    onSurface = Color(0xFF181D19),
    surfaceVariant = SuperficieVariante40,
    onSurfaceVariant = OnSuperficieVariante40,
    outline = Contorno40,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF0F5EF),
    surfaceContainer = Color(0xFFEAF0EA),
    surfaceContainerHigh = Color(0xFFE4EAE4),
    surfaceContainerHighest = Color(0xFFDFE4DE)
)

@Composable
fun EsfocarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
