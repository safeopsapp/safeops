package com.safeops.app.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class SafeOpsElevation(
    val none: Dp = 0.dp,
    val subtle: Dp = 1.dp,
    val small: Dp = 1.dp,
    val medium: Dp = 2.dp,
    val card: Dp = 0.dp,
    val dialog: Dp = 8.dp
)

val LocalSafeOpsElevation = staticCompositionLocalOf { SafeOpsElevation() }
