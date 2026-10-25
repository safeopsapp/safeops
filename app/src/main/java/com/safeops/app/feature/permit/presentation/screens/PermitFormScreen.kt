package com.safeops.app.feature.permit.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.safeops.app.core.designsystem.icon.SafeOpsIcons
import com.safeops.app.core.designsystem.theme.AccentOrange
import com.safeops.app.core.designsystem.theme.SafeOpsTheme
import com.safeops.app.feature.dashboards.presentation.SafeOpsHeroPattern
import com.safeops.app.domain.model.AuthenticatedSession
import com.safeops.app.feature.permit.data.repository.PermitRepository
import com.safeops.app.feature.permit.domain.model.ChecklistItemResponse
import com.safeops.app.feature.permit.domain.model.DailyChecklistReview
import com.safeops.app.feature.permit.domain.model.DailyEhsObservation
import com.safeops.app.feature.permit.domain.model.PermitFormModel
import com.safeops.app.feature.permit.domain.model.PermitHeader
import com.safeops.app.feature.permit.domain.model.PermitPersonnel
import com.safeops.app.feature.permit.domain.model.PermitSchedule
import com.safeops.app.feature.permit.domain.template.PermitTemplateRegistry
import com.safeops.app.feature.permit.domain.validation.PermitValidationEngine
import com.safeops.app.feature.permit.domain.validation.PermitValidationResult
import com.safeops.app.feature.permit.presentation.components.SafeOpsChecklistItemCard
import com.safeops.app.feature.permit.presentation.components.SafeOpsDailyChecklistTabStrip
import com.safeops.app.feature.permit.presentation.components.SafeOpsFormProgress
import com.safeops.app.feature.permit.presentation.components.SafeOpsPermitDateField
import com.safeops.app.feature.permit.presentation.components.SafeOpsPermitDropdown
import com.safeops.app.feature.permit.presentation.components.SafeOpsPermitMultilineField
import com.safeops.app.feature.permit.presentation.components.SafeOpsPermitNumberSelector
import com.safeops.app.feature.permit.presentation.components.SafeOpsPermitTextField
import com.safeops.app.feature.permit.presentation.components.SafeOpsPermitTimeField
import com.safeops.app.feature.permit.presentation.components.SafeOpsSectionCard
import com.safeops.app.feature.permit.presentation.components.SafeOpsSignatureField
import com.safeops.app.feature.permit.presentation.components.SafeOpsStickyActionBar
import com.safeops.app.feature.permit.presentation.components.SafeOpsSubPermitSelector
import com.safeops.app.feature.permit.presentation.components.SafeOpsValidationBanner
import kotlinx.coroutines.launch

