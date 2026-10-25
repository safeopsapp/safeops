package com.safeops.app.feature.hazards.presentation

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
import com.safeops.app.core.designsystem.component.SafeOpsOutlinedButton
import com.safeops.app.core.designsystem.component.SafeOpsPrimaryButton
import com.safeops.app.core.designsystem.component.SafeOpsSegmentedControl
import com.safeops.app.core.designsystem.component.SafeOpsStatusPill
import com.safeops.app.core.designsystem.component.SafeOpsTableColumn
import com.safeops.app.core.designsystem.component.SafeOpsTextArea
import com.safeops.app.core.designsystem.component.SafeOpsTextField
import com.safeops.app.core.designsystem.component.SafeOpsTimePickerField
import com.safeops.app.core.designsystem.component.SafeOpsToggleRow
import com.safeops.app.core.designsystem.icon.SafeOpsIcons
import com.safeops.app.core.designsystem.theme.AccentOrange
import com.safeops.app.core.designsystem.theme.SafeOpsTheme
import com.safeops.app.domain.model.AuthenticatedSession
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class HazardRecord(
    val id: String,
    val title: String,
    val category: String,
    val severity: String,
    val location: String,
    val status: String,
    val date: String
)

@Composable
fun CreateHazardScreen(
    session: AuthenticatedSession,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isDark = SafeOpsTheme.colors.isDark
    val typography = SafeOpsTheme.typography
    val coroutineScope = rememberCoroutineScope()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Report Hazard", "Hazard Register")

    // Form States
    var hazardTitle by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Working at Height / Scaffolding") }
    var selectedSeverity by remember { mutableStateOf("High (Action within 2h)") }
    var locationZone by remember { mutableStateOf("Tower B - 4th Floor Edge") }
    var observationDate by remember { mutableStateOf("03 Oct 2026") }
    var observationTime by remember { mutableStateOf("14:30") }
    var hazardDescription by remember { mutableStateOf("Missing mid-rail and toe-board on north scaffolding bay. High risk of material or worker fall.") }
    var immediateAction by remember { mutableStateOf("Red danger warning tape applied immediately. Access ladder secured.") }
    var assignedContractor by remember { mutableStateOf("Apex Scaffolding Ltd") }
    var stopWorkIssued by remember { mutableStateOf(false) }
    var ehsLeadNotified by remember { mutableStateOf(true) }

    // Evidence Photos Mock
    var photoCount by remember { mutableIntStateOf(2) }

    // Submission States
    var isSubmitting by remember { mutableStateOf(false) }
    var showSuccessAlert by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }

    // Mock Hazard Register
    val hazardList = remember {
        mutableStateListOf(
            HazardRecord("HAZ-1081", "Missing scaffolding toe-board", "Working at Height", "High", "Tower B - 4th Floor", "Open", "Today"),
            HazardRecord("HAZ-1080", "Damaged 415V distribution cable", "Electrical Safety", "Critical", "Basement Pump Room", "Under Review", "Today"),
            HazardRecord("HAZ-1079", "Blocked emergency evacuation stairwell", "Fire & Egress", "Medium", "Core 1 - Level 2", "Resolved", "Yesterday"),
            HazardRecord("HAZ-1078", "Excavation trench water ingress", "Excavation", "Medium", "Zone 3 Trench A", "Resolved", "01 Oct"),
            HazardRecord("HAZ-1077", "Workers without chin straps at height", "PPE Compliance", "Low", "Tower A - Level 12", "Resolved", "30 Sep")
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
            // Fluid Curvy Wave Pattern (Zero gridlines, zero dots)
            SafeOpsCurvyHeroPattern(
                isDark = isDark,
                modifier = Modifier.matchParentSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                // Top Navigation Row
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
                                    text = " • HAZARDS",
                                    style = typography.bodySmall.copy(
                                        color = Color(0xFF38BDF8),
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp
                                    )
                                )
                            }
                            Text(
                                text = "Field Hazard & Risk Identification",
                                style = typography.caption.copy(
                                    color = Color.White.copy(alpha = 0.75f),
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }

                    // Severity Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFEF4444).copy(alpha = 0.22f))
                            .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "HIGH RISK",
                            style = typography.caption.copy(
                                color = Color(0xFFFCA5A5),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Segmented Tab Switcher (Report Form vs Hazard Register)
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
            // ── Form View ──
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                // Success Notification Banner
                AnimatedVisibility(
                    visible = showSuccessAlert,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Column {
                        SafeOpsAlertBanner(
                            type = SafeOpsAlertType.SUCCESS,
                            title = "Hazard Logged Successfully",
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

                // Section 1: Hazard Classification
                SafeOpsFormSection(
                    title = "1. Hazard Classification & Location",
                    subtitle = "Category, severity rating, and site work front",
                    icon = SafeOpsIcons.Warning
                ) {
                    SafeOpsDropdown(
                        label = "Hazard Category *",
                        selectedOption = selectedCategory,
                        options = listOf(
                            "Working at Height / Scaffolding",
                            "Electrical Hazards & Live Wiring",
                            "Excavation, Trenching & Cave-in",
                            "Lifting, Rigging & Crane Operations",
                            "Tripping, Slipping & Housekeeping",
                            "Chemical / Flammable Substances",
                            "PPE Non-Compliance",
                            "Heavy Vehicle & Plant Traffic",
                            "Confined Space Access"
                        ),
                        onOptionSelected = { selectedCategory = it },
                        leadingIcon = SafeOpsIcons.Checklist
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    SafeOpsDropdown(
                        label = "Risk / Severity Rating *",
                        selectedOption = selectedSeverity,
                        options = listOf(
                            "Critical (Imminent Danger - Stop Work)",
                            "High (Action within 2h)",
                            "Medium (Action within 24h)",
                            "Low (Informational / Minor)"
                        ),
                        onOptionSelected = { selectedSeverity = it },
                        leadingIcon = SafeOpsIcons.Shield
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    SafeOpsTextField(
                        value = hazardTitle,
                        onValueChange = { hazardTitle = it },
                        label = "Hazard Short Title *",
                        placeholder = "e.g., Unsecured scaffolding plank on Level 4",
                        leadingIcon = SafeOpsIcons.Document
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    SafeOpsTextField(
                        value = locationZone,
                        onValueChange = { locationZone = it },
                        label = "Exact Site Location & Grid *",
                        placeholder = "e.g., Tower B - North Facade, Grid D-4",
                        leadingIcon = SafeOpsIcons.Projects
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        SafeOpsDatePickerField(
                            value = observationDate,
                            onValueChange = { observationDate = it },
                            label = "Date Identified",
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        SafeOpsTimePickerField(
                            value = observationTime,
                            onValueChange = { observationTime = it },
                            label = "Time",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 2: Details & Immediate Action
                SafeOpsFormSection(
                    title = "2. Hazard Details & Corrective Action",
                    subtitle = "Specific conditions observed and on-site mitigation",
                    icon = SafeOpsIcons.Document
                ) {
                    SafeOpsTextArea(
                        value = hazardDescription,
                        onValueChange = { hazardDescription = it },
                        label = "Detailed Hazard Description *",
                        placeholder = "Describe what was observed, why it is dangerous, and potential consequences...",
                        minLines = 3,
                        maxChars = 400
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    SafeOpsTextArea(
                        value = immediateAction,
                        onValueChange = { immediateAction = it },
                        label = "Immediate Action Taken on Site *",
                        placeholder = "e.g., Area barricaded, power supply isolated, workers redeployed...",
                        minLines = 3,
                        maxChars = 400
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    SafeOpsTextField(
                        value = assignedContractor,
                        onValueChange = { assignedContractor = it },
                        label = "Assigned Subcontractor / Responsible Person",
                        placeholder = "e.g., Apex Scaffolding Ltd",
                        leadingIcon = SafeOpsIcons.Worker
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 3: Evidence & Safety Directives
                SafeOpsFormSection(
                    title = "3. Photographic Evidence & Controls",
                    subtitle = "Attach site photos and safety directives",
                    icon = SafeOpsIcons.Camera
                ) {
                    // Photo Upload Card
                    val surfaceColor = if (isDark) Color(0xFF18181B) else Color(0xFFFFFDFF)
                    val outlineColor = if (isDark) Color(0xFF333338) else Color(0xFFE4E2E9)

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(surfaceColor)
                            .border(1.dp, outlineColor, RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "Site Photo Attachments ($photoCount attached)",
                            style = typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D)
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Mock Thumbnail 1
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF0878FF).copy(alpha = 0.15f))
                                    .border(1.dp, Color(0xFF0878FF).copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = SafeOpsIcons.Camera,
                                        contentDescription = "Photo 1",
                                        tint = Color(0xFF38BDF8),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text("IMG_01", fontSize = 9.sp, color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold)
                                }
                            }

                            // Mock Thumbnail 2
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF16A34A).copy(alpha = 0.15f))
                                    .border(1.dp, Color(0xFF16A34A).copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = SafeOpsIcons.Camera,
                                        contentDescription = "Photo 2",
                                        tint = Color(0xFF34D399),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text("IMG_02", fontSize = 9.sp, color = Color(0xFF34D399), fontWeight = FontWeight.Bold)
                                }
                            }

                            // Add Photo Button
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isDark) Color(0xFF27272A) else Color(0xFFF1F5F9))
                                    .border(1.dp, outlineColor, RoundedCornerShape(8.dp))
                                    .clickable { photoCount++ },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = SafeOpsIcons.Add,
                                        contentDescription = "Add Photo",
                                        tint = if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E),
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Text("+ Add", fontSize = 10.sp, color = if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    SafeOpsToggleRow(
                        title = "Issue Stop Work Notice (Imminent Danger)",
                        subtitle = "Immediately orders halting operations in this zone",
                        checked = stopWorkIssued,
                        onCheckedChange = { stopWorkIssued = it },
                        icon = SafeOpsIcons.Alert
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    SafeOpsToggleRow(
                        title = "Safety Officer & Site Supervisor Notified",
                        subtitle = "Direct emergency broadcast sent via SafeOps Push",
                        checked = ehsLeadNotified,
                        onCheckedChange = { ehsLeadNotified = it },
                        icon = SafeOpsIcons.Notifications
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                SafeOpsPrimaryButton(
                    text = "Submit Hazard Report",
                    onClick = {
                        isSubmitting = true
                        coroutineScope.launch {
                            delay(1000)
                            isSubmitting = false
                            val newId = "HAZ-${(1082..1150).random()}"
                            val title = if (hazardTitle.isNotBlank()) hazardTitle else "Reported Hazard at $locationZone"
                            hazardList.add(0, HazardRecord(newId, title, selectedCategory, "High", locationZone, "Open", "Just now"))
                            successMessage = "Hazard record $newId created and dispatched to EHS supervision team."
                            showSuccessAlert = true
                        }
                    },
                    isLoading = isSubmitting,
                    icon = SafeOpsIcons.Check,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                SafeOpsOutlinedButton(
                    text = "Save Draft & Exit",
                    onClick = onBackClick,
                    icon = SafeOpsIcons.Save,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp).navigationBarsPadding())
            }
        } else {
            // ── Hazard Register Table View ──
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                val columns = listOf(
                    SafeOpsTableColumn<HazardRecord>(
                        title = "ID",
                        key = "id",
                        weight = 0.85f,
                        cellContent = { record ->
                            Text(
                                text = record.id,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0878FF),
                                fontSize = 12.5.sp
                            )
                        }
                    ),
                    SafeOpsTableColumn<HazardRecord>(
                        title = "Hazard Title",
                        key = "title",
                        weight = 1.35f,
                        cellContent = { record ->
                            Text(
                                text = record.title,
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp,
                                color = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D)
                            )
                        }
                    ),
                    SafeOpsTableColumn<HazardRecord>(
                        title = "Severity",
                        key = "severity",
                        weight = 1.0f,
                        cellContent = { record ->
                            val color = when (record.severity) {
                                "Critical" -> Color(0xFFEF4444)
                                "High" -> Color(0xFFF59E0B)
                                else -> Color(0xFF0878FF)
                            }
                            SafeOpsStatusPill(text = record.severity, color = color)
                        }
                    ),
                    SafeOpsTableColumn<HazardRecord>(
                        title = "Location",
                        key = "location",
                        weight = 1.1f,
                        cellContent = { record ->
                            Text(
                                text = record.location,
                                fontSize = 12.sp,
                                color = if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E)
                            )
                        }
                    ),
                    SafeOpsTableColumn<HazardRecord>(
                        title = "Status",
                        key = "status",
                        weight = 0.95f,
                        cellContent = { record ->
                            val color = when (record.status) {
                                "Resolved" -> Color(0xFF16A34A)
                                "Open" -> Color(0xFFEF4444)
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
                    items = hazardList.filter {
                        (searchQuery.isBlank() || it.title.contains(searchQuery, ignoreCase = true) || it.id.contains(searchQuery, ignoreCase = true)) &&
                        (selectedFilter == null || selectedFilter == "All" || it.status == selectedFilter || it.severity == selectedFilter)
                    },
                    onItemClick = {},
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    searchPlaceholder = "Search hazards by ID or title...",
                    filters = listOf("All", "High", "Critical", "Open", "Resolved"),
                    selectedFilter = selectedFilter,
                    onFilterSelected = { selectedFilter = it },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
