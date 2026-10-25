package com.safeops.app.feature.modules.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.safeops.app.core.designsystem.component.SafeOpsAlertBanner
import com.safeops.app.core.designsystem.component.SafeOpsAlertType
import com.safeops.app.core.designsystem.component.SafeOpsEmptyState
import com.safeops.app.core.designsystem.component.SafeOpsOutlinedButton
import com.safeops.app.core.designsystem.component.SafeOpsPrimaryButton
import com.safeops.app.core.designsystem.component.SafeOpsSectionHeader
import com.safeops.app.core.designsystem.icon.SafeOpsIcons
import com.safeops.app.core.designsystem.theme.SafeOpsTheme
import com.safeops.app.domain.model.AuthenticatedSession
import com.safeops.app.domain.model.Role
import com.safeops.app.feature.dashboards.presentation.DashboardMockData
import com.safeops.app.feature.dashboards.presentation.SafeOpsDashboardBottomNavigation
import com.safeops.app.feature.dashboards.presentation.SafeOpsSidebarDrawer
import com.safeops.app.feature.permit.data.repository.PermitRepositoryImpl
import com.safeops.app.feature.permit.domain.model.PermitFormModel
import com.safeops.app.feature.permit.domain.model.PermitType
import com.safeops.app.feature.permit.presentation.screens.CreatePermitScreen
import com.safeops.app.feature.permit.presentation.screens.PermitDetailsScreen
import com.safeops.app.feature.permit.presentation.screens.PermitFormScreen
import com.safeops.app.feature.permit.presentation.screens.PermitListScreen
import kotlinx.coroutines.launch

