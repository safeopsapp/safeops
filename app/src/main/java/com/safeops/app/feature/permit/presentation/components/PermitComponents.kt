package com.safeops.app.feature.permit.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.safeops.app.core.designsystem.component.SafeOpsDatePickerField
import com.safeops.app.core.designsystem.component.SafeOpsDropdown
import com.safeops.app.core.designsystem.component.SafeOpsNumberStepper
import com.safeops.app.core.designsystem.component.SafeOpsTextArea
import com.safeops.app.core.designsystem.component.SafeOpsTimePickerField
import com.safeops.app.core.designsystem.icon.SafeOpsIcons
import com.safeops.app.core.designsystem.theme.SafeOpsTheme
import com.safeops.app.feature.permit.domain.model.ChecklistItemResponse
import com.safeops.app.feature.permit.domain.model.ChecklistStatus
import com.safeops.app.feature.permit.domain.model.SignatureData
import com.safeops.app.feature.permit.domain.model.SubPermitAttachment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ── 1. SafeOpsPermitTextField ────────────────────────────────────────────
@Composable
fun SafeOpsPermitTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "Enter text",
    isRequired: Boolean = false,
    errorMessage: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    readOnly: Boolean = false,
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    val isDark = SafeOpsTheme.colors.isDark
    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D)
            )
            if (isRequired) {
                Text(
                    text = " *",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFEF4444)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, fontSize = 13.5.sp, color = if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E)) },
            readOnly = readOnly,
            isError = !errorMessage.isNullOrBlank(),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = ImeAction.Next),
            singleLine = true,
            leadingIcon = leadingIcon?.let {
                { Icon(imageVector = it, contentDescription = null, tint = if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E), modifier = Modifier.size(18.dp)) }
            },
            trailingIcon = trailingIcon,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = if (isDark) Color(0xFF18181B) else Color(0xFFFFFDFF),
                unfocusedContainerColor = if (isDark) Color(0xFF18181B) else Color(0xFFFFFDFF),
                disabledContainerColor = if (isDark) Color(0xFF000000) else Color(0xFFF5F4FA),
                focusedBorderColor = Color(0xFF0878FF),
                unfocusedBorderColor = if (isDark) Color(0xFF333338) else Color(0xFFE4E2E9),
                errorBorderColor = Color(0xFFEF4444),
                focusedTextColor = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D),
                unfocusedTextColor = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D)
            )
        )
        if (!errorMessage.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage,
                fontSize = 11.5.sp,
                color = Color(0xFFEF4444),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ── 2. SafeOpsPermitNumberSelector (Worker Count 1..7) ───────────────────
@Composable
fun SafeOpsPermitNumberSelector(
    count: Int,
    onCountChange: (Int) -> Unit,
    label: String = "Number of Workers",
    isRequired: Boolean = true,
    minCount: Int = 1,
    maxCount: Int = 7,
    modifier: Modifier = Modifier
) {
    SafeOpsNumberStepper(
        value = count,
        onValueChange = onCountChange,
        label = if (isRequired) "$label *" else label,
        minValue = minCount,
        maxValue = maxCount,
        min = minCount,
        max = maxCount,
        unit = "Workers",
        modifier = modifier
    )
}

// ── 3. Date & Time Pickers ───────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafeOpsPermitDateField(
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    label: String = "Work Date",
    isRequired: Boolean = true,
    errorMessage: String? = null,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    SafeOpsDatePickerField(
        value = selectedDate,
        label = if (isRequired) "$label *" else label,
        placeholder = "Select Work Date",
        onDateClick = { showDialog = true },
        modifier = modifier
    )

    if (showDialog) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val formatted = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(millis))
                        onDateSelected(formatted)
                    }
                    showDialog = false
                }) { Text("Select", fontWeight = FontWeight.Bold, color = Color(0xFF0878FF)) }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafeOpsPermitTimeField(
    selectedTime: String,
    onTimeSelected: (String) -> Unit,
    label: String = "Start Time",
    isRequired: Boolean = true,
    errorMessage: String? = null,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    val isDark = SafeOpsTheme.colors.isDark

    SafeOpsTimePickerField(
        value = selectedTime,
        label = if (isRequired) "$label *" else label,
        placeholder = "Select Time",
        onTimeClick = { showDialog = true },
        modifier = modifier
    )

    if (showDialog) {
        val timePickerState = rememberTimePickerState(initialHour = 8, initialMinute = 0)
        Dialog(onDismissRequest = { showDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (isDark) Color(0xFF18181B) else Color(0xFFFFFDFF),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFF333338) else Color(0xFFE4E2E9)),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Select Time",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TimePicker(state = timePickerState)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showDialog = false }) { Text("Cancel") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val hour = timePickerState.hour
                                val minute = timePickerState.minute
                                val amPm = if (hour >= 12) "PM" else "AM"
                                val formattedHour = if (hour % 12 == 0) 12 else hour % 12
                                val formatted = String.format("%02d:%02d %s", formattedHour, minute, amPm)
                                onTimeSelected(formatted)
                                showDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0878FF))
                        ) { Text("OK", color = Color.White) }
                    }
                }
            }
        }
    }
}

