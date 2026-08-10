package com.mychat.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val MyChatColorScheme = darkColorScheme(
    primary = Green, onPrimary = Black, primaryContainer = GreenSubtle, onPrimaryContainer = Green,
    secondary = White, onSecondary = Black, secondaryContainer = BlackCard, onSecondaryContainer = White,
    tertiary = GreenDim, onTertiary = Black, tertiaryContainer = GreenFaint, onTertiaryContainer = Green,
    background = Black, onBackground = White,
    surface = BlackSurface, onSurface = White, surfaceVariant = BlackCard, onSurfaceVariant = WhiteDim,
    surfaceTint = GreenFaint,
    error = WhiteSubtle, onError = Black, errorContainer = WhiteFaint, onErrorContainer = White,
    outline = GreenDim, outlineVariant = WhiteFaint,
    inverseSurface = White, inverseOnSurface = Black, inversePrimary = GreenDark,
    scrim = BlackScrim
)

@Composable
fun MyChatTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = MyChatColorScheme, typography = AppTypography, content = content)
}
