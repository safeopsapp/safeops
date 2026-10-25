package com.safeops.app.core.designsystem.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.safeops.app.core.designsystem.theme.SafeOpsTheme

/**
 * Mixed light and dark blue gradient background (zero patterns/waves).
 * All technical curves, waves, and patterns removed as requested.
 * Retained as a no-op composable for binary/callsite compatibility.
 */
@Composable
fun SafeOpsCurvyHeroPattern(
    isDark: Boolean = SafeOpsTheme.colors.isDark,
    modifier: Modifier = Modifier
) {
    // Intentionally no-op: pure gradient color background only, no patterns.
}

/**
 * Signature SafeOps Pattern (no-op alias, all patterns removed).
 */
@Composable
fun SafeOpsBlueGradientWavePattern(
    isDark: Boolean = SafeOpsTheme.colors.isDark,
    modifier: Modifier = Modifier
) {
    // Intentionally no-op: pure gradient color background only, no patterns.
}

/**
 * Corporate executive mixed light-and-dark blue gradient.
 * Harmoniously blends radiant light blue highlights into deep royal blue and dark midnight navy
 * for an ultra-clean, modern, pattern-free aesthetic.
 */
fun safeOpsMixedBlueGradient(isDark: Boolean): Brush {
    return if (isDark) {
        Brush.verticalGradient(
            0.00f to Color(0xFF1E88E5), // Luminous light-blue accent at top
            0.16f to Color(0xFF0D47A1), // Rich royal blue
            0.46f to Color(0xFF08265E), // Deep cobalt navy
            0.78f to Color(0xFF031336), // Dark midnight blue
            1.00f to Color(0xFF010A1A)  // Darkest pure sapphire base
        )
    } else {
        Brush.verticalGradient(
            0.00f to Color(0xFF38BDF8), // Radiant light-blue accent at top
            0.14f to Color(0xFF1E88E5), // Vibrant azure
            0.38f to Color(0xFF0D47A1), // Deep royal blue
            0.70f to Color(0xFF062B66), // Dark navy
            1.00f to Color(0xFF020D22)  // Dark midnight blue base
        )
    }
}

/**
 * SafeOps Hero Gradient Header container with pure mixed light-and-dark blue background (no patterns).
 */
@Composable
fun SafeOpsGradientHeader(
    modifier: Modifier = Modifier,
    gradient: Brush = safeOpsMixedBlueGradient(SafeOpsTheme.colors.isDark),
    showPattern: Boolean = false,
    isDark: Boolean = SafeOpsTheme.colors.isDark,
    contentPadding: Dp = 24.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(gradient)
            .statusBarsPadding()
    ) {
        if (showPattern) {
            SafeOpsCurvyHeroPattern(
                isDark = isDark,
                modifier = Modifier.matchParentSize()
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding),
            contentAlignment = Alignment.Center,
            content = content
        )
    }
}

/**
 * Common Product Auth & Registration Theme Background.
 * Provides a unified visual flow across Login, Admin Registration, and Worker Registration:
 * - Radiant light-blue accent at top (0xFF38BDF8 / 0xFF1E88E5)
 * - Rich royal blue and deep cobalt in the center (0xFF0D47A1)
 * - Dark midnight navy at bottom (0xFF020D22)
 * - Subtle ambient light flare shining down from the top bar
 */
@Composable
fun SafeOpsAuthBackground(
    modifier: Modifier = Modifier,
    isDark: Boolean = SafeOpsTheme.colors.isDark,
    content: @Composable BoxScope.() -> Unit
) {
    val gradient = safeOpsMixedBlueGradient(isDark)
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        // Luminous ambient top flare
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(
                    Brush.verticalGradient(
                        0.0f to Color(0xFF38BDF8).copy(alpha = if (isDark) 0.18f else 0.22f),
                        0.5f to Color(0xFF1E88E5).copy(alpha = if (isDark) 0.08f else 0.10f),
                        1.0f to Color.Transparent
                    )
                )
        )
        content()
    }
}
