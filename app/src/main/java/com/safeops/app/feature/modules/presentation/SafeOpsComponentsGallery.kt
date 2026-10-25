package com.safeops.app.feature.modules.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.safeops.app.core.designsystem.component.SafeOpsAlertBanner
import com.safeops.app.core.designsystem.component.SafeOpsAlertType
import com.safeops.app.core.designsystem.component.SafeOpsAvatar
import com.safeops.app.core.designsystem.component.SafeOpsBarChart
import com.safeops.app.core.designsystem.component.SafeOpsBarData
import com.safeops.app.core.designsystem.component.SafeOpsButtonSize
import com.safeops.app.core.designsystem.component.SafeOpsChartSlice
import com.safeops.app.core.designsystem.component.SafeOpsCheckboxRow
import com.safeops.app.core.designsystem.component.SafeOpsDataTable
import com.safeops.app.core.designsystem.component.SafeOpsDatePickerField
import com.safeops.app.core.designsystem.component.SafeOpsDestructiveButton
import com.safeops.app.core.designsystem.component.SafeOpsDonutChart
import com.safeops.app.core.designsystem.component.SafeOpsDropdown
import com.safeops.app.core.designsystem.component.SafeOpsFormSection
import com.safeops.app.core.designsystem.component.SafeOpsGhostButton
import com.safeops.app.core.designsystem.component.SafeOpsIconBadge
import com.safeops.app.core.designsystem.component.SafeOpsKpiCard
import com.safeops.app.core.designsystem.component.SafeOpsNumberStepper
import com.safeops.app.core.designsystem.component.SafeOpsOutlinedButton
import com.safeops.app.core.designsystem.component.SafeOpsPasswordField
import com.safeops.app.core.designsystem.component.SafeOpsPrimaryButton
import com.safeops.app.core.designsystem.component.SafeOpsSecondaryButton
import com.safeops.app.core.designsystem.component.SafeOpsSegmentedControl
import com.safeops.app.core.designsystem.component.SafeOpsStatusDot
import com.safeops.app.core.designsystem.component.SafeOpsStatusPill
import com.safeops.app.core.designsystem.component.SafeOpsTableColumn
import com.safeops.app.core.designsystem.component.SafeOpsTextArea
import com.safeops.app.core.designsystem.component.SafeOpsTextField
import com.safeops.app.core.designsystem.component.SafeOpsToast
import com.safeops.app.core.designsystem.component.SafeOpsToggleRow
import com.safeops.app.core.designsystem.icon.SafeOpsIcons
import com.safeops.app.core.designsystem.theme.SafeOpsTheme
import com.safeops.app.domain.model.AuthenticatedSession

/**
 * Mock Model for Demo Data Table
 */
private data class DemoPermitItem(
    val code: String,
    val title: String,
    val location: String,
    val status: String,
    val statusColor: Color
)

/**
 * Universal Global Common UI Component Showcase Gallery.
 * Exhibits all centralized components operating under SafeOps Light & Pure Glass Black Dark Themes.
 */
