package com.safeops.app.feature.dashboards.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.safeops.app.domain.model.AuthenticatedSession
import com.safeops.app.domain.usecase.LogoutUseCase

@Composable
fun AdminDashboardShell(
    session: AuthenticatedSession,
    currentRoute: String,
    onNavigateTo: (String) -> Unit,
    logoutUseCase: LogoutUseCase,
    onLoggedOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    SafeOpsDashboardScaffold(
        session = session,
        currentDestination = currentRoute,
        onNavigateTo = onNavigateTo,
        logoutUseCase = logoutUseCase,
        onLoggedOut = onLoggedOut,
        modifier = modifier
    )
}

@Composable
fun EngineerDashboardShell(
    session: AuthenticatedSession,
    currentRoute: String,
    onNavigateTo: (String) -> Unit,
    logoutUseCase: LogoutUseCase,
    onLoggedOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    SafeOpsDashboardScaffold(
        session = session,
        currentDestination = currentRoute,
        onNavigateTo = onNavigateTo,
        logoutUseCase = logoutUseCase,
        onLoggedOut = onLoggedOut,
        modifier = modifier
    )
}

@Composable
fun WorkerDashboardShell(
    session: AuthenticatedSession,
    currentRoute: String,
    onNavigateTo: (String) -> Unit,
    logoutUseCase: LogoutUseCase,
    onLoggedOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    SafeOpsDashboardScaffold(
        session = session,
        currentDestination = currentRoute,
        onNavigateTo = onNavigateTo,
        logoutUseCase = logoutUseCase,
        onLoggedOut = onLoggedOut,
        modifier = modifier
    )
}

@Composable
fun SafetyOfficerDashboardShell(
    session: AuthenticatedSession,
    currentRoute: String,
    onNavigateTo: (String) -> Unit,
    logoutUseCase: LogoutUseCase,
    onLoggedOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    SafeOpsDashboardScaffold(
        session = session,
        currentDestination = currentRoute,
        onNavigateTo = onNavigateTo,
        logoutUseCase = logoutUseCase,
        onLoggedOut = onLoggedOut,
        modifier = modifier
    )
}
