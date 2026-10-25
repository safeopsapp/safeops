package com.safeops.app.core.designsystem.component

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.safeops.app.core.designsystem.theme.SafeOpsTheme

/**
 * Common Button Sizes for SafeOps Design System.
 */
enum class SafeOpsButtonSize(
    val minHeight: Dp,
    val horizontalPadding: Dp,
    val fontSize: Int,
    val iconSize: Dp
) {
    SMALL(minHeight = 36.dp, horizontalPadding = 12.dp, fontSize = 13, iconSize = 16.dp),
    MEDIUM(minHeight = 48.dp, horizontalPadding = 18.dp, fontSize = 15, iconSize = 18.dp),
    LARGE(minHeight = 56.dp, horizontalPadding = 24.dp, fontSize = 16, iconSize = 20.dp)
}

/**
 * Primary SafeOps Action Button with Gradient / Solid style, loading indicator, and 48dp minimum touch target.
 */
@Composable
fun SafeOpsButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    height: Dp = 52.dp,
    useGradient: Boolean = true,
    backgroundColor: Color = SafeOpsTheme.colors.primary,
    contentColor: Color = Color.White
) {
    val interactionSource = remember { MutableInteractionSource() }
    val shape = SafeOpsTheme.shapes.button

    val bgModifier = when {
        !enabled -> Modifier.background(SafeOpsTheme.colors.disabled.copy(alpha = 0.4f), shape)
        useGradient -> Modifier.background(SafeOpsTheme.colors.buttonGradient, shape)
        else -> Modifier.background(backgroundColor, shape)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp)
            .height(height)
            .clip(shape)
            .then(bgModifier)
            .clickable(
                enabled = enabled && !isLoading,
                role = Role.Button,
                interactionSource = interactionSource,
                indication = ripple(color = Color.White.copy(alpha = 0.3f)),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = contentColor,
                strokeWidth = 2.5.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = if (enabled) contentColor else SafeOpsTheme.colors.disabled,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Text(
                    text = text,
                    style = SafeOpsTheme.typography.button.copy(
                        color = if (enabled) contentColor else SafeOpsTheme.colors.disabled,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                )

                if (trailingIcon != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = trailingIcon,
                        contentDescription = null,
                        tint = if (enabled) contentColor else SafeOpsTheme.colors.disabled,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

/**
 * Universal Primary Button with size variants and gradient/solid options.
 */
@Composable
fun SafeOpsPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: SafeOpsButtonSize = SafeOpsButtonSize.MEDIUM,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    icon: ImageVector? = null,
    shape: Shape = RoundedCornerShape(14.dp)
) {
    val effectiveLeadingIcon = leadingIcon ?: icon
    val interactionSource = remember { MutableInteractionSource() }
    val isDark = SafeOpsTheme.colors.isDark
    val bgBrush = if (enabled) {
        Brush.horizontalGradient(listOf(Color(0xFF0878FF), Color(0xFF005AE0)))
    } else {
        Brush.horizontalGradient(listOf(Color.Gray.copy(alpha = 0.3f), Color.Gray.copy(alpha = 0.3f)))
    }

    Box(
        modifier = modifier
            .defaultMinSize(minHeight = size.minHeight)
            .clip(shape)
            .background(bgBrush)
            .clickable(
                enabled = enabled && !isLoading,
                role = Role.Button,
                interactionSource = interactionSource,
                indication = ripple(color = Color.White.copy(alpha = 0.3f)),
                onClick = onClick
            )
            .padding(horizontal = size.horizontalPadding, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(size.iconSize + 2.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (effectiveLeadingIcon != null) {
                    Icon(
                        imageVector = effectiveLeadingIcon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(size.iconSize)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    fontSize = size.fontSize.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (enabled) Color.White else Color.White.copy(alpha = 0.5f)
                )
                if (trailingIcon != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = trailingIcon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(size.iconSize)
                    )
                }
            }
        }
    }
}

/**
 * Secondary Surface Button for safe non-primary actions.
 */
@Composable
fun SafeOpsSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: SafeOpsButtonSize = SafeOpsButtonSize.MEDIUM,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    shape: Shape = RoundedCornerShape(14.dp)
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isDark = SafeOpsTheme.colors.isDark
    val containerBg = if (isDark) Color(0xFF1E293B) else Color(0xFFE3EFFF)
    val contentColor = if (isDark) Color(0xFF38BDF8) else Color(0xFF0878FF)
    val borderColor = if (isDark) Color(0xFF333338) else Color(0xFFBFDBFE)

    Box(
        modifier = modifier
            .defaultMinSize(minHeight = size.minHeight)
            .clip(shape)
            .background(if (enabled) containerBg else containerBg.copy(alpha = 0.4f))
            .border(1.dp, if (enabled) borderColor else borderColor.copy(alpha = 0.4f), shape)
            .clickable(
                enabled = enabled && !isLoading,
                role = Role.Button,
                interactionSource = interactionSource,
                indication = ripple(color = contentColor.copy(alpha = 0.2f)),
                onClick = onClick
            )
            .padding(horizontal = size.horizontalPadding, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(size.iconSize + 2.dp),
                color = contentColor,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(size.iconSize)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    fontSize = size.fontSize.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = contentColor
                )
                if (trailingIcon != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = trailingIcon,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(size.iconSize)
                    )
                }
            }
        }
    }
}

