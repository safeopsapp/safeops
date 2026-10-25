package com.safeops.app.core.designsystem.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.safeops.app.core.designsystem.theme.SafeOpsTheme

/**
 * Universal SafeOps Segmented Control with smooth sliding pill background indicator.
 * Ideal for period selectors (Day/Week/Month) and status filters.
 */
@Composable
fun SafeOpsSegmentedControl(
    options: List<String> = emptyList(),
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
    items: List<String> = options,
    onItemSelected: (Int) -> Unit = onOptionSelected
) {
    val effectiveOptions = if (items.isNotEmpty()) items else options
    val effectiveCallback = if (onItemSelected !== onOptionSelected && onItemSelected != {}) onItemSelected else onOptionSelected
    if (effectiveOptions.isEmpty()) return

    val isDark = SafeOpsTheme.colors.isDark
    val containerBg = if (isDark) Color(0xFF18181B) else Color(0xFFF1F5F9)
    val pillBg = if (isDark) Color(0xFF0878FF) else Color(0xFF0878FF)
    val borderColor = if (isDark) Color(0xFF333338) else Color(0xFFE4E2E9)
    val selectedTextColor = Color.White
    val unselectedTextColor = if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(42.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(containerBg)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(3.dp)
    ) {
        val segmentWidth = maxWidth / effectiveOptions.size
        val indicatorOffset by animateDpAsState(
            targetValue = segmentWidth * selectedIndex,
            animationSpec = tween(durationMillis = 250),
            label = "pill_slide"
        )

        // Sliding Active Pill Indicator
        Box(
            modifier = Modifier
                .offset(x = indicatorOffset)
                .width(segmentWidth)
                .fillMaxHeight()
                .clip(RoundedCornerShape(9.dp))
                .background(pillBg)
        )

        // Options Text Row
        Row(modifier = Modifier.fillMaxSize()) {
            effectiveOptions.forEachIndexed { index, option ->
                val isSelected = index == selectedIndex
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(9.dp))
                        .clickable { effectiveCallback(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = option,
                        fontSize = 12.5.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) selectedTextColor else unselectedTextColor,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
