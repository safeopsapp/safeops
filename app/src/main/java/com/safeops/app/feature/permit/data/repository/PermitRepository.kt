package com.safeops.app.feature.permit.data.repository

import com.safeops.app.feature.permit.domain.model.PermitFormModel
import com.safeops.app.feature.permit.domain.model.PermitStatus
import com.safeops.app.feature.permit.domain.model.PermitType
import kotlinx.coroutines.flow.Flow

interface PermitRepository {
    fun getPermits(): Flow<List<PermitFormModel>>
    fun getPermitById(id: String): Flow<PermitFormModel?>
    suspend fun saveDraft(permit: PermitFormModel): String
    suspend fun submitPermit(permit: PermitFormModel): Result<PermitFormModel>
    suspend fun updatePermitStatus(id: String, status: PermitStatus): Result<Unit>
    suspend fun createNewPermit(type: PermitType, createdByUserId: String, createdByName: String): PermitFormModel
}
