package com.safeops.app.feature.permit.data.repository

import com.safeops.app.feature.permit.domain.model.PermitFormModel
import com.safeops.app.feature.permit.domain.model.PermitHeader
import com.safeops.app.feature.permit.domain.model.PermitPersonnel
import com.safeops.app.feature.permit.domain.model.PermitSchedule
import com.safeops.app.feature.permit.domain.model.PermitStatus
import com.safeops.app.feature.permit.domain.model.PermitType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.util.UUID

class PermitRepositoryImpl : PermitRepository {

    private val permitsState = MutableStateFlow<List<PermitFormModel>>(generateSeedPermits())
    private var permitCounter = 125

    override fun getPermits(): Flow<List<PermitFormModel>> = permitsState

    override fun getPermitById(id: String): Flow<PermitFormModel?> {
        return permitsState.map { list -> list.find { it.id == id } }
    }

    override suspend fun saveDraft(permit: PermitFormModel): String {
        val currentList = permitsState.value.toMutableList()
        val existingIndex = currentList.indexOfFirst { it.id == permit.id }
        val updated = permit.copy(
            status = PermitStatus.DRAFT,
            updatedAt = System.currentTimeMillis()
        )
        if (existingIndex >= 0) {
            currentList[existingIndex] = updated
        } else {
            currentList.add(0, updated)
        }
        permitsState.value = currentList
        return updated.id
    }

    override suspend fun submitPermit(permit: PermitFormModel): Result<PermitFormModel> {
        val currentList = permitsState.value.toMutableList()
        val existingIndex = currentList.indexOfFirst { it.id == permit.id }
        val submitted = permit.copy(
            status = PermitStatus.UNDER_EHS_REVIEW,
            updatedAt = System.currentTimeMillis()
        )
        if (existingIndex >= 0) {
            currentList[existingIndex] = submitted
        } else {
            currentList.add(0, submitted)
        }
        permitsState.value = currentList
        return Result.success(submitted)
    }

    override suspend fun updatePermitStatus(id: String, status: PermitStatus): Result<Unit> {
        val currentList = permitsState.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == id }
        if (index >= 0) {
            currentList[index] = currentList[index].copy(
                status = status,
                updatedAt = System.currentTimeMillis()
            )
            permitsState.value = currentList
            return Result.success(Unit)
        }
        return Result.failure(Exception("Permit not found"))
    }

    override suspend fun createNewPermit(
        type: PermitType,
        createdByUserId: String,
        createdByName: String
    ): PermitFormModel {
        val generatedNo = "SPR-2026-${String.format("%06d", permitCounter++)}"
        val newPermit = PermitFormModel(
            id = UUID.randomUUID().toString(),
            permitType = type,
            status = PermitStatus.DRAFT,
            header = PermitHeader(
                permitNumber = generatedNo,
                project = "Apex Metro Line - Phase 2"
            ),
            createdByUserId = createdByUserId,
            createdByName = createdByName
        )
        val currentList = permitsState.value.toMutableList()
        currentList.add(0, newPermit)
        permitsState.value = currentList
        return newPermit
    }

    private fun generateSeedPermits(): List<PermitFormModel> {
        return listOf(
            PermitFormModel(
                id = "SEED-001",
                permitType = PermitType.GENERAL_WORK,
                status = PermitStatus.APPROVED,
                header = PermitHeader(
                    permitNumber = "SPR-2026-000120",
                    workPerformedBy = "Apex Civil Infrastructure Ltd",
                    location = "Zone A - South Pier 14",
                    project = "Apex Metro Line - Phase 2"
                ),
                schedule = PermitSchedule(
                    workDate = "27 Sep 2026",
                    startTime = "08:00 AM",
                    endTime = "06:00 PM",
                    workerCount = 5
                ),
                personnel = PermitPersonnel(
                    siteEngineerName = "Marcus Vance",
                    siteEngineerContact = "+91 98765 43210"
                ),
                descriptionOfWork = "General shuttering and rebar binding for south pier foundation footings.",
                createdByName = "Marcus Vance",
                updatedAt = System.currentTimeMillis() - 3600000
            ),
            PermitFormModel(
                id = "SEED-002",
                permitType = PermitType.HOT_WORK,
                status = PermitStatus.UNDER_EHS_REVIEW,
                header = PermitHeader(
                    permitNumber = "SPR-2026-000121",
                    workPerformedBy = "Apex Structural Welders",
                    location = "Zone B - Elevated Viaduct Span 4",
                    project = "Apex Metro Line - Phase 2"
                ),
                schedule = PermitSchedule(
                    workDate = "28 Sep 2026",
                    startTime = "09:00 AM",
                    endTime = "05:00 PM",
                    workerCount = 3
                ),
                personnel = PermitPersonnel(
                    siteEngineerName = "K. Rajesh",
                    siteEngineerContact = "+91 98765 12345"
                ),
                descriptionOfWork = "Arc welding and oxy-acetylene torch cutting of steel girder web plates.",
                createdByName = "K. Rajesh",
                updatedAt = System.currentTimeMillis() - 7200000
            ),
            PermitFormModel(
                id = "SEED-003",
                permitType = PermitType.LIFTING,
                status = PermitStatus.DRAFT,
                header = PermitHeader(
                    permitNumber = "SPR-2026-000122",
                    workPerformedBy = "Heavy Cranes South India",
                    location = "Zone C - Station Concourse 2",
                    project = "Apex Metro Line - Phase 2"
                ),
                schedule = PermitSchedule(
                    workDate = "29 Sep 2026",
                    startTime = "10:00 AM",
                    endTime = "04:00 PM",
                    workerCount = 6
                ),
                personnel = PermitPersonnel(
                    siteEngineerName = "Marcus Vance",
                    siteEngineerContact = "+91 98765 43210",
                    liftingSupervisorName = "S. Murugan",
                    liftingSupervisorContact = "+91 94440 11223",
                    riggerSignalManName = "P. Kumar",
                    riggerSignalManContact = "+91 94440 33445",
                    craneOperatorName = "A. Selvam",
                    craneOperatorContact = "+91 94440 55667",
                    craneOperatorLicenseNo = "TN-CR-2024-889"
                ),
                descriptionOfWork = "Tandem lifting of 45-ton precast concrete U-girder onto pier caps.",
                createdByName = "Marcus Vance",
                updatedAt = System.currentTimeMillis() - 10800000
            ),
            PermitFormModel(
                id = "SEED-004",
                permitType = PermitType.WORK_AT_HEIGHT,
                status = PermitStatus.ACTIVE,
                header = PermitHeader(
                    permitNumber = "SPR-2026-000123",
                    workPerformedBy = "HighRise Scaffolding Services",
                    location = "Zone D - Station Facade Pillar 6",
                    project = "Apex Metro Line - Phase 2"
                ),
                schedule = PermitSchedule(
                    workDate = "27 Sep 2026",
                    startTime = "07:30 AM",
                    endTime = "05:30 PM",
                    workerCount = 4
                ),
                personnel = PermitPersonnel(
                    siteEngineerName = "R. Suresh",
                    siteEngineerContact = "+91 98765 67890"
                ),
                descriptionOfWork = "Erection of cup-lock modular scaffolding system above 6m height.",
                createdByName = "R. Suresh",
                updatedAt = System.currentTimeMillis() - 14400000
            )
        )
    }
}