// ── 4. SafeOpsPermitDropdown ─────────────────────────────────────────────
@Composable
fun SafeOpsPermitDropdown(
    selectedOption: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit,
    label: String,
    isRequired: Boolean = false,
    placeholder: String = "Select Option",
    modifier: Modifier = Modifier
) {
    SafeOpsDropdown(
        options = options,
        selectedOption = selectedOption,
        onOptionSelected = onOptionSelected,
        label = if (isRequired) "$label *" else label,
        placeholder = placeholder,
        modifier = modifier
    )
}

// ── 5. SafeOpsPermitMultilineField ───────────────────────────────────────
@Composable
fun SafeOpsPermitMultilineField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "Description of Work",
    placeholder: String = "Describe the detailed scope of work...",
    isRequired: Boolean = true,
    errorMessage: String? = null,
    modifier: Modifier = Modifier
) {
    SafeOpsTextArea(
        value = value,
        onValueChange = onValueChange,
        label = if (isRequired) "$label *" else label,
        placeholder = placeholder,
        errorMessage = errorMessage,
        modifier = modifier
    )
}

// ── 6. SafeOpsChecklistItemCard (OK / NOT OK / N/A Segmented Control) ────
@Composable
fun SafeOpsChecklistItemCard(
    itemIndex: Int,
    itemText: String,
    response: ChecklistItemResponse?,
    onResponseChange: (ChecklistItemResponse) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = SafeOpsTheme.colors.isDark
    val currentStatus = response?.status

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF18181B) else Color(0xFFFFFDFF)),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFF333338) else Color(0xFFE4E2E9))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0878FF).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("${itemIndex + 1}", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0878FF))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = itemText,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Segmented Status Selector Buttons: [ OK ] [ NOT OK ] [ N/A ]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isDark) Color(0xFF000000) else Color(0xFFEDF2F7))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ChecklistStatus.values().forEach { status ->
                    val isSelected = currentStatus == status
                    val activeBg = when (status) {
                        ChecklistStatus.OK -> Color(0xFF10B981)
                        ChecklistStatus.NOT_OK -> Color(0xFFEF4444)
                        ChecklistStatus.NOT_APPLICABLE -> Color(0xFF64748B)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) activeBg else Color.Transparent)
                            .clickable {
                                val updated = (response ?: ChecklistItemResponse(itemIndex, itemText)).copy(status = status)
                                onResponseChange(updated)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = status.label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                            color = if (isSelected) Color.White else (if (isDark) Color(0xFFA6A3AA) else Color(0xFF475569))
                        )
                    }
                }
            }

            // If NOT OK selected, observation & corrective action input cards are immediately rendered
            AnimatedVisibility(visible = currentStatus == ChecklistStatus.NOT_OK) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    SafeOpsPermitTextField(
                        value = response?.observation ?: "",
                        onValueChange = { obs ->
                            onResponseChange((response ?: ChecklistItemResponse(itemIndex, itemText)).copy(observation = obs))
                        },
                        label = "Observation",
                        isRequired = true,
                        placeholder = "Explain the safety issue found..."
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SafeOpsPermitTextField(
                        value = response?.correctiveAction ?: "",
                        onValueChange = { act ->
                            onResponseChange((response ?: ChecklistItemResponse(itemIndex, itemText)).copy(correctiveAction = act))
                        },
                        label = "Corrective Action Required",
                        isRequired = true,
                        placeholder = "Describe mandatory action to resolve..."
                    )
                }
            }
        }
    }
}

// ── 7. SafeOpsDailyChecklistTabStrip (Days 1 to 7 selector) ──────────────
@Composable
fun SafeOpsDailyChecklistTabStrip(
    activeDay: Int,
    onDaySelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = SafeOpsTheme.colors.isDark
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isDark) Color(0xFF000000) else Color(0xFFF1F5F9))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        (1..7).forEach { day ->
            val isSelected = activeDay == day
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) Color(0xFF0878FF) else Color.Transparent)
                    .clickable { onDaySelected(day) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Day $day",
                    fontSize = 11.5.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color.White else (if (isDark) Color(0xFFA6A3AA) else Color(0xFF64748B))
                )
            }
        }
    }
}