@Composable
fun PermitFormScreen(
    initialPermit: PermitFormModel,
    repository: PermitRepository,
    session: AuthenticatedSession,
    onSubmitted: (PermitFormModel) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = SafeOpsTheme.colors.isDark
    val scope = rememberCoroutineScope()
    var currentPermit by remember { mutableStateOf(initialPermit) }
    var currentStep by remember { mutableStateOf(1) }
    var activeChecklistDay by remember { mutableStateOf(1) }
    var validationResult by remember { mutableStateOf<PermitValidationResult?>(null) }
    var autoSaveStatus by remember { mutableStateOf("Saved just now") }

    val template = remember(currentPermit.permitType) {
        PermitTemplateRegistry.getTemplate(currentPermit.permitType)
    }

    val scrollState = rememberScrollState()

    fun triggerAutoSave(updated: PermitFormModel) {
        currentPermit = updated
        scope.launch {
            repository.saveDraft(updated)
            autoSaveStatus = "Saved just now"
        }
    }

    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        val screenWidth = constraints.maxWidth.toFloat()
        val screenHeight = constraints.maxHeight.toFloat()
        val radius = if (screenHeight > 0f) screenHeight * 0.90f else 1800f

        val headerGradient = if (isDark) {
            Brush.verticalGradient(
                0.00f to Color(0xFF2979FF), // Luminous electric light blue at top
                0.30f to Color(0xFF1E88E5), // Vibrant azure blue
                0.65f to Color(0xFF1565C0), // Rich royal blue
                1.00f to Color(0xFF0D47A1)  // Deep oceanic blue
            )
        } else {
            Brush.verticalGradient(
                0.00f to Color(0xFF38BDF8), // Radiant sky/cyan light blue at top
                0.25f to Color(0xFF2979FF), // Electric light blue
                0.60f to Color(0xFF1E88E5), // Luminous azure
                1.00f to Color(0xFF1565C0)  // Fresh royal ocean blue
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isDark) Color(0xFF000000) else Color(0xFFF5F4FA))
        ) {
            // Header gradient background at top only
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .background(headerGradient)
            ) {
                // Luminous ambient top flare
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(
                            Brush.verticalGradient(
                                0.0f to Color(0xFF38BDF8).copy(alpha = if (isDark) 0.18f else 0.22f),
                                0.5f to Color(0xFF1E88E5).copy(alpha = if (isDark) 0.08f else 0.10f),
                                1.0f to Color.Transparent
                            )
                        )
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                // ── Top Bar with back button + Screen Title ──
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = SafeOpsIcons.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = currentPermit.permitType.title,
                        style = SafeOpsTheme.typography.screenTitle.copy(
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }

                // ── Step Progress Indicator (Step 2 Active) ──
                PermitStepIndicator(
                    currentStep = 2,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // ── Upward Curved Form Card ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(if (isDark) Color(0xFF18181B) else Color(0xFFFFFDFF))
                    .border(
                        1.dp,
                        if (isDark) Brush.verticalGradient(
                            listOf(
                                Color(0xFF38BDF8).copy(alpha = 0.65f),
                                Color(0xFF333338).copy(alpha = 0.30f)
                            )
                        ) else Brush.verticalGradient(
                            listOf(
                                Color(0xFFE4E2E9),
                                Color(0xFFE4E2E9)
                            )
                        ),
                        RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                    )
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Inner Form Scroll Area
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .verticalScroll(scrollState)
                            .padding(horizontal = 18.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Context Banner
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isDark) Color(0xFF1E293B) else Color(0xFFE3EFFF))
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = SafeOpsIcons.Document,
                                contentDescription = null,
                                tint = Color(0xFF0878FF),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFF0878FF).copy(alpha = 0.15f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = currentPermit.permitType.code,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFF0878FF)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = currentPermit.permitType.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D)
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Permit No: ${currentPermit.header.permitNumber} \u2022 Springfield Safety Ops",
                                    fontSize = 11.5.sp,
                                    color = if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E)
                                )
                            }
                        }

                        // Form Section Progress (Steps 1 to 6)
                        SafeOpsFormProgress(
                            currentStep = currentStep,
                            totalSteps = 6,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Validation Summary Banner
                        validationResult?.let { result ->
                            if (!result.isValid) {
                                SafeOpsValidationBanner(
                                    errors = result.errorList,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        // Active Step Section Content
                        when (currentStep) {
                            1 -> Step1ApplicationSection(
                                permit = currentPermit,
                                template = template,
                                onUpdate = { triggerAutoSave(it) }
                            )
                            2 -> Step2HseChecklistSection(
                                permit = currentPermit,
                                template = template,
                                activeDay = activeChecklistDay,
                                onDayChange = { activeChecklistDay = it },
                                onUpdate = { triggerAutoSave(it) }
                            )
                            3 -> Step3ContractorAssessmentSection(
                                permit = currentPermit,
                                onUpdate = { triggerAutoSave(it) }
                            )
                            4 -> Step4FinalReviewSection(
                                permit = currentPermit,
                                onUpdate = { triggerAutoSave(it) }
                            )
                            5 -> Step5ApprovalSection(
                                permit = currentPermit,
                                onUpdate = { triggerAutoSave(it) }
                            )
                            6 -> Step6CompletionAndLogsSection(
                                permit = currentPermit,
                                onUpdate = { triggerAutoSave(it) }
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Sticky Bottom Action Bar
                    SafeOpsStickyActionBar(
                        autoSaveStatus = autoSaveStatus,
                        isFinalStep = currentStep == 6,
                        onBack = if (currentStep > 1) { { currentStep-- } } else null,
                        onSaveDraft = {
                            triggerAutoSave(currentPermit)
                        },
                        onNext = {
                            if (currentStep < 6) {
                                currentStep++
                            } else {
                                val result = PermitValidationEngine.validateForSubmit(currentPermit)
                                validationResult = result
                                if (result.isValid) {
                                    scope.launch {
                                        repository.submitPermit(currentPermit)
                                        onSubmitted(currentPermit)
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
}

// ── STEP 1: Application Section ──────────────────────────────────────────
@Composable
private fun Step1ApplicationSection(
    permit: PermitFormModel,
    template: com.safeops.app.feature.permit.domain.template.PermitTemplate,
    onUpdate: (PermitFormModel) -> Unit
) {
    val isDark = SafeOpsTheme.colors.isDark
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Permit Header Card
        SafeOpsSectionCard(
            title = "Permit Header Details",
            subtitle = "System-generated permit details & location context",
            isCompleted = permit.header.workPerformedBy.isNotBlank() && permit.header.location.isNotBlank(),
            icon = SafeOpsIcons.Document
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SafeOpsPermitTextField(
                    value = permit.header.permitNumber,
                    onValueChange = {},
                    label = "Permit Number",
                    readOnly = true,
                    placeholder = "System Generated"
                )
                SafeOpsPermitDropdown(
                    selectedOption = permit.header.workPerformedBy,
                    options = listOf("Sairam Civil Infrastructure Ltd", "Apex Structural Welders", "Heavy Cranes South India", "HighRise Scaffolding Services"),
                    onOptionSelected = { onUpdate(permit.copy(header = permit.header.copy(workPerformedBy = it))) },
                    label = "Work Performed By (Contractor)",
                    isRequired = true
                )
                SafeOpsPermitDropdown(
                    selectedOption = permit.header.location,
                    options = listOf("Zone A - South Pier 14", "Zone B - Elevated Viaduct Span 4", "Zone C - Station Concourse 2", "Zone D - Station Facade Pillar 6"),
                    onOptionSelected = { onUpdate(permit.copy(header = permit.header.copy(location = it))) },
                    label = "Site Location",
                    isRequired = true
                )
            }
        }

        // Schedule Card
        SafeOpsSectionCard(
            title = "Work Schedule & Personnel Count",
            subtitle = "Must be submitted 1 day in advance",
            isCompleted = permit.schedule.workDate.isNotBlank() && permit.schedule.startTime.isNotBlank() && permit.schedule.endTime.isNotBlank(),
            icon = SafeOpsIcons.Date
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SafeOpsPermitDateField(
                    selectedDate = permit.schedule.workDate,
                    onDateSelected = { onUpdate(permit.copy(schedule = permit.schedule.copy(workDate = it))) },
                    label = "Work Date",
                    isRequired = true
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SafeOpsPermitTimeField(
                        selectedTime = permit.schedule.startTime,
                        onTimeSelected = { onUpdate(permit.copy(schedule = permit.schedule.copy(startTime = it))) },
                        label = "Start Time",
                        isRequired = true,
                        modifier = Modifier.weight(1f)
                    )
                    SafeOpsPermitTimeField(
                        selectedTime = permit.schedule.endTime,
                        onTimeSelected = { onUpdate(permit.copy(schedule = permit.schedule.copy(endTime = it))) },
                        label = "End Time",
                        isRequired = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                SafeOpsPermitNumberSelector(
                    count = permit.schedule.workerCount,
                    onCountChange = { onUpdate(permit.copy(schedule = permit.schedule.copy(workerCount = it))) },
                    label = "Number of Workers",
                    isRequired = true
                )
            }
        }

        // Common Site Engineer Personnel Block
        SafeOpsSectionCard(
            title = "Site Engineer / Supervisor Details",
            subtitle = "Primary engineering contact",
            isCompleted = permit.personnel.siteEngineerName.isNotBlank() && permit.personnel.siteEngineerContact.isNotBlank(),
            icon = SafeOpsIcons.Profile
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SafeOpsPermitTextField(
                    value = permit.personnel.siteEngineerName,
                    onValueChange = { onUpdate(permit.copy(personnel = permit.personnel.copy(siteEngineerName = it))) },
                    label = "Site Engineer / Site Supervisor",
                    isRequired = true,
                    placeholder = "Enter full name"
                )
                SafeOpsPermitTextField(
                    value = permit.personnel.siteEngineerContact,
                    onValueChange = { onUpdate(permit.copy(personnel = permit.personnel.copy(siteEngineerContact = it))) },
                    label = "Contact Number",
                    isRequired = true,
                    placeholder = "+91 98765 43210"
                )
            }
        }

        // Specialized Personnel Blocks for Lifting / Piling
        if (template.requiresLiftingTeam) {
            SafeOpsSectionCard(
                title = "Lifting Team Credentials",
                subtitle = "Mandatory certified personnel",
                isCompleted = permit.personnel.liftingSupervisorName.isNotBlank() && permit.personnel.craneOperatorLicenseNo.isNotBlank(),
                icon = SafeOpsIcons.Warning
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SafeOpsPermitTextField(
                        value = permit.personnel.liftingSupervisorName,
                        onValueChange = { onUpdate(permit.copy(personnel = permit.personnel.copy(liftingSupervisorName = it))) },
                        label = "Lifting Supervisor",
                        isRequired = true,
                        placeholder = "Enter lifting supervisor name"
                    )
                    SafeOpsPermitTextField(
                        value = permit.personnel.riggerSignalManName,
                        onValueChange = { onUpdate(permit.copy(personnel = permit.personnel.copy(riggerSignalManName = it))) },
                        label = "Rigger / Signal Man",
                        isRequired = true,
                        placeholder = "Enter rigger name"
                    )
                    SafeOpsPermitTextField(
                        value = permit.personnel.craneOperatorName,
                        onValueChange = { onUpdate(permit.copy(personnel = permit.personnel.copy(craneOperatorName = it))) },
                        label = "Crane Operator",
                        isRequired = true,
                        placeholder = "Enter crane operator name"
                    )
                    SafeOpsPermitTextField(
                        value = permit.personnel.craneOperatorLicenseNo,
                        onValueChange = { onUpdate(permit.copy(personnel = permit.personnel.copy(craneOperatorLicenseNo = it))) },
                        label = "Crane Operator License No",
                        isRequired = true,
                        placeholder = "e.g. TN-CR-2024-889"
                    )
                }
            }
        }

        if (template.requiresPilingTeam) {
            SafeOpsSectionCard(
                title = "Piling Rig Team Credentials",
                subtitle = "Mandatory piling rig team details",
                isCompleted = permit.personnel.pilingSupervisorName.isNotBlank() && permit.personnel.rigOperatorLicenseNo.isNotBlank(),
                icon = SafeOpsIcons.Warning
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SafeOpsPermitTextField(
                        value = permit.personnel.pilingSupervisorName,
                        onValueChange = { onUpdate(permit.copy(personnel = permit.personnel.copy(pilingSupervisorName = it))) },
                        label = "Piling Supervisor",
                        isRequired = true,
                        placeholder = "Enter piling supervisor name"
                    )
                    SafeOpsPermitTextField(
                        value = permit.personnel.riggerSignalManName,
                        onValueChange = { onUpdate(permit.copy(personnel = permit.personnel.copy(riggerSignalManName = it))) },
                        label = "Rigger / Signal Man",
                        isRequired = true,
                        placeholder = "Enter rigger name"
                    )
                    SafeOpsPermitTextField(
                        value = permit.personnel.rigOperatorName,
                        onValueChange = { onUpdate(permit.copy(personnel = permit.personnel.copy(rigOperatorName = it))) },
                        label = "Rig Operator",
                        isRequired = true,
                        placeholder = "Enter rig operator name"
                    )
                    SafeOpsPermitTextField(
                        value = permit.personnel.rigOperatorLicenseNo,
                        onValueChange = { onUpdate(permit.copy(personnel = permit.personnel.copy(rigOperatorLicenseNo = it))) },
                        label = "Rig Operator License No",
                        isRequired = true,
                        placeholder = "e.g. TN-RIG-2024-551"
                    )
                }
            }
        }

        // Night Work Reference Numbers
        if (template.requiresNightWorkReferences) {
            SafeOpsSectionCard(
                title = "Required Permit Reference Numbers",
                subtitle = "Mandatory referenced permits for night operation",
                isCompleted = permit.requiredPermitReferences.isNotEmpty(),
                icon = SafeOpsIcons.Document
            ) {
                var newRefInput by remember { mutableStateOf("") }
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SafeOpsPermitTextField(
                            value = newRefInput,
                            onValueChange = { newRefInput = it },
                            label = "Add Permit Reference Number",
                            placeholder = "e.g. SPR/HSE/WP/01",
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .padding(top = 22.dp)
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF0878FF))
                                .clickable {
                                    if (newRefInput.isNotBlank()) {
                                        onUpdate(permit.copy(requiredPermitReferences = permit.requiredPermitReferences + newRefInput))
                                        newRefInput = ""
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = SafeOpsIcons.Add, contentDescription = null, tint = Color.White)
                        }
                    }

                    // Chips of added references
                    permit.requiredPermitReferences.forEach { ref ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isDark) Color(0xFF000000) else Color(0xFFF1F5F9))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = ref, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D))
                            Text(
                                text = "Remove",
                                fontSize = 11.5.sp,
                                color = Color(0xFFEF4444),
                                modifier = Modifier.clickable {
                                    onUpdate(permit.copy(requiredPermitReferences = permit.requiredPermitReferences - ref))
                                }
                            )
                        }
                    }
                }
            }
        }

        // Sub-Permit Selector
        if (template.hasSubPermitAttachments) {
            SafeOpsSectionCard(
                title = "Sub-Permit Attachments",
                subtitle = "Select any attached sub-permits",
                isCompleted = true,
                icon = SafeOpsIcons.Document
            ) {
                SafeOpsSubPermitSelector(
                    selectedSubPermits = permit.selectedSubPermits,
                    onSubPermitToggled = { sub ->
                        val updated = if (permit.selectedSubPermits.contains(sub)) permit.selectedSubPermits - sub else permit.selectedSubPermits + sub
                        onUpdate(permit.copy(selectedSubPermits = updated))
                    }
                )
            }
        }

        // Scope of Work
        SafeOpsSectionCard(
            title = "Description of Work",
            subtitle = "Detailed description of planned operations",
            isCompleted = permit.descriptionOfWork.isNotBlank(),
            icon = SafeOpsIcons.Document
        ) {
            SafeOpsPermitMultilineField(
                value = permit.descriptionOfWork,
                onValueChange = { onUpdate(permit.copy(descriptionOfWork = it)) },
                label = "Description of Work",
                isRequired = true
            )
        }
    }
}

