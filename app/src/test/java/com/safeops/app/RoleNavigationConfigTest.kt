package com.safeops.app

import com.safeops.app.core.navigation.RoleNavigationConfig
import com.safeops.app.core.navigation.Screen
import com.safeops.app.domain.model.Role
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RoleNavigationConfigTest {

    @Test
    fun `Role dashboard routes match role requirements`() {
        assertEquals(Screen.AdminDashboard.route, RoleNavigationConfig.getDashboardRoute(Role.ADMIN))
        assertEquals(Screen.EngineerDashboard.route, RoleNavigationConfig.getDashboardRoute(Role.ENGINEER))
        assertEquals(Screen.WorkerDashboard.route, RoleNavigationConfig.getDashboardRoute(Role.WORKER))
        assertEquals(Screen.SafetyOfficerDashboard.route, RoleNavigationConfig.getDashboardRoute(Role.SAFETY_OFFICER))
    }

    @Test
    fun `Role navigation items provide role-specific destinations`() {
        val adminItems = RoleNavigationConfig.getBottomNavItems(Role.ADMIN)
        assertTrue(adminItems.any { it.route == Screen.AdminDashboard.route })
        assertTrue(adminItems.any { it.route == Screen.Projects.route })

        val safetyItems = RoleNavigationConfig.getBottomNavItems(Role.SAFETY_OFFICER)
        assertTrue(safetyItems.any { it.route == Screen.SafetyOfficerDashboard.route })
        assertTrue(safetyItems.any { it.route == Screen.Inspections.route })

        val workerItems = RoleNavigationConfig.getBottomNavItems(Role.WORKER)
        assertTrue(workerItems.any { it.route == Screen.WorkerDashboard.route })
        assertTrue(workerItems.any { it.route == Screen.Permits.route })
    }
}