// ── 8. SafeOpsSubPermitSelector ──────────────────────────────────────────
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SafeOpsSubPermitSelector(
    selectedSubPermits: Set<SubPermitAttachment>,
    onSubPermitToggled: (SubPermitAttachment) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = SafeOpsTheme.colors.isDark
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Sub-Permit Attachments Required",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D)
        )
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            SubPermitAttachment.values().forEach { attachment ->
                val isSelected = selectedSubPermits.contains(attachment)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isSelected) Color(0xFF0878FF).copy(alpha = 0.15f)
                            else (if (isDark) Color(0xFF18181B) else Color(0xFFFFFDFF))
                        )
                        .border(
                            1.dp,
                            if (isSelected) Color(0xFF0878FF) else (if (isDark) Color(0xFF333338) else Color(0xFFE4E2E9)),
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { onSubPermitToggled(attachment) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "${if (isSelected) "✓ " else ""}${attachment.label}",
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color(0xFF0878FF) else (if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D))
                    )
                }
            }
        }
    }
}

// ── 9. SafeOpsSignatureField (Touch Canvas Signature Capture) ─────────────
@Composable
fun SafeOpsSignatureField(
    signerLabel: String,
    signatureData: SignatureData?,
    onSignatureCaptured: (SignatureData) -> Unit,
    isRequired: Boolean = false,
    modifier: Modifier = Modifier
) {
    var showSignatureDialog by remember { mutableStateOf(false) }
    val isDark = SafeOpsTheme.colors.isDark
    val isSigned = signatureData?.isSigned == true

    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = signerLabel,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D)
            )
            if (isRequired) {
                Text(text = " *", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isDark) Color(0xFF18181B) else Color(0xFFFFFDFF))
                .border(
                    1.dp,
                    if (isSigned) Color(0xFF10B981) else (if (isDark) Color(0xFF333338) else Color(0xFFE4E2E9)),
                    RoundedCornerShape(12.dp)
                )
                .clickable { showSignatureDialog = true }
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isSigned) signatureData?.signerName.orEmpty() else "Tap to sign",
                        fontSize = 14.sp,
                        fontWeight = if (isSigned) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSigned) (if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D)) else (if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E))
                    )
                    if (isSigned) {
                        Text(
                            text = "Signed Digitally ✓",
                            fontSize = 11.sp,
                            color = if (isDark) Color(0xFF34D399) else Color(0xFF16A34A),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Icon(
                    imageVector = SafeOpsIcons.Check,
                    contentDescription = null,
                    tint = if (isSigned) (if (isDark) Color(0xFF34D399) else Color(0xFF16A34A)) else (if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E)),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }

    if (showSignatureDialog) {
        Dialog(onDismissRequest = { showSignatureDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (isDark) Color(0xFF18181B) else Color(0xFFFFFDFF),
                modifier = Modifier.padding(12.dp)
            ) {
                var signerNameInput by remember { mutableStateOf(signatureData?.signerName.orEmpty()) }
                val currentPath = remember { mutableStateListOf<Pair<Float, Float>>() }

                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Digital Signature Capture", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D))
                    Spacer(modifier = Modifier.height(12.dp))
                    SafeOpsPermitTextField(
                        value = signerNameInput,
                        onValueChange = { signerNameInput = it },
                        label = "Signer Full Name",
                        isRequired = true,
                        placeholder = "Enter full name"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Draw Signature Below:", fontSize = 12.sp, color = if (isDark) Color(0xFFA6A3AA) else Color(0xFF64748B))
                    Spacer(modifier = Modifier.height(6.dp))

                    // Touch Drawing Canvas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isDark) Color(0xFF000000) else Color(0xFFF8FAFC))
                            .border(1.dp, Color(0xFF0878FF).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .pointerInput(Unit) {
                                detectDragGestures { change, _ ->
                                    currentPath.add(change.position.x to change.position.y)
                                }
                            }
                    ) {
                        Canvas(modifier = Modifier.fillMaxWidth().height(140.dp)) {
                            if (currentPath.size > 1) {
                                val p = Path()
                                p.moveTo(currentPath.first().first, currentPath.first().second)
                                for (i in 1 until currentPath.size) {
                                    p.lineTo(currentPath[i].first, currentPath[i].second)
                                }
                                drawPath(
                                    path = p,
                                    color = Color(0xFF0878FF),
                                    style = Stroke(width = 4f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(onClick = { currentPath.clear() }) { Text("Clear") }
                        Row {
                            TextButton(onClick = { showSignatureDialog = false }) { Text("Cancel") }
                            Spacer(modifier = Modifier.width(6.dp))
                            Button(
                                onClick = {
                                    if (signerNameInput.isNotBlank()) {
                                        onSignatureCaptured(
                                            SignatureData(
                                                signerName = signerNameInput,
                                                role = signerLabel,
                                                signaturePath = currentPath.toList()
                                            )
                                        )
                                        showSignatureDialog = false
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0878FF))
                            ) { Text("Confirm", color = Color.White) }
                        }
                    }
                }
            }
        }
    }
}

// ── 10. SafeOpsSectionCard Wrapper ───────────────────────────────────────
@Composable
fun SafeOpsSectionCard(
    title: String,
    subtitle: String? = null,
    isCompleted: Boolean = false,
    icon: ImageVector = SafeOpsIcons.Document,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val isDark = SafeOpsTheme.colors.isDark
    val bg = if (isDark) Color(0xFF18181B) else Color(0xFFFFFDFF)
    val borderCol = if (isDark) Color(0xFF333338) else Color(0xFFE4E2E9)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderCol)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0878FF).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF0878FF), modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D)
                        )
                        if (!subtitle.isNullOrBlank()) {
                            Text(
                                text = subtitle,
                                fontSize = 11.5.sp,
                                color = if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (isCompleted) (if (isDark) Color(0xFF34D399).copy(alpha = 0.15f) else Color(0xFF16A34A).copy(alpha = 0.15f))
                            else (if (isDark) Color(0xFFFBBF24).copy(alpha = 0.15f) else Color(0xFFF59E0B).copy(alpha = 0.15f))
                        )
                        .padding(horizontal = 9.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isCompleted) "Completed ✓" else "Pending",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCompleted) (if (isDark) Color(0xFF34D399) else Color(0xFF16A34A))
                                else (if (isDark) Color(0xFFFBBF24) else Color(0xFFF59E0B)),
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            content()
        }
    }
}