// ── STEP 2: HSE Checklist Section ─────────────────────────────────────────
@Composable
private fun Step2HseChecklistSection(
    permit: PermitFormModel,
    template: com.safeops.app.feature.permit.domain.template.PermitTemplate,
    activeDay: Int,
    onDayChange: (Int) -> Unit,
    onUpdate: (PermitFormModel) -> Unit
) {
    val currentDayChecklist = permit.dailyChecklists[activeDay]?.responses ?: emptyMap()
    val isDayCompleted = template.checklistItems.indices.all { idx ->
        currentDayChecklist[idx]?.status != null
    }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        SafeOpsSectionCard(
            title = "Safety Inspection Checklist",
            subtitle = "Daily inspection column for Days 1 to 7",
            isCompleted = isDayCompleted,
            icon = SafeOpsIcons.Check
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                SafeOpsDailyChecklistTabStrip(
                    activeDay = activeDay,
                    onDaySelected = onDayChange
                )

                template.checklistItems.forEachIndexed { index, itemText ->
                    SafeOpsChecklistItemCard(
                        itemIndex = index,
                        itemText = itemText,
                        response = currentDayChecklist[index],
                        onResponseChange = { updatedResponse ->
                            val updatedResponses = currentDayChecklist.toMutableMap()
                            updatedResponses[index] = updatedResponse
                            val updatedDailyMap = permit.dailyChecklists.toMutableMap()
                            updatedDailyMap[activeDay] = DailyChecklistReview(
                                dayNumber = activeDay,
                                responses = updatedResponses
                            )
                            onUpdate(permit.copy(dailyChecklists = updatedDailyMap))
                        }
                    )
                }
            }
        }
    }
}

