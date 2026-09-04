package com.gemstech.permcalc.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * PermCalc wears GemsTech's Tanzanite - the gem the brand assigns to Insight.
 * The app's whole arc is a shift in what you believe about your phone, so the
 * chrome is Tanzanite blue over the brand's black canvas, cooled toward the
 * stone's blue-violet. Semantic colors stay independent of the gem: green for
 * the legitimate framing, red for the abusive one, and Citrine amber for live
 * caution - a single-hue UI cannot carry those distinctions.
 */
object Palette {
    // Ground
    val bg = Color(0xFF0A0E1A)
    val surface = Color(0xFF141A2B)
    val surfaceVariant = Color(0xFF1E2740)
    val outline = Color(0xFF2C3A5C)

    // Tanzanite
    val accent = Color(0xFF4D90FF)
    val accentDeep = Color(0xFF1E56C8)
    val violet = Color(0xFF5B4BD6)

    // Type
    val onBg = Color(0xFFE6EBF7)
    val onSurfaceVariant = Color(0xFFA8B4CE)
    val muted = Color(0xFF6B7A9E)
    val dim = Color(0xFF8B99BC)

    // Calculator keys - the innocent half of the app, deliberately unremarkable
    val digit = Color(0xFF1E2740)
    val operator = Color(0xFF172A4D)
    val equals = accentDeep
    val special = Color(0xFF141A2B)

    // Permission demos - Tanzanite's violet flank, so the row that matters
    // reads as a different kind of control from the number pad
    val permButton = Color(0xFF1C1938)
    val permButtonBorder = Color(0xFF5B4BD6)
    val permButtonText = Color(0xFFB3A6FF)

    val divider = Color(0xFF2A2450)
    val dividerLabel = Color(0xFF8A7BE0)

    // Semantic: the two framings a reveal contrasts
    val red = Color(0xFFEF5350)
    val legitimateBg = Color(0xFF0F2A1A)
    val legitimateBorder = Color(0xFF2E7D32)
    val legitimateText = Color(0xFFA5D6A7)
    val maliciousBg = Color(0xFF2E1418)
    val maliciousBorder = Color(0xFFC62828)
    val maliciousText = Color(0xFFEF9A9A)

    // Semantic: the verdict a reveal lands on
    val warningBg = Color(0xFF1F0B0E)
    val warningBorder = Color(0xFFB71C1C)

    // Semantic: Citrine - something is happening right now, not a verdict
    val cautionBg = Color(0xFF2A2007)
    val cautionBorder = Color(0xFFC79A16)
    val cautionText = Color(0xFFF5C93F)
}

@Composable
fun PermCalcTheme(content: @Composable () -> Unit) {
    val colors = darkColorScheme(
        primary = Palette.accent,
        onPrimary = Palette.bg,
        secondary = Palette.violet,
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