// ── 11. Validation Summary Banner ─────────────────────────────────────────
@Composable
fun SafeOpsValidationBanner(
    errors: List<String>,
    modifier: Modifier = Modifier
) {
    if (errors.isEmpty()) return
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFFEF2F2))
            .border(1.dp, Color(0xFFFCA5A5), RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = SafeOpsIcons.Check, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Please complete ${errors.size} required field${if (errors.size > 1) "s" else ""}:",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF991B1B)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            errors.forEach { err ->
                Text(text = "• $err", fontSize = 12.sp, color = Color(0xFFB91C1C), modifier = Modifier.padding(start = 8.dp, top = 2.dp))
            }
        }
    }
}

// ── 12. Form Progress Wizard (Steps 1 to 6) ──────────────────────────────
@Composable
fun SafeOpsFormProgress(
    currentStep: Int,
    totalSteps: Int = 6,
    modifier: Modifier = Modifier
) {
    val stepTitles = listOf("Application", "HSE Req", "Contractor", "Final Review", "Approval", "Completion")
    val isDark = SafeOpsTheme.colors.isDark
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Step $currentStep of $totalSteps — ${stepTitles.getOrNull(currentStep - 1).orEmpty()}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0878FF)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            (1..totalSteps).forEach { step ->
                val isDone = step <= currentStep
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(if (isDone) Color(0xFF0878FF) else (if (isDark) Color(0xFF333338) else Color(0xFFE2E8F0)))
                )
            }
        }
    }
}

// ── 13. Sticky Action Bar ────────────────────────────────────────────────
@Composable
fun SafeOpsStickyActionBar(
    onSaveDraft: () -> Unit,
    onNext: () -> Unit,
    onBack: (() -> Unit)? = null,
    isFinalStep: Boolean = false,
    autoSaveStatus: String = "Saved just now",
    modifier: Modifier = Modifier
) {
    val isDark = SafeOpsTheme.colors.isDark
    Surface(
        shadowElevation = 8.dp,
        color = if (isDark) Color(0xFF18181B) else Color(0xFFFFFDFF),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFF333338) else Color(0xFFE4E2E9)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
            Text(
                text = "Draft status: $autoSaveStatus",
                fontSize = 11.sp,
                color = if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (onBack != null) {
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFF333338) else Color(0xFFE4E2E9)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D)
                        )
                    ) { Text("Back", fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold) }
                }

                OutlinedButton(
                    onClick = onSaveDraft,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFF333338) else Color(0xFFE4E2E9)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D)
                    )
                ) { Text("Save Draft", fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold) }

                Button(
                    onClick = onNext,
                    modifier = Modifier.weight(1.2f).height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0878FF))
                ) {
                    Text(
                        text = if (isFinalStep) "Submit Permit" else "Next ›",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
