package com.safeops.app.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.safeops.app.core.designsystem.theme.SafeOpsTheme

/**
 * Translucent / Soft Layered Glass Card with high readability, subtle border, and soft tint.
 */
@Composable
fun SafeOpsGlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = SafeOpsTheme.shapes.cardStandard,
    tintColor: Color = SafeOpsTheme.colors.cardGlassTint,
    borderColor: Color = if (SafeOpsTheme.colors.isDark) {
        Color.White.copy(alpha = 0.12f)
    } else {
        SafeOpsTheme.colors.borderSubtle
    },
    contentPadding: Dp = 16.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val clickableModifier = if (onClick != null) {
        Modifier.clickable(
            role = Role.Button,
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(color = SafeOpsTheme.colors.primary.copy(alpha = 0.12f)),
            onClick = onClick
        )
    } else Modifier

    Box(
        modifier = modifier
            .clip(shape)
            .background(tintColor, shape)
            .border(BorderStroke(1.dp, borderColor), shape)
            .then(clickableModifier)
            .padding(contentPadding),
        content = content
    )
}
