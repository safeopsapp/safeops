package com.safeops.app.domain.repository

import com.safeops.app.core.common.Result
import com.safeops.app.domain.model.AdminRegistrationRequest
import com.safeops.app.domain.model.AdminRegistrationResult
import com.safeops.app.domain.model.ReferralCodeValidationResult
import com.safeops.app.domain.model.WorkerRegistrationRequest

/**
 * Registration repository contract.
 * Data layer must implement this interface.
 */
interface RegistrationRepository {
    /** Register admin and their organization. Returns referral code upon success. */
    suspend fun registerAdmin(request: AdminRegistrationRequest): Result<AdminRegistrationResult>

    /** Validate a referral/org code before worker registration. */
    suspend fun validateReferralCode(code: String): Result<ReferralCodeValidationResult>

    /** Register a worker under an existing organization using referral code. */
    suspend fun registerWorker(request: WorkerRegistrationRequest): Result<Unit>

    /** Check if an Organization ID (short code) is already taken. */
    suspend fun isOrgIdAvailable(orgId: String): Result<Boolean>
}