/**
 * Destructive / Danger Button for critical actions (Delete, Reject, Emergency Revoke).
 */
@Composable
fun SafeOpsDestructiveButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: SafeOpsButtonSize = SafeOpsButtonSize.MEDIUM,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: ImageVector? = null,
    shape: Shape = RoundedCornerShape(14.dp)
) {
    val interactionSource = remember { MutableInteractionSource() }
    val dangerColor = Color(0xFFEF4444)

    Box(
        modifier = modifier
            .defaultMinSize(minHeight = size.minHeight)
            .clip(shape)
            .background(if (enabled) dangerColor.copy(alpha = 0.12f) else Color.Gray.copy(alpha = 0.1f))
            .border(1.dp, if (enabled) dangerColor.copy(alpha = 0.40f) else Color.Gray.copy(alpha = 0.2f), shape)
            .clickable(
                enabled = enabled && !isLoading,
                role = Role.Button,
                interactionSource = interactionSource,
                indication = ripple(color = dangerColor.copy(alpha = 0.2f)),
                onClick = onClick
            )
            .padding(horizontal = size.horizontalPadding, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(size.iconSize + 2.dp),
                color = dangerColor,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = dangerColor,
                        modifier = Modifier.size(size.iconSize)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    fontSize = size.fontSize.sp,
                    fontWeight = FontWeight.Bold,
                    color = dangerColor
                )
            }
        }
    }
}

/**
 * Ghost / Text Button with subtle tap feedback.
 */
@Composable
fun SafeOpsGhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: SafeOpsButtonSize = SafeOpsButtonSize.MEDIUM,
    enabled: Boolean = true,
    contentColor: Color = Color(0xFF0878FF),
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(
                enabled = enabled,
                role = Role.Button,
                interactionSource = interactionSource,
                indication = ripple(color = contentColor.copy(alpha = 0.15f)),
                onClick = onClick
            )
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = if (enabled) contentColor else Color.Gray,
                modifier = Modifier.size(size.iconSize)
            )
            Spacer(modifier = Modifier.width(6.dp))
        }
        Text(
            text = text,
            fontSize = size.fontSize.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (enabled) contentColor else Color.Gray
        )
        if (trailingIcon != null) {
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = trailingIcon,
                contentDescription = null,
                tint = if (enabled) contentColor else Color.Gray,
                modifier = Modifier.size(size.iconSize)
            )
        }
    }
}

/**
 * Floating Action Button (FAB) with elevated shadow and gradient fill.
 */
@Composable
fun SafeOpsFloatingActionButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    badgeCount: Int = 0
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .size(size)
            .shadow(elevation = 8.dp, shape = CircleShape)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF0878FF), Color(0xFF005AE0))
                )
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = Color.White),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )

        if (badgeCount > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEF4444))
                    .border(1.5.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (badgeCount > 99) "99+" else badgeCount.toString(),
                    color = Color.White,
                    style = TextStyle(
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 9.sp,
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        ),
                        lineHeightStyle = LineHeightStyle(
                            alignment = LineHeightStyle.Alignment.Center,
                            trim = LineHeightStyle.Trim.Both
                        )
                    )
                )
            }
        }
    }
}
