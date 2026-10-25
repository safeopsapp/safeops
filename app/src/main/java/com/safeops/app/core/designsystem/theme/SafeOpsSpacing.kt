package com.safeops.app.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class SafeOpsSpacing(
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val mediumSmall: Dp = 12.dp,
    val medium: Dp = 16.dp,
    val mediumLarge: Dp = 20.dp,
    val large: Dp = 24.dp,
    val extraLarge: Dp = 32.dp,
    val huge: Dp = 40.dp,

    // Semantic tokens
    val screenHorizontalPadding: Dp = 20.dp,
    val screenHorizontalCompact: Dp = 16.dp,
    val cardPadding: Dp = 16.dp,
    val sectionSpacing: Dp = 24.dp,
    val itemSpacing: Dp = 12.dp,
    val minTouchTarget: Dp = 48.dp
)

val LocalSafeOpsSpacing = staticCompositionLocalOf { SafeOpsSpacing() }
