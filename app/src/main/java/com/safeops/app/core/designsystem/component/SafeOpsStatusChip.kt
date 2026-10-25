package com.safeops.app.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.safeops.app.core.designsystem.theme.SafeOpsTheme

enum class SafeOpsStatusType {
    Success,
    Warning,
    Danger,
    Info,
    Neutral
}

/**
 * Standard Status Badge/Chip.
 */
@Composable
fun SafeOpsStatusChip(
    text: String,
    modifier: Modifier = Modifier,
    statusType: SafeOpsStatusType = SafeOpsStatusType.Neutral,
    icon: ImageVector? = null
) {
    val (bgColor, textColor, borderColor) = when (statusType) {
        SafeOpsStatusType.Success -> Triple(
            SafeOpsTheme.colors.success.copy(alpha = 0.12f),
            SafeOpsTheme.colors.success,
            SafeOpsTheme.colors.success.copy(alpha = 0.3f)
        )
        SafeOpsStatusType.Warning -> Triple(
            SafeOpsTheme.colors.warning.copy(alpha = 0.12f),
            SafeOpsTheme.colors.warning,
            SafeOpsTheme.colors.warning.copy(alpha = 0.3f)
        )
        SafeOpsStatusType.Danger -> Triple(
            SafeOpsTheme.colors.danger.copy(alpha = 0.12f),
            SafeOpsTheme.colors.danger,
            SafeOpsTheme.colors.danger.copy(alpha = 0.3f)
        )
        SafeOpsStatusType.Info -> Triple(
            SafeOpsTheme.colors.primary.copy(alpha = 0.12f),
            SafeOpsTheme.colors.primary,
            SafeOpsTheme.colors.primary.copy(alpha = 0.3f)
        )
        SafeOpsStatusType.Neutral -> Triple(
            SafeOpsTheme.colors.disabled.copy(alpha = 0.15f),
            SafeOpsTheme.colors.secondaryText,
            SafeOpsTheme.colors.border
        )
    }

    val shape = RoundedCornerShape(8.dp)

    Box(
        modifier = modifier
            .clip(shape)
            .background(bgColor, shape)
            .border(width = 1.dp, color = borderColor, shape = shape)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = text,
                style = SafeOpsTheme.typography.caption.copy(
                    color = textColor,
                    fontSize = 11.sp
                )
            )
        }
    }
}
