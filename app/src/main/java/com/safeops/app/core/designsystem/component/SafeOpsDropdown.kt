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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.safeops.app.core.designsystem.icon.SafeOpsIcons
import com.safeops.app.core.designsystem.theme.SafeOpsTheme

/**
 * High-tech SafeOps Select / Dropdown Component.
 * Features:
 * - Attached header trigger with 180° animated chevron rotation inside
 * - Seamlessly connected dropdown container with expand/collapse transition
 * - Optional inline search bar for fast filtering
 * - Selected item highlighting with checkmark indicator
 * - Pure glass black dark mode (#18181B) & crisp light mode (#FFFFFF)
 */
@Composable
fun SafeOpsDropdown(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "Select an option",
    leadingIcon: ImageVector? = null,
    isSearchable: Boolean = options.size > 6,
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    var expanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val isDark = SafeOpsTheme.colors.isDark

    // Chevron 180° rotation state
    val chevronRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(durationMillis = 280),
        label = "chevron_rotation"
    )

    val filteredOptions = if (searchQuery.isBlank()) {
        options
    } else {
        options.filter { it.contains(searchQuery, ignoreCase = true) }
    }

    val headerBg = if (isDark) Color(0xFF18181B) else Color(0xFFFFFDFF)
    val containerBg = if (isDark) Color(0xFF18181B) else Color(0xFFFFFDFF)
    val itemHoverBg = if (isDark) Color(0xFF27272A) else Color(0xFFF1F5F9)
    val activeSelectionBg = if (isDark) Color(0xFF0878FF).copy(alpha = 0.15f) else Color(0xFFE3EFFF)
    val activeTextColor = Color(0xFF0878FF)
    val textColor = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D)
    val secondaryText = if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E)
    val borderColor = when {
        isError || errorMessage != null -> Color(0xFFEF4444)
        expanded -> Color(0xFF0878FF)
        else -> if (isDark) Color(0xFF333338) else Color(0xFFE4E2E9)
    }

    // Header shape morphs to attach seamlessly to container when expanded
    val headerShape = if (expanded) {
        RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp, bottomStart = 0.dp, bottomEnd = 0.dp)
    } else {
        RoundedCornerShape(14.dp)
    }

    val containerShape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 14.dp, bottomEnd = 14.dp)

    Column(modifier = modifier.fillMaxWidth()) {
        // Form Label
        Text(
            text = label,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Attached Header Trigger Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(headerShape)
                .background(headerBg)
                .border(1.dp, borderColor, headerShape)
                .clickable(enabled = enabled) {
                    expanded = !expanded
                    if (!expanded) searchQuery = ""
                }
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (leadingIcon != null) {
                        Icon(
                            imageVector = leadingIcon,
                            contentDescription = null,
                            tint = if (selectedOption.isNotBlank()) activeTextColor else secondaryText,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                    Text(
                        text = selectedOption.ifBlank { placeholder },
                        fontSize = 14.sp,
                        fontWeight = if (selectedOption.isNotBlank()) FontWeight.Medium else FontWeight.Normal,
                        color = if (selectedOption.isBlank()) secondaryText.copy(alpha = 0.6f) else textColor
                    )
                }

                // 180° Rotating Chevron Inside Attached Header
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(if (isDark) Color(0xFF27272A) else Color(0xFFF1F5F9)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = SafeOpsIcons.DropdownArrow,
                        contentDescription = if (expanded) "Close Dropdown" else "Open Dropdown",
                        tint = if (expanded) activeTextColor else secondaryText,
                        modifier = Modifier
                            .size(18.dp)
                            .rotate(chevronRotation)
                    )
                }
            }
        }

        // Attached Dropdown Container with Perfect Transition
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(
                animationSpec = tween(durationMillis = 280),
                expandFrom = Alignment.Top
            ) + fadeIn(animationSpec = tween(280)),
            exit = shrinkVertically(
                animationSpec = tween(durationMillis = 220),
                shrinkTowards = Alignment.Top
            ) + fadeOut(animationSpec = tween(220))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(containerShape)
                    .background(containerBg)
                    .border(
                        width = 1.dp,
                        color = borderColor,
                        shape = containerShape
                    )
                    .padding(bottom = 6.dp)
            ) {
                HorizontalDivider(color = if (isDark) Color(0xFF27272A) else Color(0xFFF1F5F9), thickness = 1.dp)

                // Optional Search Bar inside Dropdown
                if (isSearchable) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isDark) Color(0xFF27272A) else Color(0xFFF1F5F9))
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = SafeOpsIcons.Search,
                                contentDescription = null,
                                tint = secondaryText,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                singleLine = true,
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    color = textColor,
                                    fontSize = 13.sp
                                ),
                                cursorBrush = SolidColor(activeTextColor),
                                modifier = Modifier.fillMaxWidth(),
                                decorationBox = { innerTextField ->
                                    if (searchQuery.isEmpty()) {
                                        Text(
                                            text = "Search options...",
                                            fontSize = 13.sp,
                                            color = secondaryText.copy(alpha = 0.6f)
                                        )
                                    }
                                    innerTextField()
                                }
                            )
                        }
                    }
                }

                // Options List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 240.dp)
                ) {
                    if (filteredOptions.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No matching options found",
                                    fontSize = 13.sp,
                                    color = secondaryText
                                )
                            }
                        }
                    } else {
                        items(filteredOptions) { option ->
                            val isSelected = option == selectedOption
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onOptionSelected(option)
                                        expanded = false
                                        searchQuery = ""
                                    }
                                    .background(if (isSelected) activeSelectionBg else Color.Transparent)
                                    .padding(horizontal = 16.dp, vertical = 11.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = option,
                                    fontSize = 13.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) activeTextColor else textColor
                                )
                                if (isSelected) {
                                    Icon(
                                        imageVector = SafeOpsIcons.CheckCircle,
                                        contentDescription = "Selected",
                                        tint = activeTextColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Error message if any
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