@Composable
fun SafeOpsComponentsGallery(
    session: AuthenticatedSession,
    modifier: Modifier = Modifier
) {
    val isDark = SafeOpsTheme.colors.isDark
    val scrollState = rememberScrollState()

    // Interactive Demo States
    var textInputVal by remember { mutableStateOf("") }
    var passwordVal by remember { mutableStateOf("") }
    var textAreaVal by remember { mutableStateOf("Pre-shift tool inspection completed. Scaffolding anchor bolts verified.") }
    var stepperVal by remember { mutableStateOf(4) }
    var selectedDropdownOption by remember { mutableStateOf("Hot Work Permit (SPR-03)") }
    var toggleState by remember { mutableStateOf(true) }
    var checkboxState by remember { mutableStateOf(true) }
    var selectedSegmentIndex by remember { mutableStateOf(1) }
    var showToast by remember { mutableStateOf(false) }
    var tableSearchQuery by remember { mutableStateOf("") }
    var tableFilter by remember { mutableStateOf<String?>(null) }
    var sortKey by remember { mutableStateOf("code") }
    var sortAsc by remember { mutableStateOf(true) }

    val demoRecords = remember {
        listOf(
            DemoPermitItem("SPR-01", "General Work", "Block A • L3", "Approved", Color(0xFF16A34A)),
            DemoPermitItem("SPR-03", "Hot Work", "Fabrication Yard", "In Review", Color(0xFFF59E0B)),
            DemoPermitItem("SPR-04", "Lifting Work", "North Crane Zone", "Draft", Color(0xFF0878FF)),
            DemoPermitItem("SPR-05", "Height Work", "Exterior Scaffolding", "Approved", Color(0xFF16A34A)),
            DemoPermitItem("SPR-08", "Night Work", "Tunnel Section 4", "Revoked", Color(0xFFEF4444)),
            DemoPermitItem("SPR-02", "Excavation", "Basement Trench", "In Review", Color(0xFFF59E0B))
        )
    }

    val filteredDemoRecords = demoRecords.filter { item ->
        val matchesFilter = tableFilter == null || item.status.equals(tableFilter, ignoreCase = true)
        val matchesSearch = tableSearchQuery.isBlank() ||
                item.code.contains(tableSearchQuery, ignoreCase = true) ||
                item.title.contains(tableSearchQuery, ignoreCase = true) ||
                item.location.contains(tableSearchQuery, ignoreCase = true)
        matchesFilter && matchesSearch
    }.sortedWith { a, b ->
        val comparison = when (sortKey) {
            "code" -> a.code.compareTo(b.code)
            "title" -> a.title.compareTo(b.title)
            "status" -> a.status.compareTo(b.status)
            else -> a.code.compareTo(b.code)
        }
        if (sortAsc) comparison else -comparison
    }

    val donutSlices = listOf(
        SafeOpsChartSlice("Approved", 42f, Color(0xFF16A34A)),
        SafeOpsChartSlice("Pending", 18f, Color(0xFFF59E0B)),
        SafeOpsChartSlice("Draft", 12f, Color(0xFF0878FF)),
        SafeOpsChartSlice("Revoked", 4f, Color(0xFFEF4444))
    )

    val barMetrics = listOf(
        SafeOpsBarData("Mon", 14f, Color(0xFF0878FF)),
        SafeOpsBarData("Tue", 22f, Color(0xFF0878FF)),
        SafeOpsBarData("Wed", 28f, Color(0xFF38BDF8)),
        SafeOpsBarData("Thu", 19f, Color(0xFF0878FF)),
        SafeOpsBarData("Fri", 34f, Color(0xFF10B981)),
        SafeOpsBarData("Sat", 11f, Color(0xFF0878FF))
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (isDark) Color(0xFF000000) else Color(0xFFF5F4FA))
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header
            Column {
                Text(
                    text = "SafeOps Design System",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Global common UI component library with responsive props and theme consistency.",
                    fontSize = 13.sp,
                    color = if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E)
                )
            }

            // 1. Buttons Gallery
            SafeOpsFormSection(
                title = "Buttons & Action Controls",
                subtitle = "Primary, Secondary, Destructive, Ghost, and FABs with loading and sizes",
                badgeText = "5 Variants",
                isCollapsible = true
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Primary & Secondary Action Buttons", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0878FF))
                    SafeOpsPrimaryButton(
                        text = "Primary Gradient Action",
                        leadingIcon = SafeOpsIcons.Check,
                        onClick = { showToast = true }
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        SafeOpsSecondaryButton(
                            text = "Secondary Action",
                            leadingIcon = SafeOpsIcons.Document,
                            onClick = {},
                            modifier = Modifier.weight(1f)
                        )
                        SafeOpsDestructiveButton(
                            text = "Emergency Revoke",
                            leadingIcon = SafeOpsIcons.Warning,
                            onClick = {},
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Button Sizes & Loading States", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0878FF))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SafeOpsPrimaryButton(
                            text = "Small",
                            size = SafeOpsButtonSize.SMALL,
                            onClick = {},
                            modifier = Modifier.weight(1f)
                        )
                        SafeOpsPrimaryButton(
                            text = "Loading",
                            size = SafeOpsButtonSize.SMALL,
                            isLoading = true,
                            onClick = {},
                            modifier = Modifier.weight(1f)
                        )
                        SafeOpsGhostButton(
                            text = "Ghost Action",
                            leadingIcon = SafeOpsIcons.ArrowForward,
                            onClick = {},
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // 2. Forms & Inputs
            SafeOpsFormSection(
                title = "Forms & Input Fields",
                subtitle = "Text fields, password toggle, textarea, number stepper, and switches",
                badgeText = "Interactive",
                isCollapsible = true
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    SafeOpsTextField(
                        value = textInputVal,
                        onValueChange = { textInputVal = it },
                        label = "Project Location / Site Zone",
                        placeholder = "e.g. Building A - Level 3",
                        leadingIcon = SafeOpsIcons.Location
                    )

                    SafeOpsPasswordField(
                        value = passwordVal,
                        onValueChange = { passwordVal = it },
                        label = "Security Authorization PIN"
                    )

                    SafeOpsTextArea(
                        value = textAreaVal,
                        onValueChange = { textAreaVal = it },
                        label = "Hazard Description / Safety Controls",
                        minLines = 3,
                        maxCharacters = 250
                    )

                    SafeOpsNumberStepper(
                        value = stepperVal,
                        onValueChange = { stepperVal = it },
                        label = "Authorized Workers in Hot Zone",
                        minValue = 1,
                        maxValue = 20,
                        unit = "Workers"
                    )

                    SafeOpsDatePickerField(
                        value = "28 Sep 2026",
                        onDateClick = {},
                        label = "Permit Validity Date"
                    )

                    SafeOpsToggleRow(
                        title = "Atmospheric Gas Testing Required",
                        subtitle = "Continuous O2, H2S, LEL monitoring mandatory",
                        checked = toggleState,
                        onCheckedChange = { toggleState = it }
                    )

                    SafeOpsCheckboxRow(
                        title = "Fire Watch Personnel Assigned",
                        subtitle = "Equipped with 9kg ABC fire extinguisher",
                        checked = checkboxState,
                        onCheckedChange = { checkboxState = it }
                    )
                }
            }

            // 3. Dropdown with 180° Chevron Rotation
            SafeOpsFormSection(
                title = "Dropdown & Select Menu",
                subtitle = "Attached header and container with 180° rotating chevron & search",
                badgeText = "180° Motion",
                isCollapsible = true
            ) {
                SafeOpsDropdown(
                    options = listOf(
                        "General Work Permit (SPR-01)",
                        "Excavation Work Permit (SPR-02)",
                        "Hot Work Permit (SPR-03)",
                        "Lifting Operations Permit (SPR-04)",
                        "Work at Height Scaffolding (SPR-05)",
                        "Piling & Foundation Permit (SPR-09)",
                        "Night Shift Operations (SPR-08)",
                        "Demolition Permit (SPR-11)"
                    ),
                    selectedOption = selectedDropdownOption,
                    onOptionSelected = { selectedDropdownOption = it },
                    label = "Select Official Springfield PTW Template",
                    leadingIcon = SafeOpsIcons.Document,
                    isSearchable = true
                )
            }

            // 4. Notification Alerts & Toast
            SafeOpsFormSection(
                title = "Notification Banners & Alerts",
                subtitle = "Four standard feedback states: Success, Error, Warning, and Info",
                badgeText = "4 States",
                isCollapsible = true
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SafeOpsAlertBanner(
                        type = SafeOpsAlertType.SUCCESS,
                        title = "Permit Approved & Issued",
                        message = "EHS Safety Officer has digitally signed SPR-2026-819.",
                        actionLabel = "View Permit Details",
                        onActionClick = {},
                        onDismiss = {}
                    )

                    SafeOpsAlertBanner(
                        type = SafeOpsAlertType.WARNING,
                        title = "Gas Detector Calibration Due",
                        message = "Device SN-4919 calibration expires in 2 hours.",
                        onDismiss = {}
                    )

                    SafeOpsAlertBanner(
                        type = SafeOpsAlertType.FAILURE,
                        title = "High Risk Level Detected",
                        message = "Wind speed exceeded 35 km/h. Scaffolding operations must halt immediately.",
                        actionLabel = "Initiate Safety Protocol",
                        onActionClick = {}
                    )

                    SafeOpsAlertBanner(
                        type = SafeOpsAlertType.INFO,
                        title = "Daily Safety Toolbox Talk",
                        message = "Morning briefing scheduled for 08:30 AM at Zone B muster point."
                    )
                }
            }

            // 5. Icons, Badges & Segmented Control
            SafeOpsFormSection(
                title = "Icons, Badges & Segmented Controls",
                subtitle = "Notification counters, status dots, avatar rings, and segmented pills",
                isCollapsible = true
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Segmented Tab Switcher (WhatsApp / iOS style)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0878FF))
                    SafeOpsSegmentedControl(
                        options = listOf("Today", "7 Days", "30 Days", "Quarter"),
                        selectedIndex = selectedSegmentIndex,
                        onOptionSelected = { selectedSegmentIndex = it }
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Icon Badges & Avatars", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0878FF))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        SafeOpsIconBadge(
                            icon = SafeOpsIcons.Notifications,
                            contentDescription = "Alerts",
                            badgeCount = 3,
                            onClick = {}
                        )

                        SafeOpsAvatar(
                            initials = "VM",
                            onClick = {}
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            SafeOpsStatusDot(color = Color(0xFF16A34A), pulsing = true)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Live Sensor", fontSize = 12.sp, color = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D))
                        }

                        SafeOpsStatusPill(text = "Active PTW", color = Color(0xFF16A34A))
                    }
                }
            }

            // 6. Data Table Component
            SafeOpsFormSection(
                title = "Data Table with Search, Sort & Filter",
                subtitle = "Instant text query, asc/desc column sorting, and status filter chips",
                badgeText = "Complete",
                isCollapsible = true
            ) {
                val tableColumns = listOf(
                    SafeOpsTableColumn<DemoPermitItem>(
                        title = "CODE",
                        key = "code",
                        weight = 1.2f,
                        isSortable = true
                    ) { item ->
                        Text(item.code, fontWeight = FontWeight.Bold, color = Color(0xFF0878FF), fontSize = 12.sp)
                    },
                    SafeOpsTableColumn<DemoPermitItem>(
                        title = "PERMIT TYPE",
                        key = "title",
                        weight = 1.8f,
                        isSortable = true
                    ) { item ->
                        Column {
                            Text(item.title, fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp, color = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D))
                            Text(item.location, fontSize = 10.5.sp, color = if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E))
                        }
                    },
                    SafeOpsTableColumn<DemoPermitItem>(
                        title = "STATUS",
                        key = "status",
                        weight = 1.3f,
                        isSortable = true
                    ) { item ->
                        SafeOpsStatusPill(text = item.status, color = item.statusColor)
                    }
                )

                SafeOpsDataTable(
                    columns = tableColumns,
                    items = filteredDemoRecords,
                    onItemClick = {},
                    searchQuery = tableSearchQuery,
                    onSearchQueryChange = { tableSearchQuery = it },
                    searchPlaceholder = "Search code, type, location...",
                    filters = listOf("Approved", "In Review", "Draft", "Revoked"),
                    selectedFilter = tableFilter,
                    onFilterSelected = { tableFilter = it },
                    sortColumnKey = sortKey,
                    isAscending = sortAsc,
                    onSortClick = { key ->
                        if (sortKey == key) {
                            sortAsc = !sortAsc
                        } else {
                            sortKey = key
                            sortAsc = true
                        }
                    }
                )
            }

            // 7. Insights (Donut Chart, Bar Chart & KPI Stats)
            SafeOpsFormSection(
                title = "Insights & Analytics Charts",
                subtitle = "Interactive Canvas Donut Chart, Bar Chart, and KPI metric stat cards",
                badgeText = "Canvas",
                isCollapsible = true
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // KPI Strip
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SafeOpsKpiCard(
                            title = "Active Permits",
                            value = "76",
                            icon = SafeOpsIcons.Document,
                            trendPercent = "8.4%",
                            isPositiveTrend = true,
                            containerColor = Color(0xFF0878FF),
                            modifier = Modifier.weight(1f)
                        )
                        SafeOpsKpiCard(
                            title = "Near Misses",
                            value = "2",
                            icon = SafeOpsIcons.Hazards,
                            trendPercent = "50%",
                            isPositiveTrend = false,
                            containerColor = Color(0xFFEF4444),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Donut Chart
                    SafeOpsDonutChart(
                        slices = donutSlices,
                        title = "Permit Approval Distribution",
                        subtitle = "76 total operational permits logged this month"
                    )

                    // Bar Chart
                    SafeOpsBarChart(
                        bars = barMetrics,
                        title = "Weekly Compliance Audits",
                        subtitle = "Scored inspections completed per day",
                        targetLineValue = 25f,
                        targetLineLabel = "Daily Target"
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }

        // Floating Toast
        SafeOpsToast(
            message = "Action executed successfully in SafeOps Design System!",
            type = SafeOpsAlertType.SUCCESS,
            visible = showToast,
            onDismiss = { showToast = false },
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}
