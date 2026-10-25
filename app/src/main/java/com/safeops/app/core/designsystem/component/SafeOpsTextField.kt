package com.safeops.app.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.safeops.app.core.designsystem.theme.SafeOpsTheme

/**
 * Standard SafeOps Rounded Input Field with leading icon, floating label, and state feedback.
 * Use [trailingIcon] composable slot for custom trailing content.
 * Use [trailingIconVector] for a simple icon-only trailing slot.
 */
@Composable
fun SafeOpsTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    trailingIconVector: ImageVector? = null,
    errorMessage: String? = null,
    isError: Boolean = false,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val hasError = isError || !errorMessage.isNullOrBlank()

    val shape = SafeOpsTheme.shapes.inputField

    val borderColor = when {
        hasError -> SafeOpsTheme.colors.danger
        isFocused -> SafeOpsTheme.colors.primary
        else -> SafeOpsTheme.colors.border
    }

    val backgroundColor = when {
        !enabled -> SafeOpsTheme.colors.secondarySurface.copy(alpha = 0.5f)
        else -> SafeOpsTheme.colors.surface
    }

    // Resolve which trailing composable to use
    val resolvedTrailing: @Composable (() -> Unit)? = when {
        trailingIcon != null -> trailingIcon
        trailingIconVector != null -> {
            {
                Icon(
                    imageVector = trailingIconVector,
                    contentDescription = null,
                    tint = when {
                        hasError -> SafeOpsTheme.colors.danger
                        isFocused -> SafeOpsTheme.colors.primary
                        else -> SafeOpsTheme.colors.secondaryText
                    },
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        else -> null
    }

    Column(modifier = modifier.fillMaxWidth()) {
        if (label != null) {
            Text(
                text = label,
                style = SafeOpsTheme.typography.secondaryMedium.copy(
                    color = if (hasError) SafeOpsTheme.colors.danger else SafeOpsTheme.colors.primaryText,
                    fontSize = 13.sp
                ),
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 48.dp)
                .heightIn(min = 52.dp)
                .clip(shape)
                .background(backgroundColor, shape)
                .border(
                    width = if (isFocused || hasError) 1.5.dp else 1.dp,
                    color = borderColor,
                    shape = shape
                )
                .padding(horizontal = 14.dp, vertical = if (singleLine) 0.dp else 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = when {
                            hasError -> SafeOpsTheme.colors.danger
                            isFocused -> SafeOpsTheme.colors.primary
                            else -> SafeOpsTheme.colors.secondaryText
                        },
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = if (singleLine) 4.dp else 0.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isEmpty() && placeholder != null) {
                        Text(
                            text = placeholder,
                            style = SafeOpsTheme.typography.body.copy(
                                color = SafeOpsTheme.colors.tertiaryText,
                                fontSize = 14.sp
                            )
                        )
                    }

                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        enabled = enabled,
                        singleLine = singleLine,
                        maxLines = maxLines,
                        textStyle = SafeOpsTheme.typography.body.copy(
                            color = SafeOpsTheme.colors.primaryText,
                            fontSize = 14.sp
                        ),
                        cursorBrush = SolidColor(SafeOpsTheme.colors.primary),
                        visualTransformation = visualTransformation,
                        keyboardOptions = keyboardOptions,
                        keyboardActions = keyboardActions,
                        interactionSource = interactionSource,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (resolvedTrailing != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    resolvedTrailing()
                }
            }
        }

        if (!errorMessage.isNullOrBlank()) {
            Text(
                text = errorMessage,
                style = SafeOpsTheme.typography.caption.copy(
                    color = SafeOpsTheme.colors.danger
                ),
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }
    }
}
