package com.safeops.app.feature.dashboards.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.safeops.app.R
import com.safeops.app.core.designsystem.component.SafeOpsAvatar
import com.safeops.app.core.designsystem.component.SafeOpsIconBadge
import com.safeops.app.core.designsystem.icon.SafeOpsIcons
import com.safeops.app.core.designsystem.theme.AccentOrange
import com.safeops.app.core.designsystem.theme.SafeOpsTheme
import com.safeops.app.domain.model.Role
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.semantics.Role as SemanticsRole
import androidx.compose.ui.unit.Dp
import com.safeops.app.core.designsystem.component.SafeOpsBottomNavigation
import com.safeops.app.core.designsystem.component.SafeOpsNavCenterButton
import com.safeops.app.core.designsystem.component.SafeOpsNavItem
import java.util.Calendar

// ── 0. Premium Clean & Curvy Fluid Pattern for Hero Backgrounds ─────────────
/**
 * Decorative pattern layered over the hero gradient. Three quiet layers:
 *
 * 1. Soft light bloom (top-right) and depth shadow (bottom-left) for dimension.
 * 2. Concentric contour rings radiating from the top-right corner, hairline only.
 * 3. Three silk-like Bezier ribbons with a faded highlight edge.
 *
 * Everything is white at low alpha, so it sits correctly on both the light-theme
 * azure gradient and the dark-theme navy gradient. No grid lines, no dots.
 */
@Composable
fun SafeOpsHeroPattern(
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.clipToBounds()) {
        val w = size.width
        val h = size.height
        if (w <= 0f || h <= 0f) return@Canvas
        val hairline = 1.dp.toPx()

        // 1. Light bloom + depth
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF7DD3FC).copy(alpha = if (isDark) 0.16f else 0.34f),
                    Color.Transparent
                ),
                center = Offset(w * 0.95f, 0f),
                radius = w * 0.90f
            )
        )
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF021B4D).copy(alpha = if (isDark) 0.45f else 0.30f),
                    Color.Transparent
                ),
                center = Offset(0f, h),
                radius = w * 0.95f
            )
        )

        // 2. Contour rings from the top-right corner
        val origin = Offset(w * 1.05f, -h * 0.10f)
        val ringAlpha = if (isDark) 0.07f else 0.11f
        for (i in 0 until 6) {
            drawCircle(
                color = Color.White.copy(alpha = ringAlpha * (1f - i * 0.13f)),
                radius = w * (0.24f + i * 0.15f),
                center = origin,
                style = Stroke(width = hairline)
            )
        }

        // 3. Ribbons (back to front)
        drawHeroRibbon(w, h, 0.52f, 0.30f, 0.88f, 0.50f, fillAlpha = if (isDark) 0.06f else 0.08f, lineAlpha = if (isDark) 0.20f else 0.30f, hairline = hairline)
        drawHeroRibbon(w, h, 0.68f, 0.50f, 1.00f, 0.70f, fillAlpha = if (isDark) 0.05f else 0.07f, lineAlpha = if (isDark) 0.14f else 0.22f, hairline = hairline)
        drawHeroRibbon(w, h, 0.82f, 0.66f, 1.05f, 0.88f, fillAlpha = if (isDark) 0.04f else 0.06f, lineAlpha = if (isDark) 0.10f else 0.16f, hairline = hairline)
    }
}

private fun Path.heroWave(w: Float, h: Float, y0: Float, y1: Float, y2: Float, y3: Float) {
    moveTo(0f, h * y0)
    cubicTo(w * 0.30f, h * y1, w * 0.65f, h * y2, w, h * y3)
}

private fun DrawScope.drawHeroRibbon(
    w: Float,
    h: Float,
    y0: Float,
    y1: Float,
    y2: Float,
    y3: Float,
    fillAlpha: Float,
    lineAlpha: Float,
    hairline: Float
) {
    val fill = Path().apply {
        heroWave(w, h, y0, y1, y2, y3)
        lineTo(w, h)
        lineTo(0f, h)
        close()
    }
    drawPath(
        path = fill,
        brush = Brush.verticalGradient(
            colors = listOf(Color.White.copy(alpha = fillAlpha), Color.Transparent),
            startY = h * 0.30f,
            endY = h
        )
    )

    val edge = Path().apply { heroWave(w, h, y0, y1, y2, y3) }
    drawPath(
        path = edge,
        brush = Brush.horizontalGradient(
            0f to Color.Transparent,
            0.5f to Color.White.copy(alpha = lineAlpha),
            1f to Color.Transparent,
            startX = 0f,
            endX = w
        ),
        style = Stroke(width = hairline * 1.2f)
    )
}