@Composable
fun FeatureModuleShell(
    title: String,
    description: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null,
    content: (@Composable () -> Unit)? = null
) {
    val scrollState = rememberScrollState()
    val isDark = SafeOpsTheme.colors.isDark
    val typography = SafeOpsTheme.typography

    // Lighter, vibrant blue gradient mixed with light and dark blue tones
    val headerGradient = if (isDark) {
        Brush.verticalGradient(
            0.00f to Color(0xFF2979FF),
            0.30f to Color(0xFF1E88E5),
            0.65f to Color(0xFF1565C0),
            1.00f to Color(0xFF0D47A1)
        )
    } else {
        Brush.verticalGradient(
            0.00f to Color(0xFF38BDF8),
            0.25f to Color(0xFF2979FF),
            0.60f to Color(0xFF1E88E5),
            1.00f to Color(0xFF1565C0)
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (isDark) Color(0xFF000000) else Color(0xFFF5F4FA))
    ) {
        // Pure Gradient Header Background (Mixed light/dark blue, NO patterns)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
                .background(headerGradient)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .background(
                        Brush.verticalGradient(
                            0.0f to Color(0xFFE0F2FE).copy(alpha = if (isDark) 0.18f else 0.30f),
                            0.5f to Color(0xFF38BDF8).copy(alpha = if (isDark) 0.08f else 0.15f),
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
            // Header Top Bar with SafeOps wordmark & back/icon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onBackClick != null) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.18f))
                    ) {
                        Icon(
                            imageVector = SafeOpsIcons.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                } else {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "SAFE",
                            style = typography.caption.copy(
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp,
                                fontSize = 10.5.sp
                            )
                        )
                        Text(
                            text = "OPS",
                            style = typography.caption.copy(
                                color = Color(0xFFF58220),
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp,
                                fontSize = 10.5.sp
                            )
                        )
                        Text(
                            text = " • OPERATIONS",
                            style = typography.caption.copy(
                                color = Color.White.copy(alpha = 0.75f),
                                letterSpacing = 0.5.sp,
                                fontSize = 9.5.sp
                            )
                        )
                    }
                    Text(
                        text = title,
                        style = typography.screenTitle.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = description,
                        style = typography.caption.copy(
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 11.5.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Upward Curved Surface Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(if (isDark) Color(0xFF18181B) else Color(0xFFFFFDFF))
                    .border(
                        1.dp,
                        if (isDark) Color(0xFF333338) else Color(0xFFE4E2E9),
                        RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                    )
            ) {
                if (content != null) {
                    content()
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        SafeOpsEmptyState(
                            title = "$title Foundation",
                            description = description,
                            icon = icon
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProjectsShell(
    session: AuthenticatedSession,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isDark = SafeOpsTheme.colors.isDark
    val scrollState = rememberScrollState()

    FeatureModuleShell(
        title = "Site & Project Management",
        description = "Manage active construction sites, zones, floor plans, and supervisor allocations.",
        icon = SafeOpsIcons.Projects,
        onBackClick = onBackClick,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 18.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stat KPI Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProjectStatPill(
                    title = "Active Sites",
                    value = "3",
                    icon = SafeOpsIcons.Business,
                    modifier = Modifier.weight(1f),
                    isDark = isDark
                )
                ProjectStatPill(
                    title = "Work Zones",
                    value = "18",
                    icon = SafeOpsIcons.Location,
                    modifier = Modifier.weight(1f),
                    isDark = isDark
                )
                ProjectStatPill(
                    title = "Supervisors",
                    value = "12",
                    icon = SafeOpsIcons.Profile,
                    modifier = Modifier.weight(1f),
                    isDark = isDark
                )
            }

            // Project Site Cards Header
            Text(
                text = "Active Construction Sites",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D)
            )

            ProjectSiteCard(
                siteCode = "SITE-BLR-01",
                siteName = "Apex Metro Line 3 — Pier Corridor",
                location = "Elevated Viaduct Span 14 to 28",
                activeWorkers = 44,
                activePermits = 5,
                leadSupervisor = "Er. Ramesh Kumar",
                status = "ACTIVE",
                isDark = isDark
            )

            ProjectSiteCard(
                siteCode = "SITE-BLR-02",
                siteName = "Elevated Expressway — Package 4",
                location = "North Junction Interchange",
                activeWorkers = 28,
                activePermits = 3,
                leadSupervisor = "Er. Priya M.",
                status = "ACTIVE",
                isDark = isDark
            )

            ProjectSiteCard(
                siteCode = "SITE-BLR-03",
                siteName = "Commercial Hub — Tower Zenith",
                location = "Refinery Process Unit 9",
                activeWorkers = 16,
                activePermits = 2,
                leadSupervisor = "Er. Anand S.",
                status = "INSPECTION",
                isDark = isDark
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Action Controls
            SafeOpsPrimaryButton(
                text = "Register New Site / Work Zone",
                icon = SafeOpsIcons.Add,
                onClick = {}
            )
            SafeOpsOutlinedButton(
                text = "Export CAD Floor Map & Hazard Overlay",
                onClick = {}
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ProjectStatPill(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    isDark: Boolean
) {
    val bg = if (isDark) Color(0xFF18181B) else Color(0xFFFFFDFF)
    val borderCol = if (isDark) Color(0xFF333338) else Color(0xFFE4E2E9)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .border(1.dp, borderCol, RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 11.5.sp,
                    color = if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E)
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF0878FF),
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D)
            )
        }
    }
}

@Composable
private fun ProjectSiteCard(
    siteCode: String,
    siteName: String,
    location: String,
    activeWorkers: Int,
    activePermits: Int,
    leadSupervisor: String,
    status: String,
    isDark: Boolean
) {
    val bg = if (isDark) Color(0xFF18181B) else Color(0xFFFFFDFF)
    val borderCol = if (isDark) Color(0xFF333338) else Color(0xFFE4E2E9)
    val statusColor = if (status == "ACTIVE") {
        if (isDark) Color(0xFF34D399) else Color(0xFF16A34A)
    } else {
        if (isDark) Color(0xFFFBBF24) else Color(0xFFF59E0B)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .border(1.dp, borderCol, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0878FF).copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = SafeOpsIcons.Business,
                            contentDescription = null,
                            tint = Color(0xFF0878FF),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = siteCode,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0878FF)
                        )
                        Text(
                            text = siteName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D)
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = status,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = statusColor
                    )
                }
            }

            Text(
                text = location,
                fontSize = 12.sp,
                color = if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "👷 $activeWorkers Workers • 📑 $activePermits Permits",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDark) Color(0xFFE8E6EC) else Color(0xFF334155)
                )
                Text(
                    text = leadSupervisor,
                    fontSize = 11.5.sp,
                    color = if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E)
                )
            }
        }
    }
}

