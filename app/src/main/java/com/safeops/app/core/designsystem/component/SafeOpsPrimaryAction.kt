package com.safeops.app.core.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.safeops.app.core.designsystem.icon.SafeOpsIcons

/**
 * Standardized bottom/floating CTA primary action bar wrapper.
 */
@Composable
fun SafeOpsPrimaryAction(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    trailingIcon: ImageVector? = SafeOpsIcons.ArrowForward
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        SafeOpsButton(
            text = text,
            onClick = onClick,
            enabled = enabled,
            isLoading = isLoading,
            trailingIcon = trailingIcon,
            useGradient = true
        )
    }
}
