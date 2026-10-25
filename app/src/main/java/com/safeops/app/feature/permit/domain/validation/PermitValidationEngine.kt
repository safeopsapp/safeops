package com.safeops.app.feature.permit.domain.validation

import com.safeops.app.feature.permit.domain.model.ChecklistStatus
import com.safeops.app.feature.permit.domain.model.PermitFormModel
import com.safeops.app.feature.permit.domain.model.PermitType
import com.safeops.app.feature.permit.domain.template.PermitTemplateRegistry

data class PermitValidationResult(
    val isValid: Boolean,
    val errors: Map<String, String> = emptyMap()
) {
    val errorList: List<String> get() = errors.values.toList()
}

object PermitValidationEngine {

    fun validateForSubmit(permit: PermitFormModel): PermitValidationResult {
        val errors = mutableMapOf<String, String>()
        val template = PermitTemplateRegistry.getTemplate(permit.permitType)

        // 1. Header Validation
        if (permit.header.workPerformedBy.isBlank()) {
            errors["workPerformedBy"] = "Contractor / Work Performed By is required"
        }
        if (permit.header.location.isBlank()) {
            errors["location"] = "Site location is required"
        }

        // 2. Schedule Validation
        if (permit.schedule.workDate.isBlank()) {
            errors["workDate"] = "Work Date is required"
        }
        if (permit.schedule.startTime.isBlank()) {
            errors["startTime"] = "Start Time is required"
        }
        if (permit.schedule.endTime.isBlank()) {
            errors["endTime"] = "End Time is required"
        }
        if (permit.schedule.workerCount !in 1..7) {
            errors["workerCount"] = "Worker count must be between 1 and 7"
        }

        // 3. Common Site Engineer Validation
        if (permit.personnel.siteEngineerName.isBlank()) {
            errors["siteEngineerName"] = "Site Engineer / Supervisor name is required *"
        }
        if (permit.personnel.siteEngineerContact.isBlank()) {
            errors["siteEngineerContact"] = "Contact number is required *"
        } else if (permit.personnel.siteEngineerContact.length < 7) {
            errors["siteEngineerContact"] = "Enter a valid contact number *"
        }

        // 4. Specialized Team Validation
        if (template.requiresLiftingTeam) {
            if (permit.personnel.liftingSupervisorName.isBlank()) {
                errors["liftingSupervisorName"] = "Lifting Supervisor name is required *"
            }
            if (permit.personnel.riggerSignalManName.isBlank()) {
                errors["riggerSignalManName"] = "Rigger / Signal Man name is required *"
            }
            if (permit.personnel.craneOperatorName.isBlank()) {
                errors["craneOperatorName"] = "Crane Operator name is required *"
            }
            if (permit.personnel.craneOperatorLicenseNo.isBlank()) {
                errors["craneOperatorLicenseNo"] = "Crane Operator License No is required *"
            }
        }

        if (template.requiresPilingTeam) {
            if (permit.personnel.pilingSupervisorName.isBlank()) {
                errors["pilingSupervisorName"] = "Piling Supervisor name is required *"
            }
            if (permit.personnel.riggerSignalManName.isBlank()) {
                errors["riggerSignalManName"] = "Rigger / Signal Man name is required *"
            }
            if (permit.personnel.rigOperatorName.isBlank()) {
                errors["rigOperatorName"] = "Rig Operator name is required *"
            }
            if (permit.personnel.rigOperatorLicenseNo.isBlank()) {
                errors["rigOperatorLicenseNo"] = "Rig Operator License No is required *"
            }
        }

        // 5. Night Work Specific References
        if (template.requiresNightWorkReferences && permit.requiredPermitReferences.isEmpty()) {
            errors["requiredPermitReferences"] = "At least one required permit reference number must be specified *"
        }

        // 6. Description of Work
        if (permit.descriptionOfWork.isBlank()) {
            errors["descriptionOfWork"] = "Description of Work is required *"
        }

        // 7. Checklist Completeness Validation (Day 1 / active day)
        val activeChecklist = permit.dailyChecklists[1]?.responses ?: emptyMap()
        template.checklistItems.forEachIndexed { index, itemText ->
            val response = activeChecklist[index]
            val fieldKey = "checklist_item_$index"
            if (response == null || response.status == null) {
                errors[fieldKey] = "Checklist status for '$itemText' is required *"
            } else if (response.status == ChecklistStatus.NOT_OK) {
                if (response.observation.isBlank()) {
                    errors["${fieldKey}_obs"] = "Observation is required when NOT OK is selected *"
                }
                if (response.correctiveAction.isBlank()) {
                    errors["${fieldKey}_act"] = "Corrective Action is required when NOT OK is selected *"
                }
            }
        }

        // 8. Part 1B Assessment Acknowledgement
        if (!permit.hseAssessment.isAllAcknowledged) {
            errors["hseAssessment"] = "All 3 Contractor HSE Assessment statements must be acknowledged *"
        }

        return PermitValidationResult(
            isValid = errors.isEmpty(),
            errors = errors
        )
    }

    fun calculateCompletionPercentage(permit: PermitFormModel): Int {
        var completed = 0
        var total = 8

        if (permit.header.workPerformedBy.isNotBlank()) completed++
        if (permit.header.location.isNotBlank()) completed++
        if (permit.schedule.workDate.isNotBlank()) completed++
        if (permit.schedule.startTime.isNotBlank() && permit.schedule.endTime.isNotBlank()) completed++
        if (permit.personnel.siteEngineerName.isNotBlank() && permit.personnel.siteEngineerContact.isNotBlank()) completed++
        if (permit.descriptionOfWork.isNotBlank()) completed++

        val template = PermitTemplateRegistry.getTemplate(permit.permitType)
        val activeChecklist = permit.dailyChecklists[1]?.responses ?: emptyMap()
        val filledChecklistCount = template.checklistItems.indices.count { index ->
            activeChecklist[index]?.status != null
        }
        if (filledChecklistCount == template.checklistItems.size) completed++

        if (permit.hseAssessment.isAllAcknowledged) completed++

        return ((completed.toFloat() / total.toFloat()) * 100).toInt()
    }
}
