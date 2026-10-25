package com.safeops.app.feature.permit.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.geometry.Offset
import com.safeops.app.feature.dashboards.presentation.SafeOpsHeroPattern
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.safeops.app.domain.model.AuthenticatedSession
import com.safeops.app.feature.permit.data.repository.PermitRepository
import com.safeops.app.feature.permit.domain.model.PermitFormModel
import com.safeops.app.feature.permit.domain.model.PermitType

@Composable
fun PermitListScreen(
    repository: PermitRepository,
    session: AuthenticatedSession,
    onMenuClick: () -> Unit = {},
    onCreatePermitClick: () -> Unit,
    onPermitClick: (PermitFormModel) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = SafeOpsTheme.colors.isDark
    val permits by repository.getPermits().collectAsState(initial = emptyList())
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf<PermitType?>(null) }

    val filteredPermits = permits.filter { permit ->
        val matchesType = selectedFilter == null || permit.permitType == selectedFilter
        val matchesQuery = searchQuery.isBlank() ||
                permit.header.permitNumber.contains(searchQuery, ignoreCase = true) ||
                permit.header.location.contains(searchQuery, ignoreCase = true) ||
                permit.permitType.title.contains(searchQuery, ignoreCase = true)
        matchesType && matchesQuery
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
                // ── Top Brand Header Bar with Hamburger Menu ──
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 3-Line Hamburger Menu Button
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f))
                                .border(1.dp, Color.White.copy(alpha = 0.25f), CircleShape)
                                .clickable { onMenuClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = SafeOpsIcons.Menu,
                                contentDescription = "Open Navigation Menu",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "Safe",
                                    style = SafeOpsTheme.typography.screenTitle.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 22.sp
                                    )
                                )
                                Text(
                                    "Ops",
                                    style = SafeOpsTheme.typography.screenTitle.copy(
                                        color = AccentOrange,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 22.sp
                                    )
                                )
                            }
                            Text(
                                "Safety • Compliance • Operations",
                                style = SafeOpsTheme.typography.caption.copy(
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.20f))
                            .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = session.role.displayName,
                            color = Color.White,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

            // ── Screen Title & Action Header ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Permits to Work (PTW)",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${filteredPermits.size} active permits recorded",
                        fontSize = 12.5.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFFFF8A00), Color(0xFFFF5500))
                            )
                        )
                        .border(1.dp, Color(0xFFFFB366).copy(alpha = 0.80f), RoundedCornerShape(14.dp))
                        .clickable { onCreatePermitClick() }
                        .padding(horizontal = 14.dp, vertical = 9.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = SafeOpsIcons.Add,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "Create Permit",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // ── Upward Curved Form Card Container (Matching Registration Form) ──
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
                        .padding(horizontal = 18.dp, vertical = 18.dp)
                ) {
                    // Search Field
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                "Search by permit #, location, or type...",
                                fontSize = 13.5.sp,
                                color = if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = SafeOpsIcons.Search,
                                contentDescription = null,
                                tint = Color(0xFF0878FF),
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFE3EFFF),
                            unfocusedContainerColor = if (isDark) Color(0xFF000000) else Color(0xFFF5F4FA),
                            focusedBorderColor = Color(0xFF0878FF),
                            unfocusedBorderColor = if (isDark) Color(0xFF333338) else Color(0xFFE4E2E9),
                            focusedTextColor = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D),
                            unfocusedTextColor = if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E)
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Filter Chips Bar (All + 8 official permits)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            val isAllSelected = selectedFilter == null
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(
                                        if (isAllSelected) Color(0xFF0878FF)
                                        else (if (isDark) Color(0xFF000000) else Color(0xFFF5F4FA))
                                    )
                                    .border(
                                        1.dp,
                                        if (isAllSelected) Color(0xFF0878FF)
                                        else (if (isDark) Color(0xFF333338) else Color(0xFFE4E2E9)),
                                        RoundedCornerShape(20.dp)
                                    )
                                    .clickable { selectedFilter = null }
                                    .padding(horizontal = 14.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    text = "All Permits",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isAllSelected) Color.White else (if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D))
                                )
                            }
                        }

                        items(PermitType.values()) { type ->
                            val isSelected = selectedFilter == type
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(
                                        if (isSelected) Color(0xFF0878FF)
                                        else (if (isDark) Color(0xFF000000) else Color(0xFFF5F4FA))
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) Color(0xFF0878FF)
                                        else (if (isDark) Color(0xFF333338) else Color(0xFFE4E2E9)),
                                        RoundedCornerShape(20.dp)
                                    )
                                    .clickable { selectedFilter = type }
                                    .padding(horizontal = 14.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    text = type.title,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else (if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E))
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Permit Cards List
                    if (filteredPermits.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = SafeOpsIcons.Document,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "No permits found matching criteria",
                                    fontSize = 14.sp,
                                    color = if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E)
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            items(filteredPermits, key = { it.id }) { permit ->
                                PermitListItemCard(
                                    permit = permit,
                                    isDark = isDark,
                                    onClick = { onPermitClick(permit) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
}

@Composable
private fun PermitListItemCard(
    permit: PermitFormModel,
    isDark: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF18181B) else Color(0xFFFFFDFF)),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFF333338) else Color(0xFFE4E2E9))
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
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF0878FF).copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = permit.permitType.code,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0878FF),
                            maxLines = 1
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = permit.header.permitNumber.ifBlank { "DRAFT PERMIT" },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D),
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Status Pill (Single-line, non-wrapping badge)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(permit.status.colorHex).copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = permit.status.displayName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(permit.status.colorHex),
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = permit.permitType.title,
                fontSize = 15.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D)
            )

            if (permit.header.location.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = SafeOpsIcons.Location,
                        contentDescription = null,
                        tint = if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E),
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = permit.header.location,
                        fontSize = 12.5.sp,
                        color = if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Date: ${permit.schedule.workDate.ifBlank { "27 Sep 2026" }}",
                    fontSize = 12.sp,
                    color = if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E)
                )
                Text(
                    text = "Created by: ${permit.createdByName.ifBlank { "Marcus Vance" }}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF0878FF)
                )
            }
        }
    }
}
