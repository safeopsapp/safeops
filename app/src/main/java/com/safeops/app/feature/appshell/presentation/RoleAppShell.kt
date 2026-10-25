package com.safeops.app.feature.appshell.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.safeops.app.core.designsystem.theme.SafeOpsTheme
import com.safeops.app.core.navigation.Screen
import com.safeops.app.domain.model.AuthenticatedSession
import com.safeops.app.domain.model.Role
import com.safeops.app.domain.usecase.LogoutUseCase
import com.safeops.app.feature.dashboards.presentation.SafeOpsDashboardBottomNavigation

/**
 * Role-Aware Main Application Shell.
 * Renders uniform WhatsApp-style bottom navigation across modules.
 */
@Composable
fun RoleAppShell(
    session: AuthenticatedSession,
    currentDestination: String,
    onNavigateTo: (String) -> Unit,
    logoutUseCase: LogoutUseCase,
    onLoggedOut: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val isDark = SafeOpsTheme.colors.isDark

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            SafeOpsDashboardBottomNavigation(
                currentRoute = currentDestination,
                role = session.role,
                onNavigate = { route ->
                    when (route) {
                        "home" -> {
                            when (session.role) {
                                Role.ADMIN -> onNavigateTo(Screen.AdminDashboard.route)
                                Role.ENGINEER -> onNavigateTo(Screen.EngineerDashboard.route)
                                Role.SAFETY_OFFICER -> onNavigateTo(Screen.SafetyOfficerDashboard.route)
                                Role.WORKER -> onNavigateTo(Screen.WorkerDashboard.route)
                            }
                        }
                        "permits" -> onNavigateTo(Screen.Permits.route)
                        "projects" -> onNavigateTo(Screen.Projects.route)
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
        containerColor = SafeOpsTheme.colors.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            content()
        }
    }
}
