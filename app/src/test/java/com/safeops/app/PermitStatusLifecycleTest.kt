package com.safeops.app

import com.safeops.app.core.designsystem.theme.BackgroundLight
import com.safeops.app.core.designsystem.theme.DarkBackground
import com.safeops.app.core.designsystem.theme.DarkPrimaryBlue
import com.safeops.app.core.designsystem.theme.DarkSafeOpsColorScheme
import com.safeops.app.core.designsystem.theme.LightSafeOpsColorScheme
import com.safeops.app.core.designsystem.theme.RoyalBluePrimary
import com.safeops.app.domain.model.PermitStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PermitStatusLifecycleTest {

    @Test
    fun `PermitStatus draft is editable`() {
        assertTrue(PermitStatus.DRAFT.isEditable)
        assertFalse(PermitStatus.SUBMITTED.isEditable)
        assertFalse(PermitStatus.APPROVED.isEditable)
    }

    @Test
    fun `PermitStatus actionable by Safety Officer`() {
        assertTrue(PermitStatus.SUBMITTED.isActionableBySafetyOfficer)
        assertTrue(PermitStatus.UNDER_REVIEW.isActionableBySafetyOfficer)
        assertTrue(PermitStatus.ACTIVE.isActionableBySafetyOfficer)
        assertFalse(PermitStatus.DRAFT.isActionableBySafetyOfficer)
        assertFalse(PermitStatus.CLOSED.isActionableBySafetyOfficer)
    }

    @Test
    fun `SafeOps theme color tokens match specifications`() {
        assertEquals(RoyalBluePrimary, LightSafeOpsColorScheme.primary)
        assertEquals(BackgroundLight, LightSafeOpsColorScheme.background)
        assertFalse(LightSafeOpsColorScheme.isDark)

        assertEquals(DarkPrimaryBlue, DarkSafeOpsColorScheme.primary)
        assertEquals(DarkBackground, DarkSafeOpsColorScheme.background)
        assertTrue(DarkSafeOpsColorScheme.isDark)
    }
}
