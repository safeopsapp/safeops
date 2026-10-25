package com.safeops.app.feature.inspections.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.safeops.app.core.designsystem.component.SafeOpsAlertBanner
import com.safeops.app.core.designsystem.component.SafeOpsAlertType
import com.safeops.app.core.designsystem.component.SafeOpsCurvyHeroPattern
import com.safeops.app.core.designsystem.component.SafeOpsDataTable
import com.safeops.app.core.designsystem.component.SafeOpsDatePickerField
import com.safeops.app.core.designsystem.component.SafeOpsDropdown
import com.safeops.app.core.designsystem.component.SafeOpsFormSection
import com.safeops.app.core.designsystem.component.SafeOpsNumberStepper
import com.safeops.app.core.designsystem.component.SafeOpsOutlinedButton
import com.safeops.app.core.designsystem.component.SafeOpsPrimaryButton
import com.safeops.app.core.designsystem.component.SafeOpsSegmentedControl
import com.safeops.app.core.designsystem.component.SafeOpsStatusPill
import com.safeops.app.core.designsystem.component.SafeOpsTableColumn
import com.safeops.app.core.designsystem.component.SafeOpsTextArea
import com.safeops.app.core.designsystem.component.SafeOpsTextField
import com.safeops.app.core.designsystem.component.SafeOpsToggleRow
import com.safeops.app.core.designsystem.icon.SafeOpsIcons
import com.safeops.app.core.designsystem.theme.AccentOrange
import com.safeops.app.core.designsystem.theme.SafeOpsTheme
import com.safeops.app.domain.model.AuthenticatedSession
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class InspectionRecord(
    val id: String,
    val title: String,
    val type: String,
    val score: String,
    val inspector: String,
    val status: String,
    val date: String
)

data class ChecklistItem(
    val title: String,
    var status: String = "PASS" // PASS, FAIL, NA
)

