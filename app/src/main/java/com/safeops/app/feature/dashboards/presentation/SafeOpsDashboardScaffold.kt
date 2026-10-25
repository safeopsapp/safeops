package com.safeops.app.feature.dashboards.presentation

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ripple
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.safeops.app.core.designsystem.component.SafeOpsDialog
import com.safeops.app.core.designsystem.icon.SafeOpsIcons
import com.safeops.app.core.designsystem.theme.SafeOpsTheme
import com.safeops.app.core.navigation.Screen
import com.safeops.app.domain.model.AuthenticatedSession
import com.safeops.app.domain.model.Role
import com.safeops.app.domain.usecase.LogoutUseCase
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafeOpsDashboardScaffold(
    session: AuthenticatedSession,
    currentDestination: String,
    onNavigateTo: (String) -> Unit,
    logoutUseCase: LogoutUseCase,
    onLoggedOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Development Role State for Preview Switching
    var activeRole by remember(session.role) { mutableStateOf(session.role) }

    // Bottom Sheet States
    var showProjectSheet by remember { mutableStateOf(false) }
    var showRoleSwitchSheet by remember { mutableStateOf(false) }
    var placeholderTitle by remember { mutableStateOf<String?>(null) }
    var placeholderMessage by remember { mutableStateOf<String?>(null) }

    val bottomSheetState = rememberModalBottomSheetState()
    val isDark = SafeOpsTheme.colors.isDark

    val userProfile = when (activeRole) {
        Role.ADMIN -> DashboardMockData.adminProfile
        Role.ENGINEER -> DashboardMockData.engineerProfile
        Role.SAFETY_OFFICER -> DashboardMockData.ehsProfile
        Role.WORKER -> DashboardMockData.workerProfile
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

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
                        "home" -> { /* Stay on current dashboard */ }
                        "permits" -> onNavigateTo(Screen.Permits.route)
                        "projects" -> { showProjectSheet = true }
                        "notifications" -> {
                            placeholderTitle = "Notifications"
                            placeholderMessage = "You have 3 unread safety alerts and permit status updates."
                        }
                        else -> onNavigateTo(route)
                    }
                },
                onLogoutClick = {
                    coroutineScope.launch {
                        drawerState.close()
                        logoutUseCase()
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
        Scaffold(
            modifier = modifier.fillMaxSize(),
            bottomBar = {
                SafeOpsDashboardBottomNavigation(
                    currentRoute = currentDestination,
                    role = activeRole,
                    onNavigate = { route ->
                        when (route) {
                            "home" -> { /* Stay on current dashboard */ }
                            "projects" -> { showProjectSheet = true }
                            "permits" -> { onNavigateTo(Screen.Permits.route) }
                            "profile" -> { showRoleSwitchSheet = true }
                            else -> onNavigateTo(route)
                        }
                    },
                    onCreateClick = {
                        onNavigateTo(Screen.Permits.route)
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
                Column(modifier = Modifier.fillMaxSize()) {
                    // 1. Shared Blue Hero Section
                    SafeOpsDashboardHero(
                        userProfile = userProfile,
                        isDark = isDark,
                        onMenuClick = {
                            coroutineScope.launch { drawerState.open() }
                        },
                        onNotificationClick = {
                            placeholderTitle = "Notifications"
                            placeholderMessage = "You have 3 unread safety alerts and permit status updates."
                        },
                        onProfileClick = { showRoleSwitchSheet = true },
                        onProjectContextClick = { showProjectSheet = true }
                    )

                // 2. Main Dashboard Content Container with upward curved divider matching LoginScreen
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .offset(y = (-24).dp)
                        .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                        .background(if (isDark) Color(0xFF000000) else Color(0xFFF5F4FA))
                        .border(
                            1.dp,
                            if (isDark) Brush.verticalGradient(
                                listOf(Color(0xFF38BDF8).copy(alpha = 0.6f), Color(0xFF333338).copy(alpha = 0.3f))
                            ) else Brush.verticalGradient(
                                listOf(Color(0xFFE4E2E9), Color(0xFFE4E2E9))
                            ),
                            RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                        )
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Floating Context Card inside the curved section
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .padding(top = 16.dp)
                        ) {
                            when (activeRole) {
                                Role.ADMIN -> DashboardContextCard(
                                    title = "Current Project",
                                    primaryText = "Chennai Metro Site",
                                    secondaryText = "12 Active Permits • Zone B",
                                    icon = SafeOpsIcons.Business,
                                    isDark = isDark,
                                    onClick = { showProjectSheet = true }
                                )
                                Role.ENGINEER -> DashboardContextCard(
                                    title = "Current Location",
                                    primaryText = "Building A • Level 3",
                                    secondaryText = "Chennai Metro Project • Zone B",
                                    icon = SafeOpsIcons.Location,
                                    isDark = isDark,
                                    onClick = { showProjectSheet = true }
                                )
                                Role.SAFETY_OFFICER -> DashboardContextCard(
                                    title = "Current Site",
                                    primaryText = "Building A • Level 3",
                                    secondaryText = "26 Sep 2026, Saturday",
                                    icon = SafeOpsIcons.Location,
                                    isDark = isDark,
                                    onClick = { showProjectSheet = true }
                                )
                                Role.WORKER -> DashboardContextCard(
                                    title = "Assigned Work Site",
                                    primaryText = "Building A • Level 3",
                                    secondaryText = "Shift: 09:00 AM – 06:00 PM",
                                    icon = SafeOpsIcons.Worker,
                                    isDark = isDark,
                                    onClick = { showProjectSheet = true }
                                )
                            }
                        }

                        // 3. Role-Aware Scrollable Dashboard Content
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            val handleRoleNavigation: (String) -> Unit = { route ->
                                when (route) {
                                    "create_permit", "permits", "request_permit", "my_permits", "view_permits",
                                    "hot_work", "lifting", "confined_space", "excavation", "height", "piling", "night", "demolition",
                                    "general_work", "electrical", "chemical", "radiation" -> {
                                        onNavigateTo(Screen.Permits.route)
                                    }
                                    "hazards", "report_hazard", "view_hazards" -> {
                                        onNavigateTo(Screen.Hazards.route)
                                    }
                                    "incidents", "report_incident", "view_incidents" -> {
                                        onNavigateTo(Screen.Incidents.route)
                                    }
                                    "inspections", "safety_walk", "start_inspection", "conduct_inspection" -> {
                                        onNavigateTo(Screen.Inspections.route)
                                    }
                                    "projects" -> {
                                        showProjectSheet = true
                                    }
                                    "profile" -> {
                                        showRoleSwitchSheet = true
                                    }
                                    else -> {
                                        onNavigateTo(Screen.Permits.route)
                                    }
                                }
                            }

                            when (activeRole) {
                                Role.ADMIN -> AdminDashboardContent(
                                    isDark = isDark,
                                    onNavigateAction = handleRoleNavigation
                                )
                                Role.ENGINEER -> EngineerDashboardContent(
                                    isDark = isDark,
                                    onNavigateAction = handleRoleNavigation
                                )
                                Role.SAFETY_OFFICER -> EhsDashboardContent(
                                    isDark = isDark,
                                    onNavigateAction = handleRoleNavigation
                                )
                                Role.WORKER -> WorkerDashboardContent(
                                    isDark = isDark,
                                    onNavigateAction = handleRoleNavigation
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

    // ── Project Selection Bottom Sheet ───────────────────────────────────────
    if (showProjectSheet) {
        ModalBottomSheet(
            onDismissRequest = { showProjectSheet = false },
            sheetState = bottomSheetState,
            containerColor = if (isDark) Color(0xFF18181B) else Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .padding(bottom = 24.dp)
            ) {
                Text(
                    text = "Select Active Project / Site",
                    style = SafeOpsTheme.typography.screenTitle.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D)
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))

                val projects = listOf(
                    "Chennai Metro Project" to "Zone B • Building A - Level 3",
                    "Residential Tower 2" to "Exterior Scaffolding Zone",
                    "Commercial Block B" to "Mechanical Utility Yard"
                )

                projects.forEachIndexed { index, proj ->
                    val isSelected = index == 0
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isSelected) Color(0xFF0878FF).copy(alpha = 0.12f)
                                else if (isDark) Color(0xFF1E293B)
                                else Color(0xFFF8FAFC)
                            )
                            .border(
                                1.dp,
                                if (isSelected) Color(0xFF0878FF)
                                else if (isDark) Color(0xFF333338)
                                else Color(0xFFEDF2F7),
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { showProjectSheet = false }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = proj.first,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D)
                            )
                            Text(
                                text = proj.second,
                                fontSize = 12.sp,
                                color = if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E)
                            )
                        }

                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF0878FF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = SafeOpsIcons.CheckCircle,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }

    // ── Development Role Switcher Sheet ──────────────────────────────────────
    if (showRoleSwitchSheet) {
        ModalBottomSheet(
            onDismissRequest = { showRoleSwitchSheet = false },
            sheetState = bottomSheetState,
            containerColor = if (isDark) Color(0xFF18181B) else Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .padding(bottom = 28.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Switch Role Preview",
                        style = SafeOpsTheme.typography.screenTitle.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D)
                        )
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE6F7ED))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "DEV PREVIEW",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF27AE60)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Select a role below to preview its specific operational dashboard UI.",
                    fontSize = 12.sp,
                    color = if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Role.entries.forEach { role ->
                    val isSelected = activeRole == role
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isSelected) Color(0xFF0878FF).copy(alpha = 0.15f)
                                else if (isDark) Color(0xFF1E293B)
                                else Color(0xFFF8FAFC)
                            )
                            .border(
                                1.dp,
                                if (isSelected) Color(0xFF0878FF)
                                else if (isDark) Color(0xFF333338)
                                else Color(0xFFEDF2F7),
                                RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                activeRole = role
                                showRoleSwitchSheet = false
                            }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (role) {
                                            Role.ADMIN -> Color(0xFFE74C3C).copy(alpha = 0.15f)
                                            Role.ENGINEER -> Color(0xFFE67E22).copy(alpha = 0.15f)
                                            Role.SAFETY_OFFICER -> Color(0xFF0878FF).copy(alpha = 0.15f)
                                            Role.WORKER -> Color(0xFF27AE60).copy(alpha = 0.15f)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (role) {
                                        Role.ADMIN -> SafeOpsIcons.Admin
                                        Role.ENGINEER -> SafeOpsIcons.Engineer
                                        Role.SAFETY_OFFICER -> SafeOpsIcons.SafetyOfficer
                                        Role.WORKER -> SafeOpsIcons.Worker
                                    },
                                    contentDescription = null,
                                    tint = when (role) {
                                        Role.ADMIN -> Color(0xFFE74C3C)
                                        Role.ENGINEER -> Color(0xFFE67E22)
                                        Role.SAFETY_OFFICER -> Color(0xFF0878FF)
                                        Role.WORKER -> Color(0xFF27AE60)
                                    },
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = role.displayName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color.White else Color(0xFF0F172A)
                                )
                                Text(
                                    text = when (role) {
                                        Role.ADMIN -> "Governance & Operations Overview"
                                        Role.ENGINEER -> "Permits & Field Action Hub"
                                        Role.SAFETY_OFFICER -> "Permit Approvals & Site Checklists"
                                        Role.WORKER -> "Field Work Assignments & Safety"
                                    },
                                    fontSize = 11.5.sp,
                                    color = if (isDark) Color(0xFF8899B0) else Color(0xFF64748B)
                                )
                            }
                        }

                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF0878FF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = SafeOpsIcons.CheckCircle,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Sign Out Option
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFE74C3C).copy(alpha = 0.10f))
                        .border(1.dp, Color(0xFFE74C3C).copy(alpha = 0.30f), RoundedCornerShape(14.dp))
                        .clickable {
                            showRoleSwitchSheet = false
                            coroutineScope.launch {
                                logoutUseCase()
                                onLoggedOut()
                            }
                        }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = SafeOpsIcons.Logout,
                        contentDescription = null,
                        tint = Color(0xFFE74C3C),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Sign Out of SafeOps",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE74C3C)
                    )
                }
            }
        }
    }

    // ── Placeholder Dialog ───────────────────────────────────────────────────
    if (placeholderTitle != null) {
        SafeOpsDialog(
            title = placeholderTitle ?: "",
            message = placeholderMessage ?: "",
            confirmButtonText = "OK",
            onConfirmClick = {
                placeholderTitle = null
                placeholderMessage = null
            },
            onDismissRequest = {
                placeholderTitle = null
                placeholderMessage = null
            }
        )
    }
}
