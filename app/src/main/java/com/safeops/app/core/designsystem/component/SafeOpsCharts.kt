package com.safeops.app.core.designsystem.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.safeops.app.core.designsystem.theme.SafeOpsTheme
import kotlin.math.atan2

/**
 * Data Model for Donut / Pie Chart Slices.
 */
data class SafeOpsChartSlice(
    val label: String,
    val value: Float,
    val color: Color
)

/**
 * Data Model for Bar Chart items.
 */
data class SafeOpsBarData(
    val label: String,
    val value: Float,
    val color: Color = Color(0xFF0878FF),
    val secondaryValue: Float? = null
)

/**
 * Universal Donut / Pie Chart for Safety Analytics and Permit Status Insights.
 * Features:
 * - Animated slice sweep angles
 * - Center total summary counter
 * - Interactive tap detection to highlight slices
 * - Responsive color-coded legend with percentages
 */
@Composable
fun SafeOpsDonutChart(
    slices: List<SafeOpsChartSlice>,
    title: String,
    modifier: Modifier = Modifier,
    centerTitle: String = "Total",
    subtitle: String? = null,
    strokeWidth: Float = 36f
) {
    val isDark = SafeOpsTheme.colors.isDark
    val totalValue = slices.sumOf { it.value.toDouble() }.toFloat().coerceAtLeast(1f)
    var selectedSliceIndex by remember { mutableStateOf<Int?>(null) }

    var animationTrigger by remember { mutableStateOf(false) }
    androidx.compose.runtime.LaunchedEffect(Unit) { animationTrigger = true }

    val animationProgress by animateFloatAsState(
        targetValue = if (animationTrigger) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "donut_animation"
    )

    val cardBg = if (isDark) Color(0xFF18181B) else Color(0xFFFFFDFF)
    val borderColor = if (isDark) Color(0xFF333338) else Color(0xFFE4E2E9)
    val textColor = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D)
    val subTextColor = if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.5.sp,
                    color = subTextColor
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Donut Chart Canvas & Legend Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // Donut Canvas with Center Hole Counter
                Box(
                    modifier = Modifier.size(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(
                        modifier = Modifier
                            .size(150.dp)
                            .pointerInput(slices) {
                                detectTapGestures { tapOffset ->
                                    val center = Offset(size.width / 2f, size.height / 2f)
                                    val angleRad = atan2(tapOffset.y - center.y, tapOffset.x - center.x)
                                    var angleDeg = Math.toDegrees(angleRad.toDouble()).toFloat()
                                    if (angleDeg < 0) angleDeg += 360f
                                    val rotatedAngle = (angleDeg + 90f) % 360f

                                    var cumulative = 0f
                                    slices.forEachIndexed { i, s ->
                                        val sweep = (s.value / totalValue) * 360f
                                        if (rotatedAngle in cumulative..(cumulative + sweep)) {
                                            selectedSliceIndex = if (selectedSliceIndex == i) null else i
                                            return@detectTapGestures
                                        }
                                        cumulative += sweep
                                    }
                                }
                            }
                    ) {
                        var startAngle = -90f
                        val diameter = size.minDimension - strokeWidth
                        val topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)
                        val arcSize = Size(diameter, diameter)

                        slices.forEachIndexed { index, slice ->
                            val sweepAngle = (slice.value / totalValue) * 360f * animationProgress
                            val isSelected = selectedSliceIndex == index
                            val effectiveStroke = if (isSelected) strokeWidth * 1.35f else strokeWidth

                            drawArc(
                                color = slice.color,
                                startAngle = startAngle,
                                sweepAngle = sweepAngle.coerceAtLeast(1.5f),
                                useCenter = false,
                                topLeft = topLeft,
                                size = arcSize,
                                style = Stroke(width = effectiveStroke, cap = StrokeCap.Round)
                            )
                            startAngle += sweepAngle
                        }
                    }

                    // Center Metrics inside Donut Hole
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        val activeSlice = selectedSliceIndex?.let { slices.getOrNull(it) }
                        Text(
                            text = if (activeSlice != null) activeSlice.value.toInt().toString() else totalValue.toInt().toString(),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = textColor
                        )
                        Text(
                            text = activeSlice?.label ?: centerTitle,
                            fontSize = 10.5.sp,
                            color = subTextColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Legend List with Percentages
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    slices.forEachIndexed { index, slice ->
                        val isSelected = selectedSliceIndex == index
                        val percent = ((slice.value / totalValue) * 100).toInt()

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .clickable {
                                    selectedSliceIndex = if (selectedSliceIndex == index) null else index
                                }
                                .background(if (isSelected) slice.color.copy(alpha = 0.12f) else Color.Transparent)
                                .padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(slice.color, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = slice.label,
                                    fontSize = 11.5.sp,
                                    color = textColor,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                            Text(
                                text = "$percent%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = subTextColor
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Universal Vertical Bar Chart for Trends, Audits, and Inspections.
 * Features:
 * - Animated bar growth
 * - Horizontal target / threshold line
 * - X-axis category labels
 * - Highlighting of maximum value
 */
@Composable
fun SafeOpsBarChart(
    bars: List<SafeOpsBarData>,
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    targetLineValue: Float? = null,
    targetLineLabel: String = "Target"
) {
    val isDark = SafeOpsTheme.colors.isDark
    val maxValue = (bars.maxOfOrNull { it.value } ?: 100f).coerceAtLeast(10f)

    var animationTrigger by remember { mutableStateOf(false) }
    androidx.compose.runtime.LaunchedEffect(Unit) { animationTrigger = true }

    val animationProgress by animateFloatAsState(
        targetValue = if (animationTrigger) 1f else 0f,
        animationSpec = tween(durationMillis = 900),
        label = "bar_growth"
    )

    val cardBg = if (isDark) Color(0xFF18181B) else Color(0xFFFFFDFF)
    val borderColor = if (isDark) Color(0xFF333338) else Color(0xFFE4E2E9)
    val textColor = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D)
    val subTextColor = if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E)
    val gridLineColor = if (isDark) Color(0xFF27272A) else Color(0xFFF1F5F9)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    if (subtitle != null) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = subtitle,
                            fontSize = 11.5.sp,
                            color = subTextColor
                        )
                    }
                }
                if (targetLineValue != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color(0xFFF58220), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$targetLineLabel: ${targetLineValue.toInt()}",
                            fontSize = 11.sp,
                            color = subTextColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Bar Chart Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val w = size.width
                    val h = size.height
                    val bottomPadding = 24.dp.toPx()
                    val chartHeight = h - bottomPadding
                    val barCount = bars.size
                    val spacing = w / (barCount + 1)
                    val barWidth = (spacing * 0.55f).coerceAtMost(28.dp.toPx())

                    // Horizontal Grid Lines
                    val steps = 3
                    for (i in 0..steps) {
                        val y = chartHeight * (i.toFloat() / steps)
                        drawLine(
                            color = gridLineColor,
                            start = Offset(0f, y),
                            end = Offset(w, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // Target Line if supplied
                    if (targetLineValue != null) {
                        val targetRatio = (targetLineValue / maxValue).coerceIn(0f, 1f)
                        val targetY = chartHeight * (1f - targetRatio)
                        drawLine(
                            color = Color(0xFFF58220).copy(alpha = 0.8f),
                            start = Offset(0f, targetY),
                            end = Offset(w, targetY),
                            strokeWidth = 1.5.dp.toPx()
                        )
                    }

                    // Draw Bars
                    bars.forEachIndexed { i, bar ->
                        val centerX = (i + 1) * spacing
                        val barRatio = ((bar.value / maxValue) * animationProgress).coerceIn(0f, 1f)
                        val barActualHeight = chartHeight * barRatio
                        val barTop = chartHeight - barActualHeight
                        val barLeft = centerX - (barWidth / 2f)

                        // Bar Rect
                        drawRoundRect(
                            color = bar.color,
                            topLeft = Offset(barLeft, barTop),
                            size = Size(barWidth, barActualHeight),
                            cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                        )
                    }
                }

                // X-Axis Labels positioned under bars
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    bars.forEach { bar ->
                        Text(
                            text = bar.label,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = subTextColor,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

/**
 * Universal KPI Stat Card for Operational Dashboards and Reports.
 */
@Composable
fun SafeOpsKpiCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    trendPercent: String? = null,
    isPositiveTrend: Boolean = true,
    containerColor: Color = Color(0xFF0878FF)
) {
    val isDark = SafeOpsTheme.colors.isDark
    val cardBg = if (isDark) Color(0xFF18181B) else Color(0xFFFFFDFF)
    val borderColor = if (isDark) Color(0xFF333338) else Color(0xFFE4E2E9)
    val textColor = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D)
    val subTextColor = if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E)

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Colored Icon Container
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(containerColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = containerColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Trend Badge
                if (trendPercent != null) {
                    val trendColor = if (isPositiveTrend) Color(0xFF16A34A) else Color(0xFFEF4444)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(trendColor.copy(alpha = 0.12f))
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${if (isPositiveTrend) "↑" else "↓"} $trendPercent",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = trendColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = textColor
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = title,
                fontSize = 12.sp,
                color = subTextColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (subtitle != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontSize = 10.5.sp,
                    color = subTextColor.copy(alpha = 0.8f)
                )
            }
        }
    }
}