// ── 1. Hero Blue Section ─────────────────────────────────────────────────
/**
 * Premium hero banner.
 *
 * Light: bright azure fading to deep sapphire.  Dark: deep navy, so the banner
 * never glares against a dark UI.  Over the gradient sits [SafeOpsHeroPattern],
 * and the bottom corners are rounded so the banner reads as a card-like header.
 */
@Composable
fun SafeOpsDashboardHero(
    userProfile: UserProfileMock,
    isDark: Boolean,
    onMenuClick: () -> Unit = {},
    onNotificationClick: () -> Unit,
    onProfileClick: () -> Unit,
    onProjectContextClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentBottomPadding: Dp = 32.dp
) {
    val heroShape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
    val gradient = remember(isDark) {
        if (isDark) {
            Brush.linearGradient(
                0.00f to Color(0xFF14408F),
                0.55f to Color(0xFF0B2F73),
                1.00f to Color(0xFF071C47)
            )
        } else {
            Brush.linearGradient(
                0.00f to Color(0xFF2E8FFF),
                0.50f to Color(0xFF0A64E9),
                1.00f to Color(0xFF0641AA)
            )
        }
    }

    val greeting = remember {
        when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 4..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            else -> "Good evening"
        }
    }
    val firstName = remember(userProfile.name) {
        userProfile.name.split(" ").firstOrNull()?.takeIf { it.isNotBlank() } ?: userProfile.name
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(heroShape)
            .background(gradient)
    ) {
        SafeOpsHeroPattern(
            isDark = isDark,
            modifier = Modifier.matchParentSize()
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
                .padding(top = 12.dp, bottom = contentBottomPadding)
        ) {
            // Top bar: menu + wordmark  |  bell + avatar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.14f))
                            .border(1.dp, Color.White.copy(alpha = 0.22f), CircleShape)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(bounded = false),
                                role = SemanticsRole.Button,
                                onClick = onMenuClick
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = SafeOpsIcons.Menu,
                            contentDescription = "Open navigation menu",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Safe",
                                style = SafeOpsTheme.typography.screenTitle.copy(
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.4.sp
                                )
                            )
                            Text(
                                text = "Ops",
                                style = SafeOpsTheme.typography.screenTitle.copy(
                                    color = AccentOrange,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.4.sp
                                )
                            )
                        }
                        Text(
                            text = "Safety • Compliance • Operations",
                            style = SafeOpsTheme.typography.caption.copy(
                                color = Color.White.copy(alpha = 0.72f),
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    SafeOpsIconBadge(
                        icon = SafeOpsIcons.Notifications,
                        contentDescription = "Notifications",
                        onClick = onNotificationClick,
                        badgeCount = 2,
                        badgeColor = Color(0xFFEF4444)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    SafeOpsAvatar(
                        initials = userProfile.initials,
                        onClick = onProfileClick,
                        size = 40.dp
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Greeting
            Text(
                text = greeting,
                style = SafeOpsTheme.typography.body.copy(
                    color = Color.White.copy(alpha = 0.80f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                )
            )
            Text(
                text = firstName,
                style = SafeOpsTheme.typography.display.copy(
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.2.sp
                )
            )
            Text(
                text = "${userProfile.siteLocation} • ${userProfile.role.displayName}",
                color = Color.White.copy(alpha = 0.72f),
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Frosted profile card
            val cardShape = RoundedCornerShape(20.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(cardShape)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = if (isDark) 0.10f else 0.20f),
                                Color.White.copy(alpha = if (isDark) 0.04f else 0.10f)
                            )
                        )
                    )
                    .border(
                        1.dp,
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.40f),
                                Color.White.copy(alpha = 0.08f)
                            )
                        ),
                        cardShape
                    )
                    .clickable(role = SemanticsRole.Button, onClick = onProfileClick)
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(Color(0xFF38BDF8), Color(0xFF0878FF)))
                        )
                        .border(1.5.dp, Color.White.copy(alpha = 0.60f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userProfile.initials,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = userProfile.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = userProfile.roleTitle,
                        fontSize = 11.5.sp,
                        color = Color.White.copy(alpha = 0.82f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        HeroChip(text = userProfile.id)
                        HeroChip(text = userProfile.project, modifier = Modifier.weight(1f, fill = false))
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = SafeOpsIcons.ArrowForward,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.70f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun HeroChip(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(Color.White.copy(alpha = 0.16f), RoundedCornerShape(6.dp))
            .padding(horizontal = 7.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// ── 2. Full-Width Floating Context Card ──────────────────────────────────
@Composable
fun DashboardContextCard(
    title: String,
    primaryText: String,
    secondaryText: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isDark: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardBg = if (isDark) Color(0xFF18181B) else Color(0xFFFFFDFF)
    val borderCol = if (isDark) Color(0xFF333338) else Color(0xFFE4E2E9)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(cardBg)
            .border(1.dp, borderCol, RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple()
            ) { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0878FF).copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color(0xFF0878FF),
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = title,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E)
                    )
                    Text(
                        text = primaryText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = secondaryText,
                        fontSize = 12.sp,
                        color = if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Icon(
                imageVector = SafeOpsIcons.ArrowForward,
                contentDescription = null,
                tint = if (isDark) Color(0xFFA6A3AA) else Color(0xFF94A3B8),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// ── 3. KPI Strip Component (4 equal columns in ONE surface) ──────────────
@Composable
fun DashboardKpiStrip(
    metrics: List<KpiMetric>,
    isDark: Boolean,
    onKpiClick: (KpiMetric) -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (isDark) Color(0xFF18181B) else Color(0xFFFFFDFF)
    val borderCol = if (isDark) Color(0xFF333338) else Color(0xFFE4E2E9)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .border(1.dp, borderCol, RoundedCornerShape(20.dp))
            .padding(vertical = 14.dp, horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            metrics.forEachIndexed { index, kpi ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple()
                        ) { onKpiClick(kpi) }
                        .padding(vertical = 4.dp)
                ) {
                    val iconTint = if (isDark) {
                        when (kpi.iconTint) {
                            Color(0xFF0878FF) -> Color(0xFF38BDF8)
                            Color(0xFFE67E22) -> Color(0xFFFB923C)
                            Color(0xFF27AE60) -> Color(0xFF34D399)
                            Color(0xFFE74C3C) -> Color(0xFFF87171)
                            Color(0xFF9B59B6) -> Color(0xFFA78BFA)
                            else -> kpi.iconTint
                        }
                    } else kpi.iconTint

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                if (isDark) iconTint.copy(alpha = 0.22f)
                                else kpi.containerColor
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = kpi.icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = kpi.count,
                        style = SafeOpsTheme.typography.screenTitle.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D)
                        )
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = kpi.label,
                        style = SafeOpsTheme.typography.caption.copy(
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDark) Color(0xFFA6A3AA) else Color(0xFF475569)
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = kpi.subtitle,
                        style = SafeOpsTheme.typography.caption.copy(
                            fontSize = 9.sp,
                            color = if (isDark) Color(0xFF77767E) else Color(0xFF94A3B8)
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (index < metrics.size - 1) {
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(48.dp)
                            .background(if (isDark) Color(0xFF333338) else Color(0xFFF1F5F9))
                    )
                }
            }
        }
    }
}

// ── 4. Quick Actions Horizontal Scroll ───────────────────────────────────
@Composable
fun DashboardQuickActions(
    actions: List<QuickActionItem>,
    isDark: Boolean,
    onActionClick: (QuickActionItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Quick Actions",
                style = SafeOpsTheme.typography.sectionTitle.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D)
                )
            )
            Text(
                text = "View All ›",
                style = SafeOpsTheme.typography.caption.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF0878FF)
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { }
                    .padding(4.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp)
        ) {
            items(actions, key = { it.id }) { action ->
                val cardBg = if (isDark) Color(0xFF18181B) else Color(0xFFFFFDFF)
                val borderCol = if (isDark) Color(0xFF333338) else Color(0xFFE4E2E9)

                Box(
                    modifier = Modifier
                        .width(142.dp)
                        .height(118.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(cardBg)
                        .border(1.dp, borderCol, RoundedCornerShape(18.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple()
                        ) { onActionClick(action) }
                        .padding(14.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val actionTint = if (isDark) {
                                when (action.iconTint) {
                                    Color(0xFF0878FF) -> Color(0xFF38BDF8)
                                    Color(0xFFE67E22) -> Color(0xFFFB923C)
                                    Color(0xFF27AE60) -> Color(0xFF34D399)
                                    Color(0xFFE74C3C) -> Color(0xFFF87171)
                                    Color(0xFF9B59B6) -> Color(0xFFA78BFA)
                                    else -> action.iconTint
                                }
                            } else action.iconTint

                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isDark) actionTint.copy(alpha = 0.22f)
                                        else action.iconContainerColor
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = action.icon,
                                    contentDescription = null,
                                    tint = actionTint,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Icon(
                                imageVector = SafeOpsIcons.ArrowForward,
                                contentDescription = null,
                                tint = if (isDark) Color(0xFFA6A3AA) else Color(0xFFCBD5E1),
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        Column {
                            Text(
                                text = action.title,
                                style = SafeOpsTheme.typography.cardTitle.copy(
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D)
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = action.description,
                                style = SafeOpsTheme.typography.caption.copy(
                                    fontSize = 10.5.sp,
                                    color = if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E)
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── 5. Donut Chart Card (Compose Canvas rendered) ────────────────────────
@Composable
fun DashboardDonutChartCard(
    title: String,
    centerCount: String,
    centerLabel: String,
    items: List<Pair<String, Pair<Int, Color>>>,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val bg = if (isDark) Color(0xFF18181B) else Color(0xFFFFFDFF)
    val borderCol = if (isDark) Color(0xFF333338) else Color(0xFFE4E2E9)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .border(1.dp, borderCol, RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = SafeOpsTheme.typography.cardTitle.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D)
                    )
                )
                Text(
                    text = "This Month ▾",
                    fontSize = 12.sp,
                    color = Color(0xFF0878FF),
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val total = items.sumOf { it.second.first }.coerceAtLeast(1)
                Box(
                    modifier = Modifier.size(110.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidth = 14.dp.toPx()
                        var startAngle = -90f

                        items.forEach { (_, data) ->
                            val sweepAngle = (data.first.toFloat() / total.toFloat()) * 360f
                            drawArc(
                                color = data.second,
                                startAngle = startAngle,
                                sweepAngle = sweepAngle - 3f,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                            startAngle += sweepAngle
                        }
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = centerCount,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D)
                        )
                        Text(
                            text = centerLabel,
                            fontSize = 10.sp,
                            color = if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(20.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items.forEach { (label, data) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(data.second)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    color = if (isDark) Color(0xFFA6A3AA) else Color(0xFF334155)
                                )
                            }
                            Text(
                                text = data.first.toString(),
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── 6. Permit Item Card ──────────────────────────────────────────────────
@Composable
fun DashboardPermitCard(
    permit: PermitMock,
    isDark: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (isDark) Color(0xFF18181B) else Color(0xFFFFFDFF)
    val borderCol = if (isDark) Color(0xFF333338) else Color(0xFFE4E2E9)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .border(1.dp, borderCol, RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple()
            ) { onClick() }
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(permit.statusColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = SafeOpsIcons.Document,
                    contentDescription = null,
                    tint = permit.statusColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = permit.permitNumber,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0878FF)
                    )
                    Text(
                        text = " • ${permit.typeName}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D)
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = permit.location,
                    fontSize = 11.5.sp,
                    color = if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E)
                )

                Text(
                    text = "${permit.dateRange}  ${permit.timeRange}",
                    fontSize = 10.5.sp,
                    color = if (isDark) Color(0xFF77767E) else Color(0xFF94A3B8)
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(permit.statusColor.copy(alpha = 0.12f))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = permit.statusText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = permit.statusColor
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            Icon(
                imageVector = SafeOpsIcons.ArrowForward,
                contentDescription = null,
                tint = if (isDark) Color(0xFFA6A3AA) else Color(0xFFCBD5E1),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

// ── 7. Timeline Item Component ───────────────────────────────────────────
@Composable
fun DashboardTimelineItem(
    item: ScheduleItemMock,
    isDark: Boolean,
    isLast: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = item.time,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDark) Color(0xFFA6A3AA) else Color(0xFF475569),
            modifier = Modifier.width(68.dp)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(item.tagColor)
            )

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(38.dp)
                        .background(if (isDark) Color(0xFF333338) else Color(0xFFE4E2E9))
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D)
            )
            Text(
                text = item.location,
                fontSize = 11.5.sp,
                color = if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E)
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(item.tagColor.copy(alpha = 0.12f))
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Text(
                text = item.statusTag,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = item.tagColor
            )
        }
    }
}

// ── 8. Bottom Navigation (role-aware wrapper around SafeOpsBottomNavigation) ──
@Composable
fun SafeOpsDashboardBottomNavigation(
    currentRoute: String,
    role: Role,
    onNavigate: (String) -> Unit,
    onCreateClick: () -> Unit,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val items = remember(role) {
        listOf(
            SafeOpsNavItem(
                route = "home",
                label = "Home",
                icon = SafeOpsIcons.Home
            ),
            SafeOpsNavItem(
                route = "projects",
                label = when (role) {
                    Role.WORKER -> "My Work"
                    Role.SAFETY_OFFICER -> "Reviews"
                    else -> "Projects"
                },
                icon = SafeOpsIcons.Business
            ),
            SafeOpsNavItem(
                route = "permits",
                label = when (role) {
                    Role.SAFETY_OFFICER -> "Inspections"
                    Role.WORKER -> "Safety"
                    else -> "Permits"
                },
                icon = SafeOpsIcons.Document
            ),
            SafeOpsNavItem(
                route = "profile",
                label = "Profile",
                icon = SafeOpsIcons.Person
            )
        )
    }

    val selectedRoute =
        if (currentRoute == "home" || currentRoute.endsWith("dashboard")) "home" else currentRoute

    SafeOpsBottomNavigation(
        items = items,
        currentRoute = selectedRoute,
        onItemClick = { onNavigate(it.route) },
        modifier = modifier,
        isDark = isDark,
        centerAction = {
            SafeOpsNavCenterButton(
                onClick = onCreateClick,
                icon = SafeOpsIcons.Add,
                contentDescription = "Create"
            )
        }
    )
}

// ── 8. SafeOps Sidebar Drawer (Hamburger Menu Navigation) ────────────────
@Composable
fun SafeOpsSidebarDrawer(
    userProfile: UserProfileMock,
    activeRole: Role,
    onRoleSelected: (Role) -> Unit,
    onNavigate: (String) -> Unit,
    onLogoutClick: () -> Unit,
    onClose: () -> Unit,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val drawerBg = if (isDark) Color(0xFF18181B) else Color(0xFFFFFDFF)
    val textColor = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D)
    val subTextColor = if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E)
    val cardBg = if (isDark) Color(0xFF27272A) else Color(0xFFF5F4FA)
    val borderColor = if (isDark) Color(0xFF333338) else Color(0xFFE4E2E9)

    ModalDrawerSheet(
        modifier = modifier.width(320.dp),
        drawerContainerColor = drawerBg,
        drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            // Header: SafeOps Brand + Close Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Safe",
                        style = SafeOpsTheme.typography.screenTitle.copy(
                            color = if (isDark) Color.White else Color(0xFF06233F),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                    Text(
                        "Ops",
                        style = SafeOpsTheme.typography.screenTitle.copy(
                            color = AccentOrange,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF0878FF).copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            "PRO",
                            color = Color(0xFF0878FF),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(cardBg)
                        .clickable { onClose() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = SafeOpsIcons.Close,
                        contentDescription = "Close Menu",
                        tint = textColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // User Profile Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(cardBg)
                    .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF38BDF8), Color(0xFF0878FF))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userProfile.initials,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    val roleColor = when (userProfile.role) {
                        Role.ADMIN -> Color(0xFF8B5CF6)
                        Role.ENGINEER -> Color(0xFF0878FF)
                        Role.SAFETY_OFFICER -> Color(0xFF10B981)
                        Role.WORKER -> Color(0xFFF58220)
                    }

                    Column {
                        Text(
                            text = userProfile.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = textColor
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(roleColor.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = userProfile.roleTitle,
                                    color = roleColor,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = userProfile.organization,
                            fontSize = 11.sp,
                            color = subTextColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Section: Role Switcher (Preview)
            Text(
                text = "ROLE SWITCHER (DEV PREVIEW)",
                fontSize = 10.5.sp,
                fontWeight = FontWeight.ExtraBold,
                color = subTextColor,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Role.entries.forEach { role ->
                    val isSelected = activeRole == role
                    val roleLabel = when (role) {
                        Role.ADMIN -> "Admin"
                        Role.ENGINEER -> "Engr"
                        Role.SAFETY_OFFICER -> "EHS"
                        Role.WORKER -> "Worker"
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) Color(0xFF0878FF)
                                else cardBg
                            )
                            .border(
                                1.dp,
                                if (isSelected) Color(0xFF0878FF) else borderColor,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { onRoleSelected(role) }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = roleLabel,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else textColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section: Navigation Items
            Text(
                text = "MODULES & TOOLS",
                fontSize = 10.5.sp,
                fontWeight = FontWeight.ExtraBold,
                color = subTextColor,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                DrawerNavigationLink(
                    title = "Home Dashboard",
                    subtitle = "Main KPI & daily activity",
                    icon = SafeOpsIcons.Home,
                    isDark = isDark,
                    onClick = { onNavigate("home") }
                )
                DrawerNavigationLink(
                    title = "Permits to Work (PTW)",
                    subtitle = "8 Springfield permit modules",
                    icon = SafeOpsIcons.Document,
                    isDark = isDark,
                    onClick = { onNavigate("permits") }
                )
                DrawerNavigationLink(
                    title = "Projects & Sites",
                    subtitle = "Floor plans & site zones",
                    icon = SafeOpsIcons.Projects,
                    isDark = isDark,
                    onClick = { onNavigate("projects") }
                )
                DrawerNavigationLink(
                    title = "Hazard Reporting",
                    subtitle = "Site risk observations",
                    icon = SafeOpsIcons.Hazards,
                    isDark = isDark,
                    onClick = { onNavigate("hazards") }
                )
                DrawerNavigationLink(
                    title = "Safety Audits & Inspections",
                    subtitle = "Checklists & compliance scoring",
                    icon = SafeOpsIcons.Inspections,
                    isDark = isDark,
                    onClick = { onNavigate("inspections") }
                )
                DrawerNavigationLink(
                    title = "Notifications & Broadcasts",
                    subtitle = "Urgent alerts & bulletins",
                    icon = SafeOpsIcons.Notifications,
                    isDark = isDark,
                    onClick = { onNavigate("notifications") }
                )
            }

            // Bottom Sign Out & Version Info
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(cardBg)
                    .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                    .clickable { onLogoutClick() }
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = SafeOpsIcons.Logout,
                            contentDescription = "Sign Out",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Sign Out",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFFEF4444)
                        )
                    }
                    Text(
                        text = "v2.4.0",
                        fontSize = 11.sp,
                        color = subTextColor
                    )
                }
            }
        }
    }
}

@Composable
private fun DrawerNavigationLink(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isDark: Boolean,
    onClick: () -> Unit
) {
    val textColor = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D)
    val subTextColor = if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF0878FF).copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color(0xFF0878FF),
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )
            Text(
                text = subtitle,
                fontSize = 10.5.sp,
                color = subTextColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Icon(
            imageVector = SafeOpsIcons.ArrowForward,
            contentDescription = null,
            tint = subTextColor.copy(alpha = 0.5f),
            modifier = Modifier.size(16.dp)
        )
    }
}