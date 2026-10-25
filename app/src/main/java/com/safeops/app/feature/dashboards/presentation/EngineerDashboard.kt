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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.safeops.app.core.designsystem.theme.SafeOpsTheme

@Composable
fun EngineerDashboardContent(
    isDark: Boolean,
    onNavigateAction: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var selectedFilterChip by remember { mutableStateOf("All (8)") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 90.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // 1. KPI Strip (4 equal columns)
        DashboardKpiStrip(
            metrics = DashboardMockData.engineerKpis,
            isDark = isDark,
            onKpiClick = { onNavigateAction("permits") },
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 2. Quick Actions
        DashboardQuickActions(
            actions = DashboardMockData.engineerQuickActions,
            isDark = isDark,
            onActionClick = { onNavigateAction(it.targetRoute) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 3. My Permits Section with Horizontal Filter Chips
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Permits",
                    style = SafeOpsTheme.typography.sectionTitle.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else Color(0xFF0F172A)
                    )
                )
                Text(
                    text = "View All ›",
                    style = SafeOpsTheme.typography.caption.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0878FF)
                    ),
                    modifier = Modifier.clickable { onNavigateAction("permits") }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Filter Chips
            val chips = listOf("All (8)", "Draft (1)", "Under Review (2)", "Approved (4)", "Active (3)", "Expired (1)")
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp)
            ) {
                items(chips) { chipText ->
                    val isSelected = selectedFilterChip == chipText
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (isSelected) Color(0xFF0878FF)
                                else if (isDark) Color(0xFF1E293B)
                                else Color(0xFFF1F5F9)
                            )
                            .border(
                                1.dp,
                                if (isSelected) Color(0xFF0878FF)
                                else if (isDark) Color(0xFF333338)
                                else Color(0xFFE2EAF8),
                                RoundedCornerShape(20.dp)
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple()
                            ) { selectedFilterChip = chipText }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = chipText,
                            fontSize = 11.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else if (isDark) Color(0xFFCAD6E8) else Color(0xFF475569)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Permit List Cards
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DashboardMockData.engineerPermits.forEach { permit ->
                    DashboardPermitCard(
                        permit = permit,
                        isDark = isDark,
                        onClick = { onNavigateAction("permits") }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 4. Today's Schedule & Recent Activity Cards
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Today's Schedule Timeline Card
            EngineerScheduleCard(
                schedule = DashboardMockData.engineerSchedule,
                isDark = isDark,
                onViewAll = { }
            )
        }
    }
}

// ── Engineer Schedule Timeline Card ──────────────────────────────────────
@Composable
private fun EngineerScheduleCard(
    schedule: List<ScheduleItemMock>,
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
                    text = "Today's Schedule",
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
                schedule.forEachIndexed { index, item ->
                    DashboardTimelineItem(
                        item = item,
                        isDark = isDark,
                        isLast = index == schedule.size - 1
                    )
                }
            }
        }
    }
}
