package com.safeops.app.core.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.safeops.app.R
import com.safeops.app.core.designsystem.theme.AccentOrange
import com.safeops.app.core.designsystem.theme.SafeOpsTheme
import com.safeops.app.di.AppContainer
import com.safeops.app.di.SafeOpsViewModelFactory
import com.safeops.app.domain.model.AuthenticatedSession
import com.safeops.app.domain.model.Role
import com.safeops.app.feature.appshell.presentation.RoleAppShell
import com.safeops.app.feature.authentication.presentation.AdminRegScreen
import com.safeops.app.feature.authentication.presentation.AdminRegViewModel
import com.safeops.app.feature.authentication.presentation.LoginScreen
import com.safeops.app.feature.authentication.presentation.LoginViewModel
import com.safeops.app.feature.authentication.presentation.WorkerRegScreen
import com.safeops.app.feature.authentication.presentation.WorkerRegViewModel
import com.safeops.app.feature.dashboards.presentation.AdminDashboardShell
import com.safeops.app.feature.dashboards.presentation.EngineerDashboardShell
import com.safeops.app.feature.dashboards.presentation.SafetyOfficerDashboardShell
import com.safeops.app.feature.dashboards.presentation.WorkerDashboardShell
import com.safeops.app.feature.modules.presentation.HazardsShell
import com.safeops.app.feature.modules.presentation.IncidentsShell
import com.safeops.app.feature.modules.presentation.InspectionsShell
import com.safeops.app.feature.modules.presentation.NotificationsShell
import com.safeops.app.feature.modules.presentation.PermitsShell
import com.safeops.app.feature.modules.presentation.ProfileShell
import com.safeops.app.feature.modules.presentation.ProjectsShell
import com.safeops.app.feature.splash.presentation.SplashScreen

