package com.safeops.app.feature.permit

import com.safeops.app.feature.permit.domain.model.ChecklistItemResponse
import com.safeops.app.feature.permit.domain.model.ChecklistStatus
import com.safeops.app.feature.permit.domain.model.DailyChecklistReview
import com.safeops.app.feature.permit.domain.model.PermitFormModel
import com.safeops.app.feature.permit.domain.model.PermitHeader
import com.safeops.app.feature.permit.domain.model.PermitHseAssessment
import com.safeops.app.feature.permit.domain.model.PermitPersonnel
import com.safeops.app.feature.permit.domain.model.PermitSchedule
import com.safeops.app.feature.permit.domain.model.PermitType
import com.safeops.app.feature.permit.domain.template.PermitTemplateRegistry
import com.safeops.app.feature.permit.domain.validation.PermitValidationEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PermitValidationEngineTest {

    @Test
    fun `validate empty General Work permit returns validation errors`() {
        val emptyPermit = PermitFormModel(permitType = PermitType.GENERAL_WORK)
        val result = PermitValidationEngine.validateForSubmit(emptyPermit)
        assertFalse(result.isValid)
        assertTrue(result.errors.containsKey("workPerformedBy"))
        assertTrue(result.errors.containsKey("location"))
        assertTrue(result.errors.containsKey("workDate"))
        assertTrue(result.errors.containsKey("siteEngineerName"))
    }

    @Test
    fun `validate valid General Work permit passes submit validation`() {
        val validPermit = createValidPermit(PermitType.GENERAL_WORK)
        val result = PermitValidationEngine.validateForSubmit(validPermit)
        assertTrue(result.isValid)
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun `validate Lifting Work permit requires crane operator and license number`() {
        val liftingPermit = createValidPermit(PermitType.LIFTING).copy(
            personnel = PermitPersonnel(
                siteEngineerName = "Vasanthan M",
                siteEngineerContact = "+91 98765 43210",
                liftingSupervisorName = "S. Murugan",
                riggerSignalManName = "P. Kumar",
                craneOperatorName = "Selvam",
                craneOperatorLicenseNo = "" // Missing license
            )
        )
        val result = PermitValidationEngine.validateForSubmit(liftingPermit)
        assertFalse(result.isValid)
        assertTrue(result.errors.containsKey("craneOperatorLicenseNo"))
    }

    @Test
    fun `validate Piling Work permit requires rig operator and license number`() {
        val pilingPermit = createValidPermit(PermitType.PILING).copy(
            personnel = PermitPersonnel(
                siteEngineerName = "Vasanthan M",
                siteEngineerContact = "+91 98765 43210",
                pilingSupervisorName = "M. Arumugam",
                riggerSignalManName = "P. Kumar",
                rigOperatorName = "V. Logan",
                rigOperatorLicenseNo = "" // Missing license
            )
        )
        val result = PermitValidationEngine.validateForSubmit(pilingPermit)
        assertFalse(result.isValid)
        assertTrue(result.errors.containsKey("rigOperatorLicenseNo"))
    }

    @Test
    fun `validate Night Work permit requires at least one permit reference number`() {
        val nightWorkPermit = createValidPermit(PermitType.NIGHT_WORK).copy(
            requiredPermitReferences = emptyList()
        )
        val result = PermitValidationEngine.validateForSubmit(nightWorkPermit)
        assertFalse(result.isValid)
        assertTrue(result.errors.containsKey("requiredPermitReferences"))
    }

    @Test
    fun `validate NOT OK checklist item requires observation and corrective action`() {
        val template = PermitTemplateRegistry.getTemplate(PermitType.HOT_WORK)
        val responses = mutableMapOf<Int, ChecklistItemResponse>()
        template.checklistItems.indices.forEach { idx ->
            responses[idx] = ChecklistItemResponse(idx, template.checklistItems[idx], status = ChecklistStatus.OK)
        }
        // Set item 0 to NOT OK with blank observation
        responses[0] = ChecklistItemResponse(0, template.checklistItems[0], status = ChecklistStatus.NOT_OK, observation = "")

        val permit = createValidPermit(PermitType.HOT_WORK).copy(
            dailyChecklists = mapOf(1 to DailyChecklistReview(dayNumber = 1, responses = responses))
        )
        val result = PermitValidationEngine.validateForSubmit(permit)
        assertFalse(result.isValid)
        assertTrue(result.errors.containsKey("checklist_item_0_obs"))
    }

    @Test
    fun `all eight official permit templates exist and contain valid checklist items`() {
        val allTypes = PermitType.values()
        assertEquals(8, allTypes.size)
        allTypes.forEach { type ->
            val template = PermitTemplateRegistry.getTemplate(type)
            assertTrue(template.checklistItems.isNotEmpty())
        }
    }

    private fun createValidPermit(type: PermitType): PermitFormModel {
        val template = PermitTemplateRegistry.getTemplate(type)
        val responses = mutableMapOf<Int, ChecklistItemResponse>()
        template.checklistItems.indices.forEach { idx ->
            responses[idx] = ChecklistItemResponse(idx, template.checklistItems[idx], status = ChecklistStatus.OK)
        }

        return PermitFormModel(
            permitType = type,
            header = PermitHeader(
                permitNumber = "SPR-2026-000124",
                workPerformedBy = "Sairam Civil Infrastructure Ltd",
                location = "Zone A - South Pier 14",
                project = "Sairam Metro Line - Phase 2"
            ),
            schedule = PermitSchedule(
                workDate = "27 Sep 2026",
                startTime = "08:00 AM",
                endTime = "06:00 PM",
                workerCount = 4
            ),
            personnel = PermitPersonnel(
                siteEngineerName = "Vasanthan M",
                siteEngineerContact = "+91 98765 43210",
                liftingSupervisorName = "S. Murugan",
                riggerSignalManName = "P. Kumar",
                craneOperatorName = "A. Selvam",
                craneOperatorLicenseNo = "TN-CR-2024-889",
                pilingSupervisorName = "M. Arumugam",
                rigOperatorName = "V. Logan",
                rigOperatorLicenseNo = "TN-RIG-2024-551"
            ),
            descriptionOfWork = "Official safe work operation execution under permit parameters.",
            requiredPermitReferences = listOf("SPR/HSE/WP/01"),
            dailyChecklists = mapOf(1 to DailyChecklistReview(dayNumber = 1, responses = responses)),
            hseAssessment = PermitHseAssessment(
                statement1Briefed = true,
                statement2AreaSafe = true,
                statement3MeasuresTaken = true
            )
        )
    }
}
