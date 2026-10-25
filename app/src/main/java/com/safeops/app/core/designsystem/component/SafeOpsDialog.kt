package com.safeops.app.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.safeops.app.core.designsystem.theme.SafeOpsTheme

/**
 * Standard SafeOps Accessible Dialog.
 */
@Composable
fun SafeOpsDialog(
    title: String,
    message: String,
    onDismissRequest: () -> Unit,
    confirmButtonText: String = "Confirm",
    onConfirmClick: () -> Unit,
    dismissButtonText: String? = "Cancel",
    onDismissClick: (() -> Unit)? = null,
    icon: ImageVector? = null,
    properties: DialogProperties = DialogProperties()
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties
    ) {
        val shape = SafeOpsTheme.shapes.cardFeature

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(SafeOpsTheme.colors.surface, shape)
                .border(1.dp, SafeOpsTheme.colors.border, shape)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = SafeOpsTheme.colors.primary,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Text(
                text = title,
                style = SafeOpsTheme.typography.screenTitle.copy(
                    color = SafeOpsTheme.colors.primaryText,
                    fontSize = 20.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = message,
                style = SafeOpsTheme.typography.body.copy(
                    color = SafeOpsTheme.colors.secondaryText,
                    fontSize = 14.sp
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (dismissButtonText != null && onDismissClick != null) {
                    SafeOpsOutlinedButton(
                        text = dismissButtonText,
                        onClick = onDismissClick,
                        modifier = Modifier.weight(1f),
                        height = 44.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }

                SafeOpsButton(
                    text = confirmButtonText,
                    onClick = onConfirmClick,
                    modifier = Modifier.weight(1f),
                    height = 44.dp,
                    useGradient = false
                )
            }
        }
    }
}
