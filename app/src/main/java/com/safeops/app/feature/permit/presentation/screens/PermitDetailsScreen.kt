package com.safeops.app.feature.permit.presentation.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.safeops.app.core.designsystem.icon.SafeOpsIcons
import com.safeops.app.core.designsystem.theme.AccentOrange
import com.safeops.app.core.designsystem.theme.SafeOpsTheme
import com.safeops.app.feature.permit.domain.model.PermitFormModel
import com.safeops.app.feature.permit.presentation.components.SafeOpsSectionCard

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.ui.geometry.Offset
import com.safeops.app.feature.dashboards.presentation.SafeOpsHeroPattern

@Composable
fun PermitDetailsScreen(
    permit: PermitFormModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = SafeOpsTheme.colors.isDark
    val scrollState = rememberScrollState()

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
                // ── Top Bar with back button + Permit Title ──
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = SafeOpsIcons.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Permit ${permit.header.permitNumber}",
                            style = SafeOpsTheme.typography.screenTitle.copy(
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(permit.status.colorHex).copy(alpha = 0.20f))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                    Text(
                        text = permit.status.displayName,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(permit.status.colorHex)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Upward Curved Details Card Container ──
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 18.dp, vertical = 18.dp),
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
                                        text = permit.permitType.code,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF0878FF)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = permit.permitType.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Permit No: ${permit.header.permitNumber.ifBlank { "N/A" }} \u2022 Springfield Safety Ops",
                                fontSize = 11.5.sp,
                                color = if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E)
                            )
                        }
                    }

                    // Header Info Card
                    SafeOpsSectionCard(
                        title = "Application Overview",
                        isCompleted = true,
                        icon = SafeOpsIcons.Document
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            DetailRow("Permit Code:", permit.permitType.code)
                            DetailRow("Contractor:", permit.header.workPerformedBy.ifBlank { "N/A" })
                            DetailRow("Location:", permit.header.location.ifBlank { "N/A" })
                            DetailRow("Work Date:", permit.schedule.workDate.ifBlank { "N/A" })
                            DetailRow("Schedule:", "${permit.schedule.startTime} – ${permit.schedule.endTime}")
                            DetailRow("Workers:", "${permit.schedule.workerCount} personnel")
                            DetailRow("Site Engineer:", "${permit.personnel.siteEngineerName} (${permit.personnel.siteEngineerContact})")
                        }
                    }

                    // Description Card
                    SafeOpsSectionCard(
                        title = "Scope of Work",
                        isCompleted = true,
                        icon = SafeOpsIcons.Document
                    ) {
                        Text(
                            text = permit.descriptionOfWork.ifBlank { "No description entered." },
                            fontSize = 13.5.sp,
                            color = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D)
                        )
                    }

                    // Status Legend Card
                    SafeOpsSectionCard(
                        title = "Status Legend",
                        isCompleted = true,
                        icon = SafeOpsIcons.Check
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Text("OK — Good", fontSize = 12.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                            Text("NOT OK — Issue", fontSize = 12.sp, color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                            Text("N/A — Not Applicable", fontSize = 12.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
}

@Composable
private fun DetailRow(label: String, value: String) {
    val isDark = SafeOpsTheme.colors.isDark
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.5.sp, color = if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E))
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D))
    }
}
