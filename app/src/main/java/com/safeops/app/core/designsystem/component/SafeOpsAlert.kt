package com.safeops.app.core.designsystem.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.safeops.app.core.designsystem.icon.SafeOpsIcons
import com.safeops.app.core.designsystem.theme.SafeOpsTheme
import kotlinx.coroutines.delay

/**
 * Types of SafeOps Notifications & Alerts.
 */
enum class SafeOpsAlertType(
    val titleDefault: String,
    val icon: ImageVector,
    val lightColor: Color,
    val darkColor: Color
) {
    SUCCESS(
        titleDefault = "Success",
        icon = SafeOpsIcons.CheckCircle,
        lightColor = Color(0xFF16A34A),
        darkColor = Color(0xFF34D399)
    ),
    FAILURE(
        titleDefault = "Error Occurred",
        icon = SafeOpsIcons.Error,
        lightColor = Color(0xFFDC2626),
        darkColor = Color(0xFFEF4444)
    ),
    WARNING(
        titleDefault = "Warning Alert",
        icon = SafeOpsIcons.Warning,
        lightColor = Color(0xFFF59E0B),
        darkColor = Color(0xFFFBBF24)
    ),
    INFO(
        titleDefault = "Information",
        icon = SafeOpsIcons.Info,
        lightColor = Color(0xFF0878FF),
        darkColor = Color(0xFF38BDF8)
    )
}

/**
 * Universal SafeOps Notification Alert Banner (Success, Failure, Warning, Info).
 * Features glass theme styling, leading icon, optional message, action link, and dismiss button.
 */
@Composable
fun SafeOpsAlertBanner(
    type: SafeOpsAlertType,
    title: String = type.titleDefault,
    modifier: Modifier = Modifier,
    message: String? = null,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null
) {
    val isDark = SafeOpsTheme.colors.isDark
    val statusColor = if (isDark) type.darkColor else type.lightColor
    val cardBg = if (isDark) Color(0xFF18181B) else Color(0xFFFFFDFF)
    val textColor = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D)
    val subTextColor = if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(cardBg)
            .border(
                width = 1.dp,
                color = statusColor.copy(alpha = if (isDark) 0.35f else 0.25f),
                shape = RoundedCornerShape(14.dp)
            )
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // Status Icon with Soft Colored Container
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(statusColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = type.icon,
                    contentDescription = type.name,
                    tint = statusColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Text Content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )

                if (!message.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = message,
                        fontSize = 12.5.sp,
                        color = subTextColor,
                        lineHeight = 17.sp
                    )
                }

                if (actionLabel != null && onActionClick != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = actionLabel,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        modifier = Modifier.clickable { onActionClick() }
                    )
                }
            }

            // Optional Dismiss 'X' Button
            if (onDismiss != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(if (isDark) Color(0xFF27272A) else Color(0xFFF1F5F9))
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = SafeOpsIcons.Close,
                        contentDescription = "Dismiss",
                        tint = subTextColor,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

/**
 * Universal Floating Toast Notification with auto-dismiss and entrance animation.
 */
@Composable
fun SafeOpsToast(
    message: String,
    type: SafeOpsAlertType,
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    durationMs: Long = 3500L
) {
    val isDark = SafeOpsTheme.colors.isDark
    val statusColor = if (isDark) type.darkColor else type.lightColor

    LaunchedEffect(visible) {
        if (visible) {
            delay(durationMs)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { -it }, animationSpec = tween(300)) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }, animationSpec = tween(250)) + fadeOut(),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (isDark) Color(0xFF18181B) else Color(0xFFFFFDFF))
                .border(1.dp, statusColor.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = type.icon,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = message,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D)
                    )
                }
                Icon(
                    imageVector = SafeOpsIcons.Close,
                    contentDescription = "Close",
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(16.dp)
                        .clickable { onDismiss() }
                )
            }
        }
    }
}
