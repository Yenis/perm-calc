package com.permcalc.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object Palette {
    val bg = Color(0xFF12121F)
    val surface = Color(0xFF1E1E2E)
    val surfaceVariant = Color(0xFF2A2A3E)
    val accent = Color(0xFF4FC3F7)
    val red = Color(0xFFEF5350)
    val onBg = Color(0xFFE8E8F0)
    val onSurfaceVariant = Color(0xFFB0B0C8)
    val outline = Color(0xFF3A3A52)

    val digit = Color(0xFF2A2A3E)
    val operator = Color(0xFF1A3A4A)
    val equals = Color(0xFF0D47A1)
    val special = Color(0xFF1E1E2E)

    val permButton = Color(0xFF2A1A3A)
    val permButtonBorder = Color(0xFF6A1B9A)
    val permButtonText = Color(0xFFCE93D8)
    val purple = Color(0xFF6A1B9A)

    val legitimateBg = Color(0xFF1A3A1A)
    val legitimateBorder = Color(0xFF2E7D32)
    val legitimateText = Color(0xFFA5D6A7)
    val maliciousBg = Color(0xFF3A1A1A)
    val maliciousBorder = Color(0xFFC62828)
    val maliciousText = Color(0xFFEF9A9A)

    val warningBg = Color(0xFF1A0000)
    val warningBorder = Color(0xFFB71C1C)

    val muted = Color(0xFF7070A0)
    val dim = Color(0xFF9090B0)
    val divider = Color(0xFF3A1A3A)
    val dividerLabel = Color(0xFF7A3A7A)
}

@Composable
fun PermCalcTheme(content: @Composable () -> Unit) {
    val colors = darkColorScheme(
        primary = Palette.accent,
        onPrimary = Palette.bg,
        secondary = Palette.red,
        background = Palette.bg,
        onBackground = Palette.onBg,
        surface = Palette.surface,
        onSurface = Palette.onBg,
        surfaceVariant = Palette.surfaceVariant,
        onSurfaceVariant = Palette.onSurfaceVariant,
        outline = Palette.outline,
    )
    MaterialTheme(colorScheme = colors, content = content)
}
