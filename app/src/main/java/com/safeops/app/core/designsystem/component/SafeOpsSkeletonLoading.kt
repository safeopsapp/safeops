package com.safeops.app.core.designsystem.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.safeops.app.core.designsystem.theme.SafeOpsTheme

/**
 * Enterprise shimmer brush modifier for SafeOps.
 * Creates an ultra-smooth, high-frequency diagonal shimmer wave matching brand colors.
 */
fun Modifier.shimmerEffect(): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "safeops_shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1600f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translation"
    )

    val isDark = isSystemInDarkTheme()
    val shimmerColors = if (isDark) {
        listOf(
            Color(0xFF102744),
            Color(0xFF1B3B60),
            Color(0xFF244875),
            Color(0xFF1B3B60),
            Color(0xFF102744)
        )
    } else {
        listOf(
            Color(0xFFE2E8F0),
            Color(0xFFEDF2F7),
            Color(0xFFF8FAFC),
            Color(0xFFEDF2F7),
            Color(0xFFE2E8F0)
        )
    }

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(x = translateAnim - 800f, y = translateAnim - 800f),
        end = Offset(x = translateAnim, y = translateAnim)
    )

    this.background(brush)
}

/**
 * Standard Atomic Skeleton Box.
 */
@Composable
fun SafeOpsSkeletonBox(
    modifier: Modifier = Modifier,
    width: Dp? = null,
    height: Dp,
    shape: Shape = RoundedCornerShape(8.dp)
) {
    val baseModifier = if (width != null) modifier.width(width) else modifier.fillMaxWidth()
    Box(
        modifier = baseModifier
            .height(height)
            .clip(shape)
            .shimmerEffect()
    )
}

/**
 * Metric Card Skeleton (KPI Card).
 */
@Composable
fun SafeOpsCardSkeleton(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SafeOpsTheme.colors.surface)
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SafeOpsSkeletonBox(width = 110.dp, height = 14.dp, shape = RoundedCornerShape(4.dp))
                SafeOpsSkeletonBox(width = 36.dp, height = 36.dp, shape = CircleShape)
            }
            Spacer(modifier = Modifier.height(16.dp))
            SafeOpsSkeletonBox(width = 80.dp, height = 28.dp, shape = RoundedCornerShape(6.dp))
            Spacer(modifier = Modifier.height(8.dp))
            SafeOpsSkeletonBox(width = 140.dp, height = 12.dp, shape = RoundedCornerShape(4.dp))
        }
    }
}

/**
 * List Item Skeleton (Permit / Inspection / Hazard row).
 */
@Composable
fun SafeOpsListItemSkeleton(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SafeOpsTheme.colors.surface)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SafeOpsSkeletonBox(width = 44.dp, height = 44.dp, shape = RoundedCornerShape(10.dp))
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SafeOpsSkeletonBox(width = 120.dp, height = 16.dp, shape = RoundedCornerShape(4.dp))
                    SafeOpsSkeletonBox(width = 60.dp, height = 20.dp, shape = RoundedCornerShape(10.dp))
                }
                Spacer(modifier = Modifier.height(8.dp))
                SafeOpsSkeletonBox(width = 180.dp, height = 12.dp, shape = RoundedCornerShape(4.dp))
            }
        }
    }
}

/**
 * Full Screen Dashboard Skeleton.
 */
@Composable
fun SafeOpsDashboardSkeleton(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SafeOpsTheme.colors.background)
            .padding(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                SafeOpsSkeletonBox(width = 140.dp, height = 20.dp, shape = RoundedCornerShape(4.dp))
                Spacer(modifier = Modifier.height(6.dp))
                SafeOpsSkeletonBox(width = 90.dp, height = 12.dp, shape = RoundedCornerShape(4.dp))
            }
            SafeOpsSkeletonBox(width = 40.dp, height = 40.dp, shape = CircleShape)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 2x2 Metric Cards Grid
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SafeOpsCardSkeleton(modifier = Modifier.weight(1f))
            SafeOpsCardSkeleton(modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SafeOpsCardSkeleton(modifier = Modifier.weight(1f))
            SafeOpsCardSkeleton(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Activity Section Header
        SafeOpsSkeletonBox(width = 160.dp, height = 16.dp, shape = RoundedCornerShape(4.dp))
        Spacer(modifier = Modifier.height(12.dp))

        // Activity Items
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            repeat(4) {
                SafeOpsListItemSkeleton()
            }
        }
    }
}

/**
 * Data Table Skeleton for Construction Safety Logs & Permits.
 */
@Composable
fun SafeOpsTableSkeleton(
    rows: Int = 6,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SafeOpsTheme.colors.surface)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SafeOpsSkeletonBox(width = 60.dp, height = 14.dp, shape = RoundedCornerShape(4.dp))
            SafeOpsSkeletonBox(width = 100.dp, height = 14.dp, shape = RoundedCornerShape(4.dp))
            SafeOpsSkeletonBox(width = 70.dp, height = 14.dp, shape = RoundedCornerShape(4.dp))
            SafeOpsSkeletonBox(width = 50.dp, height = 14.dp, shape = RoundedCornerShape(4.dp))
        }

        // Divider Shimmer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(SafeOpsTheme.colors.border)
        )

        // Rows
        repeat(rows) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SafeOpsSkeletonBox(width = 50.dp, height = 12.dp, shape = RoundedCornerShape(4.dp))
                SafeOpsSkeletonBox(width = 120.dp, height = 12.dp, shape = RoundedCornerShape(4.dp))
                SafeOpsSkeletonBox(width = 60.dp, height = 12.dp, shape = RoundedCornerShape(4.dp))
                SafeOpsSkeletonBox(width = 45.dp, height = 18.dp, shape = RoundedCornerShape(9.dp))
            }
        }
    }
}