@Composable
fun CreateInspectionScreen(
    session: AuthenticatedSession,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isDark = SafeOpsTheme.colors.isDark
    val typography = SafeOpsTheme.typography
    val coroutineScope = rememberCoroutineScope()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("New Inspection", "Inspection Register")

    // Form Fields
    var inspectionCategory by remember { mutableStateOf("Daily Site Safety Walkthrough") }
    var workFront by remember { mutableStateOf("Sairam Metro Station 4 - Track Deck") }
    var leadInspector by remember { mutableStateOf(session.user.fullName) }
    var inspectionDate by remember { mutableStateOf("03 Oct 2026") }
    var shiftName by remember { mutableStateOf("Day Shift (08:00 - 17:00)") }

    // Interactive Checklist
    val checklistItems = remember {
        mutableStateListOf(
            ChecklistItem("Mandatory PPE worn by all site personnel (Hard hat, safety boots, hi-vis, eyewear)", "PASS"),
            ChecklistItem("Edge protection, guardrails & toe-boards installed on all openings > 2m", "PASS"),
            ChecklistItem("Scaffolding possesses valid GREEN SafeOps inspection tag and grounded base plates", "PASS"),
            ChecklistItem("Fire extinguishers inspected within 30 days, fully charged, and unobstructed", "PASS"),
            ChecklistItem("Temporary electrical DB boxes locked, weatherproofed, with functional 30mA RCD test", "FAIL"),
            ChecklistItem("Valid Permit to Work (PTW) displayed visibly on work site and approved by EHS", "PASS"),
            ChecklistItem("Housekeeping orderly, walkways free of exposed rebar, nails, and trip hazards", "PASS")
        )
    }

    // Findings
    var complianceScore by remember { mutableIntStateOf(86) }
    var deficienciesNotes by remember { mutableStateOf("Temporary DB box #4 on Level 2 had a broken weather cover latch. Power isolated until repaired.") }
    var correctiveAction by remember { mutableStateOf("Site Electrician (K. Rajan) assigned to replace DB cover latch before 18:00 today.") }
    var rectificationDueDate by remember { mutableStateOf("04 Oct 2026") }
    var resolvedOnSpot by remember { mutableStateOf(false) }
    var requiresFollowUp by remember { mutableStateOf(true) }

    // Submission states
    var isSubmitting by remember { mutableStateOf(false) }
    var showSuccessAlert by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }

    // Mock Inspection Register Data
    val inspectionList = remember {
        mutableStateListOf(
            InspectionRecord("INS-2026-442", "Daily Site Safety Walkthrough", "Safety Walk", "86%", session.user.fullName, "Pending Rectification", "Today"),
            InspectionRecord("INS-2026-441", "Scaffolding & Work at Height Audit", "Scaffolding", "94%", "S. Shankar (EHS)", "Compliant", "Yesterday"),
            InspectionRecord("INS-2026-440", "Tower Crane Pre-Operation Inspection", "Heavy Plant", "100%", "Vasanthan M", "Compliant", "01 Oct"),
            InspectionRecord("INS-2026-439", "Basement Dewatering & Trenching", "Excavation", "91%", "S. Shankar (EHS)", "Compliant", "30 Sep")
        )
    }

    val headerGradient = if (isDark) {
        Brush.verticalGradient(
            0.00f to Color(0xFF1E88E5), // Luminous light-blue accent at top
            0.16f to Color(0xFF0D47A1), // Rich royal blue
            0.46f to Color(0xFF08265E), // Deep cobalt navy
            0.78f to Color(0xFF031336), // Dark midnight blue
            1.00f to Color(0xFF010A1A)  // Darkest pure sapphire base
        )
    } else {
        Brush.verticalGradient(
            0.00f to Color(0xFF38BDF8), // Radiant light-blue accent at top
            0.14f to Color(0xFF1E88E5), // Vibrant azure
            0.38f to Color(0xFF0D47A1), // Deep royal blue
            0.70f to Color(0xFF062B66), // Dark navy
            1.00f to Color(0xFF020D22)  // Dark midnight blue base
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(if (isDark) Color(0xFF000000) else Color(0xFFF5F4FA))
    ) {
        // ── 1. Clean & Curvy Fluid Gradient Header ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(headerGradient)
                .statusBarsPadding()
        ) {
            SafeOpsCurvyHeroPattern(
                isDark = isDark,
                modifier = Modifier.matchParentSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                // Navigation Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.12f))
                        ) {
                            Icon(
                                imageVector = SafeOpsIcons.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "SAFE",
                                    style = typography.titleMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp
                                    )
                                )
                                Text(
                                    text = "OPS",
                                    style = typography.titleMedium.copy(
                                        color = AccentOrange,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp
                                    )
                                )
                                Text(
                                    text = " • INSPECTIONS",
                                    style = typography.bodySmall.copy(
                                        color = Color(0xFF38BDF8),
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp
                                    )
                                )
                            }
                            Text(
                                text = "Safety Audits & Compliance Scoring",
                                style = typography.caption.copy(
                                    color = Color.White.copy(alpha = 0.75f),
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }

                    // Score pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF16A34A).copy(alpha = 0.22f))
                            .border(1.dp, Color(0xFF16A34A).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "$complianceScore% AUDIT",
                            style = typography.caption.copy(
                                color = Color(0xFF86EFAC),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                SafeOpsSegmentedControl(
                    items = tabs,
                    selectedIndex = selectedTabIndex,
                    onItemSelected = { selectedTabIndex = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // ── 2. Content Body ──
        if (selectedTabIndex == 0) {
            // New Inspection Form
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                // Success Banner
                AnimatedVisibility(
                    visible = showSuccessAlert,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Column {
                        SafeOpsAlertBanner(
                            type = SafeOpsAlertType.SUCCESS,
                            title = "Safety Inspection Logged",
                            message = successMessage,
                            onDismiss = { showSuccessAlert = false },
                            actionLabel = "View Register",
                            onActionClick = {
                                showSuccessAlert = false
                                selectedTabIndex = 1
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // Section 1: Audit Scope & Protocol
                SafeOpsFormSection(
                    title = "1. Inspection Protocol & Site Details",
                    subtitle = "Inspection category, location, and lead inspector",
                    icon = SafeOpsIcons.Checklist
                ) {
                    SafeOpsDropdown(
                        label = "Inspection Type / Protocol *",
                        selectedOption = inspectionCategory,
                        options = listOf(
                            "Daily Site Safety Walkthrough",
                            "Scaffolding & Work at Height Audit",
                            "Tower Crane & Rigging Inspection",
                            "Excavation & Shoring Safety Check",
                            "Fire Protection & Extinguishers Check",
                            "Electrical Installation & DB Inspection",
                            "Welfare & First Aid Facilities Audit"
                        ),
                        onOptionSelected = { inspectionCategory = it },
                        leadingIcon = SafeOpsIcons.Checklist
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    SafeOpsTextField(
                        value = workFront,
                        onValueChange = { workFront = it },
                        label = "Site Location & Work Front *",
                        placeholder = "e.g., Sairam Metro Station 4 - Track Deck",
                        leadingIcon = SafeOpsIcons.Projects
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    SafeOpsTextField(
                        value = leadInspector,
                        onValueChange = { leadInspector = it },
                        label = "Lead Inspector Name *",
                        placeholder = "e.g., S. Shankar (Senior EHS Officer)",
                        leadingIcon = SafeOpsIcons.SafetyOfficer
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        SafeOpsDatePickerField(
                            value = inspectionDate,
                            onValueChange = { inspectionDate = it },
                            label = "Inspection Date",
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        SafeOpsDropdown(
                            label = "Work Shift",
                            selectedOption = shiftName,
                            options = listOf(
                                "Day Shift (08:00 - 17:00)",
                                "Night Shift (20:00 - 05:00)",
                                "Weekend Maintenance Shift"
                            ),
                            onOptionSelected = { shiftName = it },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 2: Interactive Compliance Checklist
                SafeOpsFormSection(
                    title = "2. Field Compliance Checklist",
                    subtitle = "Evaluate site conditions (Pass / Fail / N/A)",
                    icon = SafeOpsIcons.Shield
                ) {
                    val surfaceColor = if (isDark) Color(0xFF18181B) else Color(0xFFFFFDFF)
                    val outlineColor = if (isDark) Color(0xFF333338) else Color(0xFFE4E2E9)

                    checklistItems.forEachIndexed { index, item ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(surfaceColor)
                                .border(1.dp, outlineColor, RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "${index + 1}. ${item.title}",
                                style = typography.bodySmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D)
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // PASS Button
                                val isPass = item.status == "PASS"
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isPass) Color(0xFF16A34A).copy(alpha = 0.20f) else Color.Transparent)
                                        .border(
                                            1.dp,
                                            if (isPass) Color(0xFF16A34A) else outlineColor,
                                            RoundedCornerShape(6.dp)
                                        )
                                        .clickable {
                                            checklistItems[index] = item.copy(status = "PASS")
                                        }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "PASS",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (isPass) Color(0xFF22C55E) else (if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E))
                                    )
                                }

                                // FAIL Button
                                val isFail = item.status == "FAIL"
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isFail) Color(0xFFEF4444).copy(alpha = 0.20f) else Color.Transparent)
                                        .border(
                                            1.dp,
                                            if (isFail) Color(0xFFEF4444) else outlineColor,
                                            RoundedCornerShape(6.dp)
                                        )
                                        .clickable {
                                            checklistItems[index] = item.copy(status = "FAIL")
                                        }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "FAIL",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (isFail) Color(0xFFEF4444) else (if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E))
                                    )
                                }

                                // N/A Button
                                val isNa = item.status == "NA"
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isNa) Color(0xFF3B82F6).copy(alpha = 0.20f) else Color.Transparent)
                                        .border(
                                            1.dp,
                                            if (isNa) Color(0xFF3B82F6) else outlineColor,
                                            RoundedCornerShape(6.dp)
                                        )
                                        .clickable {
                                            checklistItems[index] = item.copy(status = "NA")
                                        }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "N / A",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (isNa) Color(0xFF38BDF8) else (if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E))
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 3: Findings & Rectification Plan
                SafeOpsFormSection(
                    title = "3. Audit Findings & Scoring",
                    subtitle = "Score calculation, observations, and follow-up timeline",
                    icon = SafeOpsIcons.Document
                ) {
                    SafeOpsNumberStepper(
                        value = complianceScore,
                        onValueChange = { complianceScore = it },
                        label = "Overall Site Compliance Score",
                        min = 0,
                        max = 100,
                        step = 5,
                        unit = "%"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    SafeOpsTextArea(
                        value = deficienciesNotes,
                        onValueChange = { deficienciesNotes = it },
                        label = "Observations & Deficiencies Noted *",
                        placeholder = "Detail any non-compliant conditions or unsafe practices...",
                        minLines = 3,
                        maxChars = 500
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    SafeOpsTextArea(
                        value = correctiveAction,
                        onValueChange = { correctiveAction = it },
                        label = "Required Corrective Actions & Assigned Personnel *",
                        placeholder = "Specific rectification tasks, names of assigned supervisors...",
                        minLines = 3,
                        maxChars = 400
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    SafeOpsDatePickerField(
                        value = rectificationDueDate,
                        onValueChange = { rectificationDueDate = it },
                        label = "Target Rectification Deadline",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    SafeOpsToggleRow(
                        title = "Critical Deficiencies Resolved on the Spot",
                        subtitle = "Non-conformances corrected prior to inspector leaving site",
                        checked = resolvedOnSpot,
                        onCheckedChange = { resolvedOnSpot = it },
                        icon = SafeOpsIcons.Check
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    SafeOpsToggleRow(
                        title = "Requires Follow-up Re-Inspection within 48h",
                        subtitle = "Secondary verification required by EHS inspector",
                        checked = requiresFollowUp,
                        onCheckedChange = { requiresFollowUp = it },
                        icon = SafeOpsIcons.Calendar
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                SafeOpsPrimaryButton(
                    text = "Complete & Submit Inspection",
                    onClick = {
                        isSubmitting = true
                        coroutineScope.launch {
                            delay(1000)
                            isSubmitting = false
                            val newId = "INS-2026-${(443..500).random()}"
                            val status = if (complianceScore >= 90) "Compliant" else "Pending Rectification"
                            inspectionList.add(0, InspectionRecord(newId, inspectionCategory, "Audit", "$complianceScore%", leadInspector, status, "Just now"))
                            successMessage = "Inspection audit $newId logged with compliance score $complianceScore%."
                            showSuccessAlert = true
                        }
                    },
                    isLoading = isSubmitting,
                    icon = SafeOpsIcons.Check,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                SafeOpsOutlinedButton(
                    text = "Save In-Progress Audit",
                    onClick = onBackClick,
                    icon = SafeOpsIcons.Save,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp).navigationBarsPadding())
            }
        } else {
            // Inspection Register Table View
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                val columns = listOf(
                    SafeOpsTableColumn<InspectionRecord>(
                        title = "ID",
                        key = "id",
                        weight = 0.95f,
                        cellContent = { record ->
                            Text(
                                text = record.id,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0878FF),
                                fontSize = 12.sp
                            )
                        }
                    ),
                    SafeOpsTableColumn<InspectionRecord>(
                        title = "Inspection Title",
                        key = "title",
                        weight = 1.4f,
                        cellContent = { record ->
                            Text(
                                text = record.title,
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp,
                                color = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D)
                            )
                        }
                    ),
                    SafeOpsTableColumn<InspectionRecord>(
                        title = "Score",
                        key = "score",
                        weight = 0.8f,
                        cellContent = { record ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF2563EB).copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = record.score,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF60A5FA),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    ),
                    SafeOpsTableColumn<InspectionRecord>(
                        title = "Inspector",
                        key = "inspector",
                        weight = 1.1f,
                        cellContent = { record ->
                            Text(
                                text = record.inspector,
                                fontSize = 12.sp,
                                color = if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E)
                            )
                        }
                    ),
                    SafeOpsTableColumn<InspectionRecord>(
                        title = "Status",
                        key = "status",
                        weight = 1.1f,
                        cellContent = { record ->
                            val color = when (record.status) {
                                "Compliant" -> Color(0xFF16A34A)
                                else -> Color(0xFFF59E0B)
                            }
                            SafeOpsStatusPill(text = record.status, color = color)
                        }
                    )
                )

                var searchQuery by remember { mutableStateOf("") }
                var selectedFilter by remember { mutableStateOf<String?>("All") }

                SafeOpsDataTable(
                    columns = columns,
                    items = inspectionList.filter {
                        (searchQuery.isBlank() || it.title.contains(searchQuery, ignoreCase = true) || it.id.contains(searchQuery, ignoreCase = true)) &&
                        (selectedFilter == null || selectedFilter == "All" || it.status == selectedFilter)
                    },
                    onItemClick = {},
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    searchPlaceholder = "Search inspections by ID or title...",
                    filters = listOf("All", "Compliant", "Pending Rectification"),
                    selectedFilter = selectedFilter,
                    onFilterSelected = { selectedFilter = it },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
