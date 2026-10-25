package com.safeops.app

import com.safeops.app.domain.model.Permission
import com.safeops.app.domain.model.Role
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RoleModelTest {

    @Test
    fun `Role fromKey maps valid keys to expected roles`() {
        assertEquals(Role.ADMIN, Role.fromKey("admin"))
        assertEquals(Role.ADMIN, Role.fromKey("ADMIN"))
        assertEquals(Role.ENGINEER, Role.fromKey("engineer"))
        assertEquals(Role.SAFETY_OFFICER, Role.fromKey("safety_officer"))
        assertEquals(Role.SAFETY_OFFICER, Role.fromKey("safetyofficer"))
        assertEquals(Role.WORKER, Role.fromKey("worker"))
        assertEquals(Role.WORKER, Role.fromKey("unknown_key"))
        assertEquals(Role.WORKER, Role.fromKey(null))
    }

    @Test
    fun `Admin role has full administrative permissions`() {
        val adminPermissions = Permission.defaultPermissionsForRole(Role.ADMIN)
        assertTrue(adminPermissions.contains(Permission.ADMIN_CONFIG))
        assertTrue(adminPermissions.contains(Permission.MANAGE_WORKERS))
        assertTrue(adminPermissions.contains(Permission.APPROVE_PERMIT))
        assertEquals(Permission.entries.size, adminPermissions.size)
    }

    @Test
    fun `Safety Officer has approval and inspection permissions`() {
        val safetyPermissions = Permission.defaultPermissionsForRole(Role.SAFETY_OFFICER)
        assertTrue(safetyPermissions.contains(Permission.APPROVE_PERMIT))
        assertTrue(safetyPermissions.contains(Permission.CONDUCT_INSPECTION))
        assertTrue(safetyPermissions.contains(Permission.RESOLVE_HAZARD))
    }

    @Test
    fun `Worker role has field reporting and submission permissions`() {
        val workerPermissions = Permission.defaultPermissionsForRole(Role.WORKER)
        assertTrue(workerPermissions.contains(Permission.CREATE_PERMIT))
        assertTrue(workerPermissions.contains(Permission.REPORT_HAZARD))
        assertTrue(!workerPermissions.contains(Permission.ADMIN_CONFIG))
    }
}
