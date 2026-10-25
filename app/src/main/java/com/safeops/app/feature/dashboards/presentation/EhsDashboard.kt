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
fun EhsDashboardContent(
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
            metrics = DashboardMockData.ehsKpis,
            isDark = isDark,
            onKpiClick = { onNavigateAction("permits") },
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 2. Quick Actions
        DashboardQuickActions(
            actions = DashboardMockData.ehsQuickActions,
            isDark = isDark,
            onActionClick = { onNavigateAction(it.targetRoute) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 3. Permit Approval Queue Section
        EhsPermitApprovalQueueCard(
            queue = DashboardMockData.ehsReviewQueue,
            isDark = isDark,
            onReviewClick = { onNavigateAction("review_permits") },
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 4. Upcoming Inspections Timeline
        EhsUpcomingInspectionsCard(
            inspections = DashboardMockData.ehsInspections,
            isDark = isDark,
            onViewAll = { onNavigateAction("inspections") },
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 5. Hazard & Incident Summary Donut Chart
        DashboardDonutChartCard(
            title = "Hazard & Incident Summary",
            centerCount = "18",
            centerLabel = "Total Reports",
            items = listOf(
                "High" to (4 to Color(0xFFE74C3C)),
                "Medium" to (6 to Color(0xFFE67E22)),
                "Low" to (5 to Color(0xFFF1C40F)),
                "Closed" to (3 to Color(0xFF0878FF))
            ),
            isDark = isDark,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
    }
}

// ── EHS Permit Approval Queue Card ───────────────────────────────────────
@Composable
private fun EhsPermitApprovalQueueCard(
    queue: List<PermitMock>,
    isDark: Boolean,
    onReviewClick: (PermitMock) -> Unit,
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
                    text = "Permit Approval Queue",
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
                queue.forEach { permit ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC))
                            .border(
                                1.dp,
                                if (isDark) Color(0xFF333338) else Color(0xFFEDF2F7),
                                RoundedCornerShape(14.dp)
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple()
                            ) { onReviewClick(permit) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE67E22).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = SafeOpsIcons.Document,
                                contentDescription = null,
                                tint = Color(0xFFE67E22),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = permit.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color.White else Color(0xFF0F172A)
                                )
                            }
                            Text(
                                text = "${permit.permitNumber} • ${permit.location}",
                                fontSize = 11.sp,
                                color = if (isDark) Color(0xFF8899B0) else Color(0xFF64748B)
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = permit.timeAgo,
                                fontSize = 10.sp,
                                color = if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFFFF4E5))
                                    .border(1.dp, Color(0xFFFFD8A8), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 10.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "Review ›",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFD97706)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── EHS Upcoming Inspections Component ───────────────────────────────────
@Composable
private fun EhsUpcomingInspectionsCard(
    inspections: List<ScheduleItemMock>,
    isDark: Boolean,
    onViewAll: () -> Unit,
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
                    text = "Upcoming Inspections",
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
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { onViewAll() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column {
                inspections.forEachIndexed { index, item ->
                    DashboardTimelineItem(
                        item = item,
                        isDark = isDark,
                        isLast = index == inspections.size - 1
                    )
                }
            }
        }
    }
}