// ── STEP 3: Contractor Assessment (Part 1B) ──────────────────────────────
@Composable
private fun Step3ContractorAssessmentSection(
    permit: PermitFormModel,
    onUpdate: (PermitFormModel) -> Unit
) {
    val isDark = SafeOpsTheme.colors.isDark
    val hse = permit.hseAssessment

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        SafeOpsSectionCard(
            title = "Part 1B — Contractor HSE Assessment",
            subtitle = "Mandatory contractor safety acknowledgements",
            isCompleted = hse.isAllAcknowledged && hse.contractorEngineerSig?.isSigned == true,
            icon = SafeOpsIcons.Shield
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Statement 1
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = hse.statement1Briefed,
                        onCheckedChange = { onUpdate(permit.copy(hseAssessment = hse.copy(statement1Briefed = it))) },
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF0878FF))
                    )
                    Text(
                        text = "1. Work personnel briefed on risk-impact assessment of the work place.",
                        fontSize = 13.sp,
                        color = if (isDark) Color.White else Color(0xFF0F172A)
                    )
                }

                // Statement 2
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = hse.statement2AreaSafe,
                        onCheckedChange = { onUpdate(permit.copy(hseAssessment = hse.copy(statement2AreaSafe = it))) },
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF0878FF))
                    )
                    Text(
                        text = "2. Work area is Safe. Environment aspects eliminated.",
                        fontSize = 13.sp,
                        color = if (isDark) Color.White else Color(0xFF0F172A)
                    )
                }

                // Statement 3
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = hse.statement3MeasuresTaken,
                        onCheckedChange = { onUpdate(permit.copy(hseAssessment = hse.copy(statement3MeasuresTaken = it))) },
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF0878FF))
                    )
                    Text(
                        text = "3. Reasonably practicable measures are / will be taken to safeguard safety & health of work personnel.",
                        fontSize = 13.sp,
                        color = if (isDark) Color.White else Color(0xFF0F172A)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Signatures
                SafeOpsSignatureField(
                    signerLabel = "Contractor Engineer / Supervisor Signature",
                    signatureData = hse.contractorEngineerSig,
                    onSignatureCaptured = { onUpdate(permit.copy(hseAssessment = hse.copy(contractorEngineerSig = it))) },
                    isRequired = true
                )
                SafeOpsSignatureField(
                    signerLabel = "Contractor HSE Personnel Signature",
                    signatureData = hse.contractorHseSig,
                    onSignatureCaptured = { onUpdate(permit.copy(hseAssessment = hse.copy(contractorHseSig = it))) }
                )
                SafeOpsSignatureField(
                    signerLabel = "Contractor PM / CM Signature",
                    signatureData = hse.contractorPmSig,
                    onSignatureCaptured = { onUpdate(permit.copy(hseAssessment = hse.copy(contractorPmSig = it))) }
                )
            }
        }
    }
}

