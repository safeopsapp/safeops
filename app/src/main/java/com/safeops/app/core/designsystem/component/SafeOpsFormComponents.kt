package com.safeops.app.core.designsystem.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.safeops.app.core.designsystem.icon.SafeOpsIcons
import com.safeops.app.core.designsystem.theme.SafeOpsTheme

/**
 * Universal Form Section Container Card with optional badge, subtitle,
 * and smooth collapsible chevron transition.
 */
@Composable
fun SafeOpsFormSection(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    badgeText: String? = null,
    badgeColor: Color = Color(0xFF0878FF),
    icon: ImageVector? = null,
    isCollapsible: Boolean = false,
    defaultExpanded: Boolean = true,
    content: @Composable () -> Unit
) {
    val isDark = SafeOpsTheme.colors.isDark
    var expanded by remember { mutableStateOf(defaultExpanded) }
    val chevronRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(300),
        label = "section_chevron"
    )

    val cardBg = if (isDark) Color(0xFF18181B) else Color(0xFFFFFDFF)
    val borderColor = if (isDark) Color(0xFF333338) else Color(0xFFE4E2E9)
    val titleColor = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D)
    val subtitleColor = if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = isCollapsible) { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (icon != null) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = Color(0xFF0878FF),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            text = title,
                            fontSize = 15.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = titleColor
                        )
                        if (badgeText != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(badgeColor.copy(alpha = 0.15f))
                                    .padding(horizontal = 7.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = badgeText,
                                    color = badgeColor,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    if (subtitle != null) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = subtitle,
                            fontSize = 11.5.sp,
                            color = subtitleColor
                        )
                    }
                }

                if (isCollapsible) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(if (isDark) Color(0xFF27272A) else Color(0xFFF1F5F9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = SafeOpsIcons.DropdownArrow,
                            contentDescription = if (expanded) "Collapse" else "Expand",
                            tint = subtitleColor,
                            modifier = Modifier
                                .size(18.dp)
                                .rotate(chevronRotation)
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(tween(300)) + fadeIn(tween(300)),
                exit = shrinkVertically(tween(250)) + fadeOut(tween(250))
            ) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    content()
                }
            }
        }
    }
}

/**
 * Universal Multi-line Text Area with Character Counter.
 */
@Composable
fun SafeOpsTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "Enter details...",
    minLines: Int = 3,
    maxCharacters: Int = 500,
    maxChars: Int = 500,
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    val effectiveMaxChars = if (maxChars != 500) maxChars else maxCharacters
    val isDark = SafeOpsTheme.colors.isDark
    val containerBg = if (isDark) Color(0xFF18181B) else Color(0xFFFFFDFF)
    val borderColor = when {
        isError || errorMessage != null -> Color(0xFFEF4444)
        else -> if (isDark) Color(0xFF333338) else Color(0xFFE4E2E9)
    }
    val textColor = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D)
    val secondaryText = if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E)

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )
            Text(
                text = "${value.length} / $effectiveMaxChars",
                fontSize = 10.5.sp,
                color = if (value.length > effectiveMaxChars) Color(0xFFEF4444) else secondaryText
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = (minLines * 24 + 20).dp)
                .clip(RoundedCornerShape(12.dp))
                .background(containerBg)
                .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    color = secondaryText.copy(alpha = 0.6f),
                    fontSize = 13.5.sp
                )
            }
            BasicTextField(
                value = value,
                onValueChange = {
                    if (it.length <= effectiveMaxChars) onValueChange(it)
                },
                enabled = enabled,
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = textColor,
                    fontSize = 13.5.sp,
                    lineHeight = 20.sp
                ),
                cursorBrush = SolidColor(Color(0xFF0878FF)),
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage,
                color = Color(0xFFEF4444),
                fontSize = 11.sp
            )
        }
    }
}

/**
 * Universal Number Stepper / Counter Field with minus and plus controls.
 */
