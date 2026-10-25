package com.safeops.app.core.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import com.safeops.app.core.designsystem.component.SafeOpsNavItem
import com.safeops.app.core.designsystem.icon.SafeOpsIcons
import com.safeops.app.domain.model.Role

/**
 * Type-safe centralized Navigation Destinations.
 */
sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object AuthCheck : Screen("auth_check")
    data object Login : Screen("login")
    data object AdminRegister : Screen("register/admin")
    data object WorkerRegister : Screen("register/worker")

    // Dashboards per Role
    data object AdminDashboard : Screen("dashboard/admin")
    data object EngineerDashboard : Screen("dashboard/engineer")
    data object WorkerDashboard : Screen("dashboard/worker")
    data object SafetyOfficerDashboard : Screen("dashboard/safety_officer")

    // Future Shared Feature Shells
    data object Projects : Screen("projects")
    data object Permits : Screen("permits")
    data object Hazards : Screen("hazards")
    data object Incidents : Screen("incidents")
    data object Inspections : Screen("inspections")
    data object Notifications : Screen("notifications")
    data object Profile : Screen("profile")
}

/**
 * Centralized Role Navigation Configuration.
 * Maps authenticated roles to their initial dashboard and role-specific bottom navigation tabs.
 */
object RoleNavigationConfig {

    fun getDashboardRoute(role: Role): String {
        return when (role) {
            Role.ADMIN -> Screen.AdminDashboard.route
            Role.ENGINEER -> Screen.EngineerDashboard.route
            Role.WORKER -> Screen.WorkerDashboard.route
            Role.SAFETY_OFFICER -> Screen.SafetyOfficerDashboard.route
        }
    }

    fun getBottomNavItems(role: Role): List<SafeOpsNavItem> {
        return when (role) {
            Role.ADMIN -> listOf(
                SafeOpsNavItem(Screen.AdminDashboard.route, "Overview", SafeOpsIcons.Admin),
                SafeOpsNavItem(Screen.Projects.route, "Projects", SafeOpsIcons.Projects),
                SafeOpsNavItem(Screen.Permits.route, "Permits", SafeOpsIcons.Permits),
                SafeOpsNavItem(Screen.Notifications.route, "Alerts", SafeOpsIcons.Notifications),
                SafeOpsNavItem(Screen.Profile.route, "Profile", SafeOpsIcons.Profile)
            )
            Role.SAFETY_OFFICER -> listOf(
                SafeOpsNavItem(Screen.SafetyOfficerDashboard.route, "Safety Hub", SafeOpsIcons.SafetyOfficer),
                SafeOpsNavItem(Screen.Permits.route, "Permits", SafeOpsIcons.Permits),
                SafeOpsNavItem(Screen.Hazards.route, "Hazards", SafeOpsIcons.Hazards),
                SafeOpsNavItem(Screen.Inspections.route, "Inspections", SafeOpsIcons.Inspections),
                SafeOpsNavItem(Screen.Profile.route, "Profile", SafeOpsIcons.Profile)
            )
            Role.ENGINEER -> listOf(
                SafeOpsNavItem(Screen.EngineerDashboard.route, "Site Hub", SafeOpsIcons.Engineer),
                SafeOpsNavItem(Screen.Projects.route, "Sites", SafeOpsIcons.Projects),
                SafeOpsNavItem(Screen.Permits.route, "PTW Work", SafeOpsIcons.Permits),
                SafeOpsNavItem(Screen.Hazards.route, "Hazards", SafeOpsIcons.Hazards),
                SafeOpsNavItem(Screen.Profile.route, "Profile", SafeOpsIcons.Profile)
            )
            Role.WORKER -> listOf(
                SafeOpsNavItem(Screen.WorkerDashboard.route, "Workplace", SafeOpsIcons.Worker),
                SafeOpsNavItem(Screen.Permits.route, "My Permits", SafeOpsIcons.Permits),
                SafeOpsNavItem(Screen.Hazards.route, "Report", SafeOpsIcons.Hazards),
                SafeOpsNavItem(Screen.Notifications.route, "Alerts", SafeOpsIcons.Notifications),
                SafeOpsNavItem(Screen.Profile.route, "Profile", SafeOpsIcons.Profile)
            )
        }
    }
}