// ── STEP 4: Final Review (Part 2) ────────────────────────────────────────
@Composable
private fun Step4FinalReviewSection(
    permit: PermitFormModel,
    onUpdate: (PermitFormModel) -> Unit
) {
    val isDark = SafeOpsTheme.colors.isDark
    val review = permit.review

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        SafeOpsSectionCard(
            title = "Part 2 — Final Review by SPR",
            subtitle = "SPR Engineer & HSE Person verification",
            isCompleted = review.engineerStatement && review.hseStatement && review.sprEngineerSig?.isSigned == true,
            icon = SafeOpsIcons.Check
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = review.engineerStatement,
                        onCheckedChange = { onUpdate(permit.copy(review = review.copy(engineerStatement = it))) },
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF0878FF))
                    )
                    Text(
                        text = "SPR Engineer: I am aware of intended works and all safe work procedures will be in place.",
                        fontSize = 13.sp,
                        color = if (isDark) Color.White else Color(0xFF0F172A)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = review.hseStatement,
                        onCheckedChange = { onUpdate(permit.copy(review = review.copy(hseStatement = it))) },
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF0878FF))
                    )
                    Text(
                        text = "SPR HSE Person: I have reviewed risk assessment and instructed contractors to eliminate risks.",
                        fontSize = 13.sp,
                        color = if (isDark) Color.White else Color(0xFF0F172A)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                SafeOpsSignatureField(
                    signerLabel = "SPR Engineer / Supervisor Signature",
                    signatureData = review.sprEngineerSig,
                    onSignatureCaptured = { onUpdate(permit.copy(review = review.copy(sprEngineerSig = it))) },
                    isRequired = true
                )
                SafeOpsSignatureField(
                    signerLabel = "SPR HSE Person Signature",
                    signatureData = review.sprHseSig,
                    onSignatureCaptured = { onUpdate(permit.copy(review = review.copy(sprHseSig = it))) }
                )
            }
        }
    }
}

