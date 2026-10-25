package com.safeops.app.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Immutable
data class SafeOpsShapes(
    val extraSmall: Shape = RoundedCornerShape(6.dp),
    val small: Shape = RoundedCornerShape(12.dp),
    val medium: Shape = RoundedCornerShape(16.dp),
    val large: Shape = RoundedCornerShape(20.dp),
    val extraLarge: Shape = RoundedCornerShape(28.dp),
    val pill: Shape = RoundedCornerShape(50),
    val containerTopRounded: Shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
    val cardStandard: Shape = RoundedCornerShape(16.dp),
    val cardFeature: Shape = RoundedCornerShape(20.dp),
    val cardSmall: Shape = RoundedCornerShape(12.dp),
    val inputField: Shape = RoundedCornerShape(14.dp),
    val button: Shape = RoundedCornerShape(14.dp)
)

val LocalSafeOpsShapes = staticCompositionLocalOf { SafeOpsShapes() }