/**
 * Full Mobile Permit Form Module Shell rendering the 8 official Springfield/SafeOps permits.
 * Displays constant WhatsApp-style bottom navigation on PermitListScreen,
 * and hides it cleanly when filling out multi-step forms or creating permits.
 */
@Composable
fun PermitsShell(
    session: AuthenticatedSession,
    onNavigateTo: (String) -> Unit = {},
    onLoggedOut: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val repository = remember { PermitRepositoryImpl() }
    var currentScreen by remember { mutableStateOf<PermitScreenState>(PermitScreenState.List) }
    var activeRole by remember(session.role) { mutableStateOf(session.role) }
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val isDark = SafeOpsTheme.colors.isDark

    val userProfile = when (activeRole) {
        Role.ADMIN -> DashboardMockData.adminProfile
        Role.ENGINEER -> DashboardMockData.engineerProfile
        Role.SAFETY_OFFICER -> DashboardMockData.ehsProfile
        Role.WORKER -> DashboardMockData.workerProfile
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SafeOpsSidebarDrawer(
                userProfile = userProfile,
                activeRole = activeRole,
                onRoleSelected = { role ->
                    activeRole = role
                    coroutineScope.launch { drawerState.close() }
                },
                onNavigate = { route ->
                    coroutineScope.launch { drawerState.close() }
                    when (route) {
                        "home" -> onNavigateTo("home")
                        "permits" -> { currentScreen = PermitScreenState.List }
                        else -> onNavigateTo(route)
                    }
                },
                onLogoutClick = {
                    coroutineScope.launch {
                        drawerState.close()
                        onLoggedOut()
                    }
                },
                onClose = {
                    coroutineScope.launch { drawerState.close() }
                },
                isDark = isDark
            )
        }
    ) {
        when (val state = currentScreen) {
            is PermitScreenState.List -> {
                Scaffold(
                    modifier = modifier.fillMaxSize(),
                    bottomBar = {
                        SafeOpsDashboardBottomNavigation(
                            currentRoute = "permits",
                            role = activeRole,
                            onNavigate = { route ->
                                when (route) {
                                    "permits" -> { /* Already on permits list */ }
                                    else -> onNavigateTo(route)
                                }
                            },
                            onCreateClick = {
                                currentScreen = PermitScreenState.SelectType
                            },
                            isDark = isDark,
                            modifier = Modifier
                        )
                    },
                    containerColor = if (isDark) Color(0xFF000000) else Color(0xFFF5F4FA)
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = innerPadding.calculateBottomPadding())
                    ) {
                        PermitListScreen(
                            repository = repository,
                            session = session,
                            onMenuClick = {
                                coroutineScope.launch { drawerState.open() }
                            },
                            onCreatePermitClick = { currentScreen = PermitScreenState.SelectType },
                            onPermitClick = { permit ->
                                if (permit.status == com.safeops.app.feature.permit.domain.model.PermitStatus.DRAFT) {
                                    currentScreen = PermitScreenState.Form(permit)
                                } else {
                                    currentScreen = PermitScreenState.Details(permit)
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
            is PermitScreenState.SelectType -> {
                CreatePermitScreen(
                    onPermitTypeSelected = { selectedType ->
                        val newPermit = PermitFormModel(
                            permitType = selectedType,
                            header = com.safeops.app.feature.permit.domain.model.PermitHeader(
                                permitNumber = "SPR-2026-${(100..999).random()}",
                                project = "Apex Metro Line - Phase 2"
                            ),
                            createdByUserId = session.user.id,
                            createdByName = session.user.fullName
                        )
                        currentScreen = PermitScreenState.Form(newPermit)
                    },
                    onBackClick = { currentScreen = PermitScreenState.List },
                    modifier = modifier.fillMaxSize()
                )
            }
            is PermitScreenState.Form -> {
                PermitFormScreen(
                    initialPermit = state.permit,
                    repository = repository,
                    session = session,
                    onSubmitted = { currentScreen = PermitScreenState.List },
                    onBackClick = { currentScreen = PermitScreenState.List },
                    modifier = modifier.fillMaxSize()
                )
            }
            is PermitScreenState.Details -> {
                PermitDetailsScreen(
                    permit = state.permit,
                    onBackClick = { currentScreen = PermitScreenState.List },
                    modifier = modifier.fillMaxSize()
                )
            }
        }
    }
}

private sealed interface PermitScreenState {
    object List : PermitScreenState
    object SelectType : PermitScreenState
    data class Form(val permit: PermitFormModel) : PermitScreenState
    data class Details(val permit: PermitFormModel) : PermitScreenState
}

@Composable
fun HazardsShell(
    session: AuthenticatedSession,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    com.safeops.app.feature.hazards.presentation.CreateHazardScreen(
        session = session,
        onBackClick = onBackClick,
        modifier = modifier
    )
}

@Composable
fun IncidentsShell(
    session: AuthenticatedSession,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    com.safeops.app.feature.incidents.presentation.CreateIncidentScreen(
        session = session,
        onBackClick = onBackClick,
        modifier = modifier
    )
}

@Composable
fun InspectionsShell(
    session: AuthenticatedSession,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    com.safeops.app.feature.inspections.presentation.CreateInspectionScreen(
        session = session,
        onBackClick = onBackClick,
        modifier = modifier
    )
}

@Composable
fun NotificationsShell(
    session: AuthenticatedSession,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isDark = SafeOpsTheme.colors.isDark
    val scrollState = rememberScrollState()

    FeatureModuleShell(
        title = "Safety Alerts & Broadcasts",
        description = "Real-time emergency broadcasts, permit status updates, and compliance notifications.",
        icon = SafeOpsIcons.Notifications,
        onBackClick = onBackClick,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 18.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            SafeOpsAlertBanner(
                title = "CRITICAL WEATHER BROADCAST",
                message = "High wind advisory (>45 km/h) at Tower Zenith. All crane lifting and external scaffolding operations suspended until 17:00.",
                type = SafeOpsAlertType.FAILURE
            )

            Text(
                text = "Recent Operational Updates",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D)
            )

            NotificationItemCard(
                title = "Hot Work Permit Approved",
                detail = "Permit SPR-2026-881 for Pier 14 has been approved by EHS Officer S. Shankar.",
                time = "10m ago",
                type = "PERMIT",
                isDark = isDark
            )

            NotificationItemCard(
                title = "High Risk Hazard Reported",
                detail = "HAZ-1092: Missing toe-board on Scaffold Bay 4 reported. Assigned to Site Supervisor.",
                time = "25m ago",
                type = "HAZARD",
                isDark = isDark
            )

            NotificationItemCard(
                title = "Toolbox Talk Verified",
                detail = "42 workers logged and biometric verified for Morning Pre-Start Safety Briefing.",
                time = "2h ago",
                type = "COMPLIANCE",
                isDark = isDark
            )

            NotificationItemCard(
                title = "Daily Site Inspection Scheduled",
                detail = "Electrical substation safety review scheduled for today at 15:30.",
                time = "3h ago",
                type = "INSPECTION",
                isDark = isDark
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun NotificationItemCard(
    title: String,
    detail: String,
    time: String,
    type: String,
    isDark: Boolean
) {
    val bg = if (isDark) Color(0xFF18181B) else Color(0xFFFFFDFF)
    val borderCol = if (isDark) Color(0xFF333338) else Color(0xFFE4E2E9)
    val icon = when (type) {
        "PERMIT" -> SafeOpsIcons.Document
        "HAZARD" -> SafeOpsIcons.Hazards
        "COMPLIANCE" -> SafeOpsIcons.Check
        else -> SafeOpsIcons.Inspections
    }
    val badgeColor = when (type) {
        "PERMIT" -> Color(0xFF0878FF)
        "HAZARD" -> Color(0xFFEF4444)
        "COMPLIANCE" -> if (isDark) Color(0xFF34D399) else Color(0xFF16A34A)
        else -> if (isDark) Color(0xFFFBBF24) else Color(0xFFF59E0B)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .border(1.dp, borderCol, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(badgeColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = badgeColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D)
                    )
                    Text(
                        text = time,
                        fontSize = 11.sp,
                        color = if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = detail,
                    fontSize = 12.sp,
                    color = if (isDark) Color(0xFFA6A3AA) else Color(0xFF64748B)
                )
            }
        }
    }
}

@Composable
fun ProfileShell(session: AuthenticatedSession, modifier: Modifier = Modifier) {
    SafeOpsComponentsGallery(session = session, modifier = modifier)
}
