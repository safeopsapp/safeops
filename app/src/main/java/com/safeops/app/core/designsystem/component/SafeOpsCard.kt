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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.safeops.app.core.designsystem.theme.SafeOpsTheme

enum class SafeOpsCardType {
    Standard,
    Feature,
    Small
}

/**
 * Reusable SafeOps Card Container with refined borders, soft elevation, and consistent radius.
 */
@Composable
fun SafeOpsCard(
    modifier: Modifier = Modifier,
    cardType: SafeOpsCardType = SafeOpsCardType.Standard,
    backgroundColor: Color = SafeOpsTheme.colors.surface,
    borderColor: Color = SafeOpsTheme.colors.border,
    elevation: Dp = SafeOpsTheme.elevation.card,
    contentPadding: Dp = 16.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val shape: Shape = when (cardType) {
        SafeOpsCardType.Standard -> SafeOpsTheme.shapes.cardStandard
        SafeOpsCardType.Feature -> SafeOpsTheme.shapes.cardFeature
        SafeOpsCardType.Small -> SafeOpsTheme.shapes.cardSmall
    }

    val clickableModifier = if (onClick != null) {
        Modifier.clickable(
            role = Role.Button,
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(color = SafeOpsTheme.colors.primary.copy(alpha = 0.1f)),
            onClick = onClick
        )
    } else Modifier

    Box(
        modifier = modifier
            .shadow(elevation = elevation, shape = shape, spotColor = SafeOpsTheme.colors.primary.copy(alpha = 0.08f))
            .clip(shape)
            .background(backgroundColor, shape)
            .border(BorderStroke(1.dp, borderColor), shape)
            .then(clickableModifier)
            .padding(contentPadding),
        content = content
    )
}
