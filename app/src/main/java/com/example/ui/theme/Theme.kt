package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ElectricViolet,
    secondary = BrightCyan,
    tertiary = CoralRed,
    background = DarkBackground,
    surface = SurfaceDarkNavy,
    surfaceVariant = SecondarySurfaceNavy,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.White,
    onBackground = PrimaryText,
    onSurface = PrimaryText,
    onSurfaceVariant = Color(0xFFD1D1E0),
    outline = InactiveMuted
)

private val LightColorScheme = lightColorScheme(
    primary = ElectricViolet,
    secondary = BrightCyan,
    tertiary = CoralRed,
    background = Color(0xFF121212),
    surface = SurfaceDarkNavy,
    surfaceVariant = SecondarySurfaceNavy,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.White,
    onBackground = PrimaryText,
    onSurface = PrimaryText,
    onSurfaceVariant = Color(0xFFD1D1E0),
    outline = InactiveMuted
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
