package com.safeops.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.safeops.app.core.common.Result
import com.safeops.app.core.error.ErrorMapper
import com.safeops.app.core.error.SafeOpsError
import com.safeops.app.core.utils.await
import com.safeops.app.data.remote.SafeOpsApiClient
import com.safeops.app.data.remote.dto.AdminRegistrationRequestDto
import com.safeops.app.data.remote.dto.StaffRegistrationRequestDto
import com.safeops.app.domain.model.AdminRegistrationRequest
import com.safeops.app.domain.model.AdminRegistrationResult
import com.safeops.app.domain.model.ReferralCodeValidationResult
import com.safeops.app.domain.model.WorkerRegistrationRequest
import com.safeops.app.domain.repository.RegistrationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RegistrationRepositoryImpl(
    private val firebaseAuthProvider: () -> FirebaseAuth = { FirebaseAuth.getInstance() },
    private val apiClient: SafeOpsApiClient = SafeOpsApiClient()
) : RegistrationRepository {

    override suspend fun registerAdmin(request: AdminRegistrationRequest): Result<AdminRegistrationResult> = withContext(Dispatchers.IO) {
        val auth = firebaseAuthProvider()
        val email = request.adminDetails.email.trim()
        val password = request.adminDetails.password

        val firebaseUser = try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            authResult.user ?: throw IllegalStateException("Firebase user creation succeeded but user is null")
        } catch (e: com.google.firebase.auth.FirebaseAuthUserCollisionException) {
            // Email already in Firebase - try signing in with provided credentials to complete onboarding
            try {
                val signInResult = auth.signInWithEmailAndPassword(email, password).await()
                signInResult.user ?: throw e
            } catch (_: Exception) {
                return@withContext Result.Error(
                    SafeOpsError.ValidationError("email", "This email address is already registered. Please sign in or use a different email.")
                )
            }
        } catch (e: Exception) {
            return@withContext Result.Error(ErrorMapper.mapThrowable(e))
        }

        try {
            val tokenResult = firebaseUser.getIdToken(true).await()
            val idToken = tokenResult.token
                ?: throw IllegalStateException("Failed to retrieve Firebase ID token after registration")

            val response = apiClient.registerAdmin(
                idToken = idToken,
                payload = AdminRegistrationRequestDto(
                    fullName = request.adminDetails.fullName.trim(),
                    organizationName = request.organizationDetails.organizationName.trim(),
                    organizationId = request.organizationDetails.organizationId.trim()
                )
            )

            Result.Success(
                AdminRegistrationResult(
                    orgId = response.organizationId ?: response.orgId ?: request.organizationDetails.organizationId,
                    referralCode = response.referralCode ?: request.organizationDetails.organizationId,
                    organizationName = response.organizationName ?: request.organizationDetails.organizationName,
                    adminName = response.adminName ?: request.adminDetails.fullName
                )
            )
        } catch (e: Exception) {
            Result.Error(ErrorMapper.mapThrowable(e))
        }
    }

    override suspend fun validateReferralCode(code: String): Result<ReferralCodeValidationResult> = withContext(Dispatchers.IO) {
        try {
            val dto = apiClient.validateReferralCode(code.trim())
            Result.Success(
                ReferralCodeValidationResult(
                    isValid = dto.valid,
                    organizationName = dto.organizationName ?: "",
                    orgId = dto.orgId ?: "",
                    errorMessage = dto.errorMessage ?: ""
                )
            )
        } catch (e: Exception) {
            Result.Error(ErrorMapper.mapThrowable(e))
        }
    }

    override suspend fun registerWorker(request: WorkerRegistrationRequest): Result<Unit> = withContext(Dispatchers.IO) {
        val auth = firebaseAuthProvider()
        val email = request.email.trim()
        val password = request.password

        val firebaseUser = try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            authResult.user ?: throw IllegalStateException("Firebase user creation succeeded but user is null")
        } catch (e: com.google.firebase.auth.FirebaseAuthUserCollisionException) {
            try {
                val signInResult = auth.signInWithEmailAndPassword(email, password).await()
                signInResult.user ?: throw e
            } catch (_: Exception) {
                return@withContext Result.Error(
                    SafeOpsError.ValidationError("email", "This email address is already registered. Please sign in or use a different email.")
                )
            }
        } catch (e: Exception) {
            return@withContext Result.Error(ErrorMapper.mapThrowable(e))
        }

        try {
            val tokenResult = firebaseUser.getIdToken(true).await()
            val idToken = tokenResult.token
                ?: throw IllegalStateException("Failed to retrieve Firebase ID token after registration")

            apiClient.registerStaff(
                idToken = idToken,
                payload = StaffRegistrationRequestDto(
                    referralCode = request.referralCode.trim(),
                    fullName = request.fullName.trim(),
                    workerRole = request.workerRole.displayName,
                    workLocation = request.workLocation.trim()
                )
            )

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(ErrorMapper.mapThrowable(e))
        }
    }

    override suspend fun isOrgIdAvailable(orgId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val dto = apiClient.validateReferralCode(orgId.trim())
            // If valid referral code already exists, the ID is taken; otherwise available
            Result.Success(!dto.valid)
        } catch (_: Exception) {
            Result.Success(true)
        }
    }
}
