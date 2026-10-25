package com.safeops.app.feature.incidents.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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

data class IncidentRecord(
    val id: String,
    val title: String,
    val classification: String,
    val severity: String,
    val location: String,
    val status: String,
    val date: String
)

@Composable
fun CreateIncidentScreen(
    session: AuthenticatedSession,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isDark = SafeOpsTheme.colors.isDark
    val typography = SafeOpsTheme.typography
    val coroutineScope = rememberCoroutineScope()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Report Incident", "Incident Log")

    // Form Fields
    var incidentTitle by remember { mutableStateOf("") }
    var selectedClassification by remember { mutableStateOf("Near Miss (No injury / No damage)") }
    var selectedSeverity by remember { mutableStateOf("Level 2 - Moderate (Medical treatment)") }
    var locationArea by remember { mutableStateOf("Main Basement Ramp, Zone 2") }
    var incidentDate by remember { mutableStateOf("03 Oct 2026") }
    var incidentTime by remember { mutableStateOf("11:15") }

    var injuredPersonName by remember { mutableStateOf("R. Kumar (ID: SCPL-W892)") }
    var subcontractorCompany by remember { mutableStateOf("SteelWorks Infrastructure Ltd") }
    var natureOfInjury by remember { mutableStateOf("Laceration / Deep Cut") }
    var medicalAttention by remember { mutableStateOf("Treated by On-site First Aider") }
    var eyewitnesses by remember { mutableStateOf("M. Mohan (Scaffolder Lead), S. Vijay (Crane Operator)") }

    var sequenceOfEvents by remember { mutableStateOf("While transferring steel rebar bundle, crane tag line snagged against edge shoring, causing minor swing.") }
    var emergencyContainment by remember { mutableStateOf("Load lowered to ground level immediately. First aid applied to minor arm laceration. Rigging inspected.") }
    var rootCauseAnalysis by remember { mutableStateOf("Inadequate tagline tension and gusty crosswinds in basement access ramp.") }
    var isOshaReportable by remember { mutableStateOf(false) }
    var isWorkSuspended by remember { mutableStateOf(true) }

    // Submission states
    var isSubmitting by remember { mutableStateOf(false) }
    var showSuccessAlert by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }

    // Mock Incident Log Data
    val incidentList = remember {
        mutableStateListOf(
            IncidentRecord("INC-2026-088", "Crane tagline rebar snag during transfer", "Near Miss", "Level 2", "Basement Ramp Zone 2", "Under Investigation", "Today"),
            IncidentRecord("INC-2026-087", "Worker finger pinch while handling shoring jack", "First Aid Case", "Level 1", "Tower B Level 1", "Closed", "Yesterday"),
            IncidentRecord("INC-2026-086", "Hydraulic fluid hose leak on excavator EX-04", "Environmental", "Level 2", "Zone 4 Excavation", "Closed", "29 Sep"),
            IncidentRecord("INC-2026-085", "Dropped hand tool from scaffolding (caught by net)", "Near Miss", "Level 3", "Tower A Level 9", "Closed", "27 Sep")
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
                                    text = " • INCIDENTS",
                                    style = typography.bodySmall.copy(
                                        color = Color(0xFF38BDF8),
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp
                                    )
                                )
                            }
                            Text(
                                text = "Official Site Incident & Accident Log",
                                style = typography.caption.copy(
                                    color = Color.White.copy(alpha = 0.75f),
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }

                    // Siren Severity Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF59E0B).copy(alpha = 0.22f))
                            .border(1.dp, Color(0xFFF59E0B).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "INCIDENT",
                            style = typography.caption.copy(
                                color = Color(0xFFFDE68A),
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
            // Form View
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
                            title = "Incident Report Dispatched",
                            message = successMessage,
                            onDismiss = { showSuccessAlert = false },
                            actionLabel = "View Incident Log",
                            onActionClick = {
                                showSuccessAlert = false
                                selectedTabIndex = 1
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // Section 1: Overview & Location
                SafeOpsFormSection(
                    title = "1. Incident Classification & Location",
                    subtitle = "Category, severity tier, and site area",
                    icon = SafeOpsIcons.Siren
                ) {
                    SafeOpsDropdown(
                        label = "Incident Classification *",
                        selectedOption = selectedClassification,
                        options = listOf(
                            "Near Miss (No injury / No damage)",
                            "First Aid Case (FAC)",
                            "Medical Treatment Case (MTC)",
                            "Lost Time Injury (LTI)",
                            "Dangerous Occurrence / Structural",
                            "Environmental Spill / Release",
                            "Fire / Explosion Incident"
                        ),
                        onOptionSelected = { selectedClassification = it },
                        leadingIcon = SafeOpsIcons.Siren
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    SafeOpsDropdown(
                        label = "Severity Level *",
                        selectedOption = selectedSeverity,
                        options = listOf(
                            "Level 1 - Minor (First aid on site)",
                            "Level 2 - Moderate (Medical treatment)",
                            "Level 3 - Serious (Lost time < 3 days)",
                            "Level 4 - Major (Hospitalization > 3 days)",
                            "Level 5 - Catastrophic (Severe/Fatal)"
                        ),
                        onOptionSelected = { selectedSeverity = it },
                        leadingIcon = SafeOpsIcons.Warning
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    SafeOpsTextField(
                        value = incidentTitle,
                        onValueChange = { incidentTitle = it },
                        label = "Incident Title *",
                        placeholder = "e.g., Crane tagline rebar snag during transfer",
                        leadingIcon = SafeOpsIcons.Document
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    SafeOpsTextField(
                        value = locationArea,
                        onValueChange = { locationArea = it },
                        label = "Project Work Front & Area *",
                        placeholder = "e.g., Main Basement Ramp, Zone 2",
                        leadingIcon = SafeOpsIcons.Projects
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        SafeOpsDatePickerField(
                            value = incidentDate,
                            onValueChange = { incidentDate = it },
                            label = "Date of Incident",
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        SafeOpsTimePickerField(
                            value = incidentTime,
                            onValueChange = { incidentTime = it },
                            label = "Time",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 2: Involved Parties & Medical Response
                SafeOpsFormSection(
                    title = "2. Personnel Involved & Medical Attention",
                    subtitle = "Injured workers, subcontractors, and first-aid status",
                    icon = SafeOpsIcons.Worker
                ) {
                    SafeOpsTextField(
                        value = injuredPersonName,
                        onValueChange = { injuredPersonName = it },
                        label = "Person Involved / Injured Worker",
                        placeholder = "Worker name, trade, and employee badge number",
                        leadingIcon = SafeOpsIcons.Worker
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    SafeOpsTextField(
                        value = subcontractorCompany,
                        onValueChange = { subcontractorCompany = it },
                        label = "Subcontractor / Direct Employer",
                        placeholder = "e.g., SteelWorks Infrastructure Ltd",
                        leadingIcon = SafeOpsIcons.Admin
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    SafeOpsDropdown(
                        label = "Nature of Injury / Impact",
                        selectedOption = natureOfInjury,
                        options = listOf(
                            "None (Near Miss Event)",
                            "Laceration / Deep Cut",
                            "Pinch / Crush Injury",
                            "Fracture / Suspected Bone Injury",
                            "Foreign Body in Eye",
                            "Sprain / Musculoskeletal Strain",
                            "Burn (Thermal / Chemical)",
                            "Head Injury / Concussion"
                        ),
                        onOptionSelected = { natureOfInjury = it },
                        leadingIcon = SafeOpsIcons.Shield
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    SafeOpsDropdown(
                        label = "Medical Treatment Provided",
                        selectedOption = medicalAttention,
                        options = listOf(
                            "Treated by On-site First Aider",
                            "Referred to Designated Site Medical Clinic",
                            "Emergency Hospital Transfer via Ambulance",
                            "None Required (Near Miss)"
                        ),
                        onOptionSelected = { medicalAttention = it },
                        leadingIcon = SafeOpsIcons.Check
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    SafeOpsTextArea(
                        value = eyewitnesses,
                        onValueChange = { eyewitnesses = it },
                        label = "Witness Names & Contact Numbers",
                        placeholder = "List names and designations of on-site witnesses...",
                        minLines = 2,
                        maxChars = 300
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 3: Sequence of Events & Containment
                SafeOpsFormSection(
                    title = "3. Event Sequence & Root Cause Analysis",
                    subtitle = "Chronological account and immediate remedial action",
                    icon = SafeOpsIcons.Checklist
                ) {
                    SafeOpsTextArea(
                        value = sequenceOfEvents,
                        onValueChange = { sequenceOfEvents = it },
                        label = "Detailed Sequence of Events *",
                        placeholder = "Explain what happened leading up to, during, and immediately following the incident...",
                        minLines = 3,
                        maxChars = 600
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    SafeOpsTextArea(
                        value = emergencyContainment,
                        onValueChange = { emergencyContainment = it },
                        label = "Immediate Containment Action Taken *",
                        placeholder = "Emergency response, first aid given, area cordoned...",
                        minLines = 3,
                        maxChars = 400
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    SafeOpsTextArea(
                        value = rootCauseAnalysis,
                        onValueChange = { rootCauseAnalysis = it },
                        label = "Preliminary Root Cause Notes",
                        placeholder = "Equipment defect, procedural failure, lack of supervision, weather...",
                        minLines = 3,
                        maxChars = 400
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    SafeOpsToggleRow(
                        title = "Reportable to Statutory Authorities",
                        subtitle = "OSHA / Government Factory Inspectorate reportable threshold met",
                        checked = isOshaReportable,
                        onCheckedChange = { isOshaReportable = it },
                        icon = SafeOpsIcons.Alert
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    SafeOpsToggleRow(
                        title = "Suspend Operations in Affected Zone",
                        subtitle = "Area remains quarantined until formal EHS clearance",
                        checked = isWorkSuspended,
                        onCheckedChange = { isWorkSuspended = it },
                        icon = SafeOpsIcons.Warning
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                SafeOpsPrimaryButton(
                    text = "Submit Official Incident Report",
                    onClick = {
                        isSubmitting = true
                        coroutineScope.launch {
                            delay(1000)
                            isSubmitting = false
                            val newId = "INC-2026-${(90..150).random()}"
                            val title = if (incidentTitle.isNotBlank()) incidentTitle else "Site Incident at $locationArea"
                            incidentList.add(0, IncidentRecord(newId, title, selectedClassification, "Level 2", locationArea, "Under Investigation", "Just now"))
                            successMessage = "Formal Incident Report $newId lodged and broadcasted to Project Director & EHS Lead."
                            showSuccessAlert = true
                        }
                    },
                    isLoading = isSubmitting,
                    icon = SafeOpsIcons.Check,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                SafeOpsOutlinedButton(
                    text = "Save In-Progress Draft",
                    onClick = onBackClick,
                    icon = SafeOpsIcons.Save,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp).navigationBarsPadding())
            }
        } else {
            // Incident Log Table View
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                val columns = listOf(
                    SafeOpsTableColumn<IncidentRecord>(
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
                    SafeOpsTableColumn<IncidentRecord>(
                        title = "Incident Title",
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
                    SafeOpsTableColumn<IncidentRecord>(
                        title = "Classification",
                        key = "classification",
                        weight = 1.0f,
                        cellContent = { record ->
                            Text(
                                text = record.classification,
                                fontSize = 12.sp,
                                color = if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E)
                            )
                        }
                    ),
                    SafeOpsTableColumn<IncidentRecord>(
                        title = "Severity",
                        key = "severity",
                        weight = 0.9f,
                        cellContent = { record ->
                            val color = when (record.severity) {
                                "Level 4", "Level 5" -> Color(0xFFEF4444)
                                "Level 3", "Level 2" -> Color(0xFFF59E0B)
                                else -> Color(0xFF0878FF)
                            }
                            SafeOpsStatusPill(text = record.severity, color = color)
                        }
                    ),
                    SafeOpsTableColumn<IncidentRecord>(
                        title = "Status",
                        key = "status",
                        weight = 1.1f,
                        cellContent = { record ->
                            val color = when (record.status) {
                                "Closed" -> Color(0xFF16A34A)
                                "Under Investigation" -> Color(0xFFF59E0B)
                                else -> Color(0xFFEF4444)
                            }
                            SafeOpsStatusPill(text = record.status, color = color)
                        }
                    )
                )

                var searchQuery by remember { mutableStateOf("") }
                var selectedFilter by remember { mutableStateOf<String?>("All") }

                SafeOpsDataTable(
                    columns = columns,
                    items = incidentList.filter {
                        (searchQuery.isBlank() || it.title.contains(searchQuery, ignoreCase = true) || it.id.contains(searchQuery, ignoreCase = true)) &&
                        (selectedFilter == null || selectedFilter == "All" || it.classification == selectedFilter || it.status == selectedFilter)
                    },
                    onItemClick = {},
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    searchPlaceholder = "Search incidents by title or ID...",
                    filters = listOf("All", "Near Miss", "First Aid", "Under Investigation", "Closed"),
                    selectedFilter = selectedFilter,
                    onFilterSelected = { selectedFilter = it },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
