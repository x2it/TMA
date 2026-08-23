package com.realtor.geeksales.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

// Flat style - subtle rounding for a clean but not harsh look
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(2.dp),
    small = RoundedCornerShape(3.dp),
    medium = RoundedCornerShape(4.dp),
    large = RoundedCornerShape(6.dp),
    extraLarge = RoundedCornerShape(8.dp)
)

private val DarkScheme = darkColorScheme(
    primary = Accent,
    onPrimary = Bg,
    primaryContainer = Accent.copy(alpha = 0.15f),
    onPrimaryContainer = Accent,
    secondary = TextSecondary,
    onSecondary = Bg,
    background = Bg,
    onBackground = TextPrimary,
    surface = BgElev,
    onSurface = TextPrimary,
    surfaceVariant = BgElev2,
    onSurfaceVariant = TextSecondary,
    error = Danger,
    onError = Bg,
    outline = Divider,
    outlineVariant = Divider,
    inverseSurface = TextPrimary,
    inverseOnSurface = Bg
)

@Composable
fun GeekSalesTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}