// ── STEP 5: Approval Section (Part 3) ────────────────────────────────────
@Composable
private fun Step5ApprovalSection(
    permit: PermitFormModel,
    onUpdate: (PermitFormModel) -> Unit
) {
    val isDark = SafeOpsTheme.colors.isDark
    val approval = permit.approval

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        SafeOpsSectionCard(
            title = "Part 3 — Approval",
            subtitle = "SPR Project Manager / HOD approval",
            isCompleted = approval.hiracBriefed && approval.controlsInPlace && approval.riskReduced && approval.approverSig?.isSigned == true,
            icon = SafeOpsIcons.Shield
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = approval.hiracBriefed,
                        onCheckedChange = { onUpdate(permit.copy(approval = approval.copy(hiracBriefed = it))) },
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF0878FF))
                    )
                    Text(
                        text = "HIRAC / Risk Assessment briefed to work personnel.",
                        fontSize = 13.sp,
                        color = if (isDark) Color.White else Color(0xFF0F172A)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = approval.controlsInPlace,
                        onCheckedChange = { onUpdate(permit.copy(approval = approval.copy(controlsInPlace = it))) },
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF0878FF))
                    )
                    Text(
                        text = "All control measures are in place.",
                        fontSize = 13.sp,
                        color = if (isDark) Color.White else Color(0xFF0F172A)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = approval.riskReduced,
                        onCheckedChange = { onUpdate(permit.copy(approval = approval.copy(riskReduced = it))) },
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF0878FF))
                    )
                    Text(
                        text = "Reduces risk & impact level to minimum.",
                        fontSize = 13.sp,
                        color = if (isDark) Color.White else Color(0xFF0F172A)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                SafeOpsSignatureField(
                    signerLabel = "SPR Project Manager / HOD Signature",
                    signatureData = approval.approverSig,
                    onSignatureCaptured = { onUpdate(permit.copy(approval = approval.copy(approverSig = it))) },
                    isRequired = true
                )
            }
        }
    }
}