@Composable
fun SafeOpsNumberStepper(
    value: Int,
    onValueChange: (Int) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    minValue: Int = 0,
    maxValue: Int = 999,
    min: Int = 0,
    max: Int = 999,
    step: Int = 1,
    unit: String? = null
) {
    val effectiveMin = if (min != 0) min else minValue
    val effectiveMax = if (max != 999) max else maxValue
    val isDark = SafeOpsTheme.colors.isDark
    val containerBg = if (isDark) Color(0xFF18181B) else Color(0xFFFFFDFF)
    val buttonBg = if (isDark) Color(0xFF27272A) else Color(0xFFF1F5F9)
    val borderColor = if (isDark) Color(0xFF333338) else Color(0xFFE4E2E9)
    val textColor = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D)
    val secondaryText = if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(containerBg)
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )
            if (unit != null) {
                Text(
                    text = "Unit: $unit",
                    fontSize = 11.sp,
                    color = secondaryText
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Decrement Button
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(buttonBg)
                    .clickable(enabled = value > effectiveMin) {
                        onValueChange((value - step).coerceAtLeast(effectiveMin))
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "−",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (value > effectiveMin) Color(0xFF0878FF) else secondaryText.copy(alpha = 0.4f)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = value.toString(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                modifier = Modifier.width(36.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.width(14.dp))

            // Increment Button
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(buttonBg)
                    .clickable(enabled = value < effectiveMax) {
                        onValueChange((value + step).coerceAtMost(effectiveMax))
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (value < effectiveMax) Color(0xFF0878FF) else secondaryText.copy(alpha = 0.4f)
                )
            }
        }
    }
}

/**
 * Universal Date Picker Trigger Field.
 */
@Composable
fun SafeOpsDatePickerField(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    onDateClick: () -> Unit = {},
    placeholder: String = "DD/MM/YYYY",
    onValueChange: ((String) -> Unit)? = null
) {
    val isDark = SafeOpsTheme.colors.isDark
    val containerBg = if (isDark) Color(0xFF18181B) else Color(0xFFFFFDFF)
    val borderColor = if (isDark) Color(0xFF333338) else Color(0xFFE4E2E9)
    val textColor = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D)
    val secondaryText = if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E)

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(containerBg)
                .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                .clickable { onDateClick() }
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = value.ifBlank { placeholder },
                fontSize = 13.5.sp,
                color = if (value.isBlank()) secondaryText.copy(alpha = 0.6f) else textColor
            )
            Icon(
                imageVector = SafeOpsIcons.Date,
                contentDescription = "Select Date",
                tint = Color(0xFF0878FF),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

/**
 * Universal Time Picker Trigger Field.
 */
@Composable
fun SafeOpsTimePickerField(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    onTimeClick: () -> Unit = {},
    placeholder: String = "HH:MM",
    onValueChange: ((String) -> Unit)? = null
) {
    val isDark = SafeOpsTheme.colors.isDark
    val containerBg = if (isDark) Color(0xFF18181B) else Color(0xFFFFFDFF)
    val borderColor = if (isDark) Color(0xFF333338) else Color(0xFFE4E2E9)
    val textColor = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D)
    val secondaryText = if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E)

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(containerBg)
                .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                .clickable { onTimeClick() }
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = value.ifBlank { placeholder },
                fontSize = 13.5.sp,
                color = if (value.isBlank()) secondaryText.copy(alpha = 0.6f) else textColor
            )
            Icon(
                imageVector = SafeOpsIcons.Time,
                contentDescription = "Select Time",
                tint = Color(0xFF0878FF),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

/**
 * Universal Toggle Switch Row.
 */
@Composable
fun SafeOpsToggleRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: ImageVector? = null
) {
    val isDark = SafeOpsTheme.colors.isDark
    val textColor = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D)
    val secondaryText = if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF0878FF),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = textColor
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        fontSize = 11.5.sp,
                        color = secondaryText
                    )
                }
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF0878FF),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = if (isDark) Color(0xFF333338) else Color(0xFFE4E2E9)
            )
        )
    }
}

/**
 * Universal Checkbox Row.
 */
@Composable
fun SafeOpsCheckboxRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null
) {
    val isDark = SafeOpsTheme.colors.isDark
    val textColor = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D)
    val secondaryText = if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = Color(0xFF0878FF),
                uncheckedColor = secondaryText
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 11.5.sp,
                    color = secondaryText
                )
            }
        }
    }
}
