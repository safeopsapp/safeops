package com.safeops.app.feature.permit.presentation.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.safeops.app.core.designsystem.icon.SafeOpsIcons
import com.safeops.app.core.designsystem.theme.AccentOrange
import com.safeops.app.core.designsystem.theme.RoyalBluePrimary
import com.safeops.app.core.designsystem.theme.SafeOpsTheme
import com.safeops.app.core.designsystem.theme.SuccessGreen
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.ui.geometry.Offset
import com.safeops.app.feature.dashboards.presentation.SafeOpsHeroPattern
import com.safeops.app.feature.permit.domain.model.PermitType

@Composable
fun CreatePermitScreen(
    onPermitTypeSelected: (PermitType) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = SafeOpsTheme.colors.isDark

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
                        text = "Create Permit",
                        style = SafeOpsTheme.typography.screenTitle.copy(
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                // ── Step Progress Indicator ──
                PermitStepIndicator(
                    currentStep = 1,
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
                                listOf(Color(0xFFE4E2E9), Color(0xFFE4E2E9))
                            ),
                            RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp, vertical = 20.dp)
                ) {
                    Text(
                        text = "Select Permit Type",
                        style = SafeOpsTheme.typography.screenTitle.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D)
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Select an official safety permit type to initialize the PTW application.",
                        style = SafeOpsTheme.typography.body.copy(
                            fontSize = 13.sp,
                            color = if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E)
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Context Banner matching Image 3 "Joining: Demo Organization"
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isDark) Color(0xFF1E293B)
                                else Color(0xFFE3EFFF)
                            )
                            .border(
                                1.dp,
                                if (isDark) Color(0xFF333338)
                                else Color(0xFFBFDBFE),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = SafeOpsIcons.Business,
                                contentDescription = null,
                                tint = Color(0xFF0878FF),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Project: Springfield Safety Ops • Zone B",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF0878FF)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Grid/List of 8 Official Permits
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(PermitType.values()) { type ->
                            PermitTypeSelectionCard(
                                type = type,
                                isDark = isDark,
                                onSelect = { onPermitTypeSelected(type) }
                            )
                        }
                    }
                }
            }
        }
    }
}
}

@Composable
fun PermitStepIndicator(currentStep: Int, modifier: Modifier = Modifier) {
    val steps = listOf(
        1 to "Select Type",
        2 to "Permit Details",
        3 to "Submit & Sign"
    )
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Top
    ) {
        steps.forEachIndexed { index, (stepNum, label) ->
            val isCompleted = currentStep > stepNum
            val isCurrent = currentStep == stepNum

            val circleBg by animateColorAsState(
                targetValue = when {
                    isCompleted -> SuccessGreen
                    isCurrent -> Color.White
                    else -> Color(0xFF0F2B57)
                },
                animationSpec = tween(300),
                label = "permit_circle_bg"
            )
            val numberColor = when {
                isCompleted -> Color.White
                isCurrent -> RoyalBluePrimary
                else -> Color.White.copy(alpha = 0.90f)
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(84.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(circleBg)
                        .border(
                            1.5.dp,
                            if (isCurrent) Color.White else Color.White.copy(alpha = 0.40f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(
                            SafeOpsIcons.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    } else {
                        Text(
                            text = stepNum.toString(),
                            style = SafeOpsTheme.typography.bodyMedium.copy(
                                color = numberColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = label,
                    style = SafeOpsTheme.typography.caption.copy(
                        color = if (isCurrent) Color.White else Color.White.copy(alpha = 0.85f),
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 11.sp
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }

            // Connector Line between circle edges
            if (index < steps.size - 1) {
                val isLinePassed = currentStep > stepNum
                val lineColor by animateColorAsState(
                    targetValue = if (isLinePassed) SuccessGreen else Color.White.copy(alpha = 0.40f),
                    animationSpec = tween(300),
                    label = "permit_line_color"
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 15.dp)
                        .height(3.dp)
                        .background(lineColor, RoundedCornerShape(1.5.dp))
                )
            }
        }
    }
}

@Composable
private fun PermitTypeSelectionCard(
    type: PermitType,
    isDark: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple()
            ) { onSelect() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color(0xFF18181B) else Color(0xFFFFFDFF)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDark) Color(0xFF333338) else Color(0xFFE4E2E9)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0878FF).copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = SafeOpsIcons.Document,
                    contentDescription = null,
                    tint = Color(0xFF0878FF),
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF0878FF).copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = type.code,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0878FF)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = type.title,
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = type.description,
                    fontSize = 12.sp,
                    color = if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E),
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = SafeOpsIcons.ArrowForward,
                contentDescription = null,
                tint = Color(0xFF0878FF),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