@Composable
fun SafeOpsNavHost(
    container: AppContainer,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    var isSessionLoaded by remember { mutableStateOf(false) }
    var currentSession by remember { mutableStateOf<AuthenticatedSession?>(null) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Direct session resolution without artificial delay or secondary click gate
    LaunchedEffect(Unit) {
        try {
            val session = container.sessionRepository.getCurrentSession()
            if (session != null) {
                currentSession = session
                val route = container.resolveRoleNavigationUseCase(session)
                navController.navigate(route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }
        } catch (_: Exception) {
            // Unauthenticated: defaults to LoginScreen
        } finally {
            isSessionLoaded = true
        }
    }

    if (!isSessionLoaded) {
        // Native-matching blue gradient launch view on app open
        SafeOpsLaunchScreen()
    } else {
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = modifier
        ) {
        // 1. Splash Route (Direct instant redirect without secondary loading screen)
        composable(Screen.Splash.route) {
            LaunchedEffect(Unit) {
                try {
                    val session = container.sessionRepository.getCurrentSession()
                    if (session != null) {
                        currentSession = session
                        val route = container.resolveRoleNavigationUseCase(session)
                        navController.navigate(route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                } catch (_: Exception) {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            }
        }

        // 2. Login Screen
        composable(Screen.Login.route) {
            val loginViewModel: LoginViewModel = viewModel(
                factory = SafeOpsViewModelFactory(container)
            )
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = { session ->
                    currentSession = session
                    val route = container.resolveRoleNavigationUseCase(session)
                    navController.navigate(route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToAdminRegister = {
                    navController.navigate(Screen.AdminRegister.route)
                },
                onNavigateToWorkerRegister = {
                    navController.navigate(Screen.WorkerRegister.route)
                }
            )
        }

        // 3. Admin Registration (4-step wizard)
        composable(Screen.AdminRegister.route) {
            val adminRegViewModel: AdminRegViewModel = viewModel(
                factory = SafeOpsViewModelFactory(container)
            )
            AdminRegScreen(
                viewModel = adminRegViewModel,
                onNavigateBack = { navController.popBackStack() },
                onRegistrationComplete = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.AdminRegister.route) { inclusive = true }
                    }
                }
            )
        }

        // 4. Worker Registration (referral code flow)
        composable(Screen.WorkerRegister.route) {
            val workerRegViewModel: WorkerRegViewModel = viewModel(
                factory = SafeOpsViewModelFactory(container)
            )
            WorkerRegScreen(
                viewModel = workerRegViewModel,
                onNavigateBack = { navController.popBackStack() },
                onRegistrationComplete = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.WorkerRegister.route) { inclusive = true }
                    }
                }
            )
        }

        // 3. Admin Dashboard
        composable(Screen.AdminDashboard.route) {
            val defaultOrg = com.safeops.app.domain.model.Organization("ORG-01", "Sairam Construction Pvt Ltd", "SCPL")
            val session = currentSession ?: AuthenticatedSession(
                user = com.safeops.app.domain.model.User("ADM-001", "vasanthan@safeops.com", "Vasanthan M", com.safeops.app.domain.model.Role.ADMIN, organizationId = "ORG-01"),
                organization = defaultOrg,
                role = com.safeops.app.domain.model.Role.ADMIN,
                currentProject = null,
                permissions = emptySet(),
                accessToken = "mock_token",
                expiresAt = System.currentTimeMillis() + 86400000
            )
            AdminDashboardShell(
                session = session,
                currentRoute = Screen.AdminDashboard.route,
                onNavigateTo = { target -> navController.navigate(target) },
                logoutUseCase = container.logoutUseCase,
                onLoggedOut = {
                    currentSession = null
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // 4. Engineer Dashboard
        composable(Screen.EngineerDashboard.route) {
            val defaultOrg = com.safeops.app.domain.model.Organization("ORG-01", "Sairam Construction Pvt Ltd", "SCPL")
            val session = currentSession ?: AuthenticatedSession(
                user = com.safeops.app.domain.model.User("ENG-104", "vasanthan.eng@safeops.com", "Vasanthan M", com.safeops.app.domain.model.Role.ENGINEER, organizationId = "ORG-01"),
                organization = defaultOrg,
                role = com.safeops.app.domain.model.Role.ENGINEER,
                currentProject = null,
                permissions = emptySet(),
                accessToken = "mock_token",
                expiresAt = System.currentTimeMillis() + 86400000
            )
            EngineerDashboardShell(
                session = session,
                currentRoute = Screen.EngineerDashboard.route,
                onNavigateTo = { target -> navController.navigate(target) },
                logoutUseCase = container.logoutUseCase,
                onLoggedOut = {
                    currentSession = null
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // 5. Worker Dashboard
        composable(Screen.WorkerDashboard.route) {
            val defaultOrg = com.safeops.app.domain.model.Organization("ORG-01", "Sairam Construction Pvt Ltd", "SCPL")
            val session = currentSession ?: AuthenticatedSession(
                user = com.safeops.app.domain.model.User("WRK-512", "ramesh@safeops.com", "Ramesh Kumar", com.safeops.app.domain.model.Role.WORKER, organizationId = "ORG-01"),
                organization = defaultOrg,
                role = com.safeops.app.domain.model.Role.WORKER,
                currentProject = null,
                permissions = emptySet(),
                accessToken = "mock_token",
                expiresAt = System.currentTimeMillis() + 86400000
            )
            WorkerDashboardShell(
                session = session,
                currentRoute = Screen.WorkerDashboard.route,
                onNavigateTo = { target -> navController.navigate(target) },
                logoutUseCase = container.logoutUseCase,
                onLoggedOut = {
                    currentSession = null
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // 6. Safety Officer Dashboard
        composable(Screen.SafetyOfficerDashboard.route) {
            val defaultOrg = com.safeops.app.domain.model.Organization("ORG-01", "Sairam Construction Pvt Ltd", "SCPL")
            val session = currentSession ?: AuthenticatedSession(
                user = com.safeops.app.domain.model.User("EHS-209", "shankar@safeops.com", "S. Shankar", com.safeops.app.domain.model.Role.SAFETY_OFFICER, organizationId = "ORG-01"),
                organization = defaultOrg,
                role = com.safeops.app.domain.model.Role.SAFETY_OFFICER,
                currentProject = null,
                permissions = emptySet(),
                accessToken = "mock_token",
                expiresAt = System.currentTimeMillis() + 86400000
            )
            SafetyOfficerDashboardShell(
                session = session,
                currentRoute = Screen.SafetyOfficerDashboard.route,
                onNavigateTo = { target -> navController.navigate(target) },
                logoutUseCase = container.logoutUseCase,
                onLoggedOut = {
                    currentSession = null
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        val defaultOrg = com.safeops.app.domain.model.Organization("ORG-01", "Sairam Construction Pvt Ltd", "SCPL")
        val fallbackSession = AuthenticatedSession(
            user = com.safeops.app.domain.model.User("ENG-104", "vasanthan.eng@safeops.com", "Vasanthan M", com.safeops.app.domain.model.Role.ENGINEER, organizationId = "ORG-01"),
            organization = defaultOrg,
            role = com.safeops.app.domain.model.Role.ENGINEER,
            currentProject = null,
            permissions = emptySet(),
            accessToken = "mock_token",
            expiresAt = System.currentTimeMillis() + 86400000
        )

        // 7. Projects Shell
        composable(Screen.Projects.route) {
            val session = currentSession ?: fallbackSession
            RoleAppShell(
                session = session,
                currentDestination = Screen.Projects.route,
                onNavigateTo = { target -> navController.navigate(target) },
                logoutUseCase = container.logoutUseCase,
                onLoggedOut = {
                    currentSession = null
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            ) {
                ProjectsShell(session = session)
            }
        }

        // 8. Permits Shell
        composable(Screen.Permits.route) {
            val session = currentSession ?: fallbackSession
            PermitsShell(
                session = session,
                onNavigateTo = { target ->
                    when (target) {
                        "home" -> {
                            when (session.role) {
                                Role.ADMIN -> navController.navigate(Screen.AdminDashboard.route)
                                Role.ENGINEER -> navController.navigate(Screen.EngineerDashboard.route)
                                Role.SAFETY_OFFICER -> navController.navigate(Screen.SafetyOfficerDashboard.route)
                                Role.WORKER -> navController.navigate(Screen.WorkerDashboard.route)
                            }
                        }
                        "projects" -> navController.navigate(Screen.Projects.route)
                        "hazards" -> navController.navigate(Screen.Hazards.route)
                        "inspections" -> navController.navigate(Screen.Inspections.route)
                        else -> navController.navigate(target)
                    }
                },
                onLoggedOut = {
                    currentSession = null
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // 9. Hazards Shell
        composable(Screen.Hazards.route) {
            val session = currentSession ?: fallbackSession
            RoleAppShell(
                session = session,
                currentDestination = Screen.Hazards.route,
                onNavigateTo = { target -> navController.navigate(target) },
                logoutUseCase = container.logoutUseCase,
                onLoggedOut = {
                    currentSession = null
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            ) {
                HazardsShell(
                    session = session,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }

        // 10. Inspections Shell
        composable(Screen.Inspections.route) {
            val session = currentSession ?: fallbackSession
            RoleAppShell(
                session = session,
                currentDestination = Screen.Inspections.route,
                onNavigateTo = { target -> navController.navigate(target) },
                logoutUseCase = container.logoutUseCase,
                onLoggedOut = {
                    currentSession = null
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            ) {
                InspectionsShell(
                    session = session,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }

        // 10b. Incidents Shell
        composable(Screen.Incidents.route) {
            val session = currentSession ?: fallbackSession
            RoleAppShell(
                session = session,
                currentDestination = Screen.Incidents.route,
                onNavigateTo = { target -> navController.navigate(target) },
                logoutUseCase = container.logoutUseCase,
                onLoggedOut = {
                    currentSession = null
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            ) {
                IncidentsShell(
                    session = session,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }

        // 11. Notifications Shell
        composable(Screen.Notifications.route) {
            val session = currentSession ?: fallbackSession
            RoleAppShell(
                session = session,
                currentDestination = Screen.Notifications.route,
                onNavigateTo = { target -> navController.navigate(target) },
                logoutUseCase = container.logoutUseCase,
                onLoggedOut = {
                    currentSession = null
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            ) {
                NotificationsShell(session = session)
            }
        }

        // 12. Profile Shell
        composable(Screen.Profile.route) {
            val session = currentSession ?: fallbackSession
            RoleAppShell(
                session = session,
                currentDestination = Screen.Profile.route,
                onNavigateTo = { target -> navController.navigate(target) },
                logoutUseCase = container.logoutUseCase,
                onLoggedOut = {
                    currentSession = null
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            ) {
                ProfileShell(session = session)
            }
        }
    }
}
}

/**
 * Clean native-matching launch view shown seamlessly during initial session lookup on app open.
 * Eliminates artificial secondary splash delays and click gates.
 */
@Composable
fun SafeOpsLaunchScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF0878FF),
                        Color(0xFF063D9D),
                        Color(0xFF031B45),
                        Color(0xFF010B1E)
                    ),
                    radius = 1600f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(108.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color(0xFF0878FF),
                                Color(0xFF063D9D)
                            )
                        )
                    )
                    .border(
                        1.5.dp,
                        Color(0xFF38BDF8).copy(alpha = 0.7f),
                        RoundedCornerShape(26.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_safeops_logo),
                    contentDescription = "SafeOps App Logo",
                    modifier = Modifier.size(72.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SAFE",
                    style = SafeOpsTheme.typography.display.copy(
                        color = Color.White,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp
                    )
                )
                Text(
                    text = "OPS",
                    style = SafeOpsTheme.typography.display.copy(
                        color = AccentOrange,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Construction Safety & Compliance",
                style = SafeOpsTheme.typography.secondaryMedium.copy(
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    letterSpacing = 0.5.sp
                )
            )
        }
    }
}