// ── STEP 6: Completion & Page 2 Log Section ──────────────────────────────
@Composable
private fun Step6CompletionAndLogsSection(
    permit: PermitFormModel,
    onUpdate: (PermitFormModel) -> Unit
) {
    val isDark = SafeOpsTheme.colors.isDark
    val completion = permit.completion
    val confirmation = permit.confirmation

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Part 5 Contractor Completion
        SafeOpsSectionCard(
            title = "Part 5 — Notification of Completion (Contractor)",
            subtitle = "Contractor work completion notice",
            isCompleted = completion.workCompleted && completion.contractorSig?.isSigned == true,
            icon = SafeOpsIcons.Check
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = completion.workCompleted,
                        onCheckedChange = { onUpdate(permit.copy(completion = completion.copy(workCompleted = it))) },
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF0878FF))
                    )
                    Text("Work has completed", fontSize = 13.sp, color = if (isDark) Color.White else Color(0xFF0F172A))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = completion.housekeepingCarriedOut,
                        onCheckedChange = { onUpdate(permit.copy(completion = completion.copy(housekeepingCarriedOut = it))) },
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF0878FF))
                    )
                    Text("Housekeeping has been carried out", fontSize = 13.sp, color = if (isDark) Color.White else Color(0xFF0F172A))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = completion.areaSafe,
                        onCheckedChange = { onUpdate(permit.copy(completion = completion.copy(areaSafe = it))) },
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF0878FF))
                    )
                    Text("Work area is safe for other personnel", fontSize = 13.sp, color = if (isDark) Color.White else Color(0xFF0F172A))
                }
                SafeOpsSignatureField(
                    signerLabel = "Contractor Signature",
                    signatureData = completion.contractorSig,
                    onSignatureCaptured = { onUpdate(permit.copy(completion = completion.copy(contractorSig = it))) }
                )
            }
        }

        // Part 6 SPR Confirmation
        SafeOpsSectionCard(
            title = "Part 6 — Final Confirmation (SPR)",
            subtitle = "SPR Engineer site verification",
            isCompleted = confirmation.workCompleted && confirmation.sprEngineerSig?.isSigned == true,
            icon = SafeOpsIcons.Check
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = confirmation.workCompleted,
                        onCheckedChange = { onUpdate(permit.copy(confirmation = confirmation.copy(workCompleted = it))) },
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF0878FF))
                    )
                    Text("Work has completed", fontSize = 13.sp, color = if (isDark) Color.White else Color(0xFF0F172A))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = confirmation.housekeepingCarriedOut,
                        onCheckedChange = { onUpdate(permit.copy(confirmation = confirmation.copy(housekeepingCarriedOut = it))) },
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF0878FF))
                    )
                    Text("Housekeeping has been carried out", fontSize = 13.sp, color = if (isDark) Color.White else Color(0xFF0F172A))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = confirmation.areaSafe,
                        onCheckedChange = { onUpdate(permit.copy(confirmation = confirmation.copy(areaSafe = it))) },
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF0878FF))
                    )
                    Text("Work area is safe for other personnel", fontSize = 13.sp, color = if (isDark) Color.White else Color(0xFF0F172A))
                }
                SafeOpsSignatureField(
                    signerLabel = "SPR Engineer Signature",
                    signatureData = confirmation.sprEngineerSig,
                    onSignatureCaptured = { onUpdate(permit.copy(confirmation = confirmation.copy(sprEngineerSig = it))) }
                )
            }
        }

        // Second Page Daily EHS Observation Log
        SafeOpsSectionCard(
            title = "Daily EHS Observation Log (Page 2)",
            subtitle = "Record daily site safety observations (Max 7 records)",
            isCompleted = permit.dailyObservations.isNotEmpty(),
            icon = SafeOpsIcons.Document
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                permit.dailyObservations.forEachIndexed { idx, obs ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isDark) Color(0xFF000000) else Color(0xFFF1F5F9))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(text = "Day ${obs.dayNumber} — ${obs.date.ifBlank { "Date not set" }}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0878FF))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Job: ${obs.jobDescriptionWorkmenDetails.ifBlank { "N/A" }}", fontSize = 12.sp, color = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D))
                            Text(text = "Observation: ${obs.ehsObservation.ifBlank { "N/A" }}", fontSize = 12.sp, color = if (isDark) Color(0xFFA6A3AA) else Color(0xFF64748B))
                        }
                    }
                }

                if (permit.dailyObservations.size < 7) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFF0878FF), RoundedCornerShape(12.dp))
                            .clickable {
                                val nextDayNum = permit.dailyObservations.size + 1
                                val newObs = DailyEhsObservation(dayNumber = nextDayNum)
                                onUpdate(permit.copy(dailyObservations = permit.dailyObservations + newObs))
                            }
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "+ Add Daily Observation (Day ${permit.dailyObservations.size + 1})", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0878FF))
                    }
                }
            }
        }
    }
}
