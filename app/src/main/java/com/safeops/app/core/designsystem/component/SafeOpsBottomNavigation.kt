package com.safeops.app.core.designsystem.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.safeops.app.core.designsystem.theme.SafeOpsTheme

data class SafeOpsNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val badgeCount: Int = 0
)

private val NavActive = Color(0xFF0878FF)
private val NavBadgeRed = Color(0xFFEF4444)

/**
 * SafeOps bottom navigation.
 *
 * - Hairline divider on top only (no boxed outline).
 * - Animated pill indicator behind the selected icon.
 * - Optional [centerAction] slot (e.g. [SafeOpsNavCenterButton]) rendered in the
 *   middle of the items; with 4 items it yields a clean 2 + action + 2 layout.
 * - Background extends behind the system gesture bar; content stays above it.
 */
@Composable
fun SafeOpsBottomNavigation(
    items: List<SafeOpsNavItem>,
    currentRoute: String?,
    onItemClick: (SafeOpsNavItem) -> Unit,
    modifier: Modifier = Modifier,
    isDark: Boolean = SafeOpsTheme.colors.isDark,
    centerAction: (@Composable RowScope.() -> Unit)? = null
) {
    val container = if (isDark) Color(0xFF18181B) else Color(0xFFFFFDFF)
    val hairline = if (isDark) Color(0xFF2E2E33) else Color(0xFFE4E2E9)
    val centerIndex = items.size / 2

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(container)
            .navigationBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(hairline)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                if (centerAction != null && index == centerIndex) {
                    centerAction.invoke(this)
                }
                SafeOpsBottomNavItem(
                    item = item,
                    selected = currentRoute == item.route,
                    isDark = isDark,
                    containerColor = container,
                    onClick = { onItemClick(item) }
                )
            }
        }
    }
}

@Composable
private fun RowScope.SafeOpsBottomNavItem(
    item: SafeOpsNavItem,
    selected: Boolean,
    isDark: Boolean,
    containerColor: Color,
    onClick: () -> Unit
) {
    val inactive = if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E)

    val tint by animateColorAsState(
        targetValue = if (selected) NavActive else inactive,
        animationSpec = tween(durationMillis = 200),
        label = "nav_tint"
    )
    val pill by animateColorAsState(
        targetValue = if (selected) NavActive.copy(alpha = if (isDark) 0.20f else 0.12f) else Color.Transparent,
        animationSpec = tween(durationMillis = 200),
        label = "nav_pill"
    )

    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .selectable(
                selected = selected,
                role = Role.Tab,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .background(pill, RoundedCornerShape(16.dp))
                .padding(horizontal = 18.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(22.dp)
            )
            if (item.badgeCount > 0) {
                NavBadge(
                    count = item.badgeCount,
                    ringColor = containerColor,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 8.dp, y = (-6).dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = item.label,
            color = tint,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun NavBadge(
    count: Int,
    ringColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .defaultMinSize(minWidth = 16.dp, minHeight = 16.dp)
            .background(NavBadgeRed, CircleShape)
            .border(1.5.dp, ringColor, CircleShape)
            .padding(horizontal = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (count > 9) "9+" else count.toString(),
            style = TextStyle(
                color = Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 9.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = false),
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Center,
                    trim = LineHeightStyle.Trim.Both
                )
            )
        )
    }
}

/**
 * Center "create / scan" action for [SafeOpsBottomNavigation.centerAction].
 * Rounded-square (not a circle) so it reads as part of the bar, with a soft blue glow.
 */
@Composable
fun RowScope.SafeOpsNavCenterButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = modifier
            .weight(1f)
            .fillMaxHeight(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = shape,
                    ambientColor = NavActive.copy(alpha = 0.25f),
                    spotColor = NavActive.copy(alpha = 0.45f)
                )
                .background(
                    Brush.linearGradient(listOf(Color(0xFF2E8FFF), Color(0xFF0661E8))),
                    shape
                )
                .border(1.dp, Color.White.copy(alpha = 0.25f), shape)
                .clickable(role = Role.Button, onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}