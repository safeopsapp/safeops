package com.safeops.app.feature.dashboards.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.safeops.app.core.designsystem.icon.SafeOpsIcons
import com.safeops.app.core.designsystem.theme.SafeOpsTheme

@Composable
fun AdminDashboardContent(
    isDark: Boolean,
    onNavigateAction: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 90.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // 1. KPI Strip (4 equal columns)
        DashboardKpiStrip(
            metrics = DashboardMockData.adminKpis,
            isDark = isDark,
            onKpiClick = { onNavigateAction("permits") },
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 2. Quick Actions Row
        DashboardQuickActions(
            actions = DashboardMockData.adminQuickActions,
            isDark = isDark,
            onActionClick = { onNavigateAction(it.targetRoute) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 3. Permit Overview (Donut Chart) + Permit by Type List
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Permit Overview Donut Chart Card
            DashboardDonutChartCard(
                title = "Permit Overview",
                centerCount = "24",
                centerLabel = "Total Permits",
                items = listOf(
                    "Active" to (12 to Color(0xFF27AE60)),
                    "Under Review" to (6 to Color(0xFF0878FF)),
                    "Completed" to (4 to Color(0xFF9B59B6)),
                    "Expired" to (2 to Color(0xFFE74C3C))
                ),
                isDark = isDark
            )

            // Permit by Type List Card
            AdminPermitByTypeCard(
                items = DashboardMockData.permitTypesCount,
                isDark = isDark
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 4. Pending Actions & Recent Activity Sections
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Pending Actions
            AdminPendingActionsCard(
                actions = DashboardMockData.adminPendingActions,
                isDark = isDark,
                onActionClick = { onNavigateAction("review_permits") }
            )

            // Recent Activity Card
            AdminRecentActivityCard(
                activities = DashboardMockData.recentActivities,
                isDark = isDark
            )
        }
    }
}

// ── Admin Permit by Type Component ───────────────────────────────────────
@Composable
private fun AdminPermitByTypeCard(
    items: List<PermitTypeCountMock>,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val bg = if (isDark) Color(0xFF18181B) else Color.White
    val borderCol = if (isDark) Color(0xFF333338) else Color(0xFFE2EAF8)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(20.dp))
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
                    text = "Permit by Type",
                    style = SafeOpsTheme.typography.cardTitle.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else Color(0xFF0F172A)
                    )
                )
                Text(
                    text = "This Month ▾",
                    fontSize = 12.sp,
                    color = Color(0xFF0878FF),
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.name,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isDark) Color(0xFFCAD6E8) else Color(0xFF334155),
                            modifier = Modifier.width(115.dp)
                        )

                        Box(modifier = Modifier.weight(1f)) {
                            LinearProgressIndicator(
                                progress = { item.totalRatio },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(7.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = item.barColor,
                                trackColor = if (isDark) Color(0xFF333338) else Color(0xFFE2EAF8),
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = item.count.toString(),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else Color(0xFF0F172A)
                        )
                    }
                }
            }
        }
    }
}

// ── Admin Pending Actions Component ──────────────────────────────────────
@Composable
private fun AdminPendingActionsCard(
    actions: List<PendingActionMock>,
    isDark: Boolean,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (isDark) Color(0xFF18181B) else Color.White
    val borderCol = if (isDark) Color(0xFF333338) else Color(0xFFE2EAF8)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(20.dp))
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
                    text = "Pending Actions",
                    style = SafeOpsTheme.typography.cardTitle.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else Color(0xFF0F172A)
                    )
                )
                Text(
                    text = "View All ›",
                    fontSize = 12.sp,
                    color = Color(0xFF0878FF),
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                actions.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple()
                            ) { onActionClick() }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(item.actionColor.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = SafeOpsIcons.Document,
                                    contentDescription = null,
                                    tint = item.actionColor,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Text(
                                    text = item.title,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color.White else Color(0xFF0F172A)
                                )
                                Text(
                                    text = item.permitRef,
                                    fontSize = 11.sp,
                                    color = if (isDark) Color(0xFF8899B0) else Color(0xFF64748B)
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(item.actionColor.copy(alpha = 0.12f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = item.actionText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = item.actionColor
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Admin Recent Activity Component ──────────────────────────────────────
@Composable
private fun AdminRecentActivityCard(
    activities: List<ActivityItemMock>,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val bg = if (isDark) Color(0xFF18181B) else Color.White
    val borderCol = if (isDark) Color(0xFF333338) else Color(0xFFE2EAF8)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(20.dp))
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
                    text = "Recent Activity",
                    style = SafeOpsTheme.typography.cardTitle.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else Color(0xFF0F172A)
                    )
                )
                Text(
                    text = "View All ›",
                    fontSize = 12.sp,
                    color = Color(0xFF0878FF),
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                activities.forEach { activity ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(activity.iconColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = activity.icon,
                                contentDescription = null,
                                tint = activity.iconColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = activity.title,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else Color(0xFF0F172A)
                            )
                            Text(
                                text = activity.subtitle,
                                fontSize = 11.sp,
                                color = if (isDark) Color(0xFF8899B0) else Color(0xFF64748B)
                            )
                        }

                        Text(
                            text = activity.timeAgo,
                            fontSize = 10.5.sp,
                            color = if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8)
                        )
                    }
                }
            }
        }
    }
}
