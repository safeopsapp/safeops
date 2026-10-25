package com.safeops.app.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.safeops.app.core.designsystem.theme.SafeOpsTheme

/**
 * Secondary Outlined SafeOps Button with subtle border and rounded shape.
 */
@Composable
fun SafeOpsOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    icon: ImageVector? = null,
    height: Dp = 50.dp,
    borderColor: Color = SafeOpsTheme.colors.border,
    textColor: Color = SafeOpsTheme.colors.primaryText,
    backgroundColor: Color = Color.Transparent
) {
    val effectiveLeadingIcon = leadingIcon ?: icon
    val interactionSource = remember { MutableInteractionSource() }
    val shape = SafeOpsTheme.shapes.button

    Box(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp)
            .height(height)
            .clip(shape)
            .background(backgroundColor, shape)
            .border(
                border = BorderStroke(1.dp, if (enabled) borderColor else SafeOpsTheme.colors.borderSubtle),
                shape = shape
            )
            .clickable(
                enabled = enabled && !isLoading,
                role = Role.Button,
                interactionSource = interactionSource,
                indication = ripple(color = SafeOpsTheme.colors.primary.copy(alpha = 0.15f)),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = SafeOpsTheme.colors.primary,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                if (effectiveLeadingIcon != null) {
                    Icon(
                        imageVector = effectiveLeadingIcon,
                        contentDescription = null,
                        tint = if (enabled) textColor else SafeOpsTheme.colors.disabled,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Text(
                    text = text,
                    style = SafeOpsTheme.typography.button.copy(
                        color = if (enabled) textColor else SafeOpsTheme.colors.disabled,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                )

                if (trailingIcon != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = trailingIcon,
                        contentDescription = null,
                        tint = if (enabled) textColor else SafeOpsTheme.colors.disabled,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
