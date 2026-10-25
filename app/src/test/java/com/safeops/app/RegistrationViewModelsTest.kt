package com.safeops.app

import com.safeops.app.core.common.Result
import com.safeops.app.domain.model.AdminRegistrationRequest
import com.safeops.app.domain.model.AdminRegistrationResult
import com.safeops.app.domain.model.IndustryType
import com.safeops.app.domain.model.ReferralCodeValidationResult
import com.safeops.app.domain.model.WorkerRegistrationRequest
import com.safeops.app.domain.model.WorkerRole
import com.safeops.app.domain.repository.RegistrationRepository
import com.safeops.app.feature.authentication.presentation.AdminRegEvent
import com.safeops.app.feature.authentication.presentation.AdminRegStep
import com.safeops.app.feature.authentication.presentation.AdminRegViewModel
import com.safeops.app.feature.authentication.presentation.WorkerRegEvent
import com.safeops.app.feature.authentication.presentation.WorkerRegStep
import com.safeops.app.feature.authentication.presentation.WorkerRegViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RegistrationViewModelsTest {

    private val testDispatcher = StandardTestDispatcher()
    private val registrationRepository = object : RegistrationRepository {
        override suspend fun registerAdmin(request: AdminRegistrationRequest): Result<AdminRegistrationResult> {
            val orgId = request.organizationDetails.organizationId.uppercase().take(10)
            return Result.Success(
                AdminRegistrationResult(
                    orgId = orgId,
                    referralCode = orgId,
                    organizationName = request.organizationDetails.organizationName,
                    adminName = request.adminDetails.fullName
                )
            )
        }

        override suspend fun validateReferralCode(code: String): Result<ReferralCodeValidationResult> {
            val trimmed = code.trim().uppercase()
            return if (trimmed.length in 4..12) {
                Result.Success(
                    ReferralCodeValidationResult(
                        isValid = true,
                        organizationName = "SafeOps Organization",
                        orgId = trimmed
                    )
                )
            } else {
                Result.Success(
                    ReferralCodeValidationResult(
                        isValid = false,
                        errorMessage = "Invalid referral code"
                    )
                )
            }
        }

        override suspend fun registerWorker(request: WorkerRegistrationRequest): Result<Unit> = Result.Success(Unit)

        override suspend fun isOrgIdAvailable(orgId: String): Result<Boolean> = Result.Success(true)
    }

    private lateinit var adminViewModel: AdminRegViewModel
    private lateinit var workerViewModel: WorkerRegViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        adminViewModel = AdminRegViewModel(registrationRepository)
        workerViewModel = WorkerRegViewModel(registrationRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ─── Admin Registration Tests ──────────────────────────────────────────

    @Test
    fun `Admin registration step 1 fails on invalid inputs`() = runTest {
        adminViewModel.onEvent(AdminRegEvent.FullNameChanged(""))
        adminViewModel.onEvent(AdminRegEvent.EmailChanged("invalid-email"))
        adminViewModel.onEvent(AdminRegEvent.PasswordChanged("weak"))
        adminViewModel.onEvent(AdminRegEvent.ConfirmPasswordChanged("mismatch"))
        adminViewModel.onEvent(AdminRegEvent.NextStep)

        advanceUntilIdle()
        val state = adminViewModel.uiState.value
        assertEquals(AdminRegStep.ADMIN_DETAILS, state.step)
        assertNotNull(state.fullNameError)
        assertNotNull(state.emailError)
        assertNotNull(state.passwordError)
        assertNotNull(state.confirmPasswordError)
    }

    @Test
    fun `Admin registration progresses through all 4 steps successfully`() = runTest {
        // Step 1: Admin Details
        adminViewModel.onEvent(AdminRegEvent.FullNameChanged("Vasanthan M"))
        adminViewModel.onEvent(AdminRegEvent.EmailChanged("vasanthan@safeops.app"))
        adminViewModel.onEvent(AdminRegEvent.PasswordChanged("SecurePass123!"))
        adminViewModel.onEvent(AdminRegEvent.ConfirmPasswordChanged("SecurePass123!"))
        adminViewModel.onEvent(AdminRegEvent.PhoneNumberChanged("+919876543210"))
        adminViewModel.onEvent(AdminRegEvent.NextStep)

        advanceUntilIdle()
        assertEquals(AdminRegStep.ORGANIZATION, adminViewModel.uiState.value.step)

        // Step 2: Org Details
        adminViewModel.onEvent(AdminRegEvent.OrgNameChanged("SafeOps Construction"))
        adminViewModel.onEvent(AdminRegEvent.OrgIdChanged("SAFEOPS"))
        adminViewModel.onEvent(AdminRegEvent.IndustryTypeSelected(IndustryType.CONSTRUCTION))
        adminViewModel.onEvent(AdminRegEvent.AddressChanged("Chennai, India"))
        adminViewModel.onEvent(AdminRegEvent.ContactNumberChanged("+919876543210"))
        adminViewModel.onEvent(AdminRegEvent.NextStep)

        advanceUntilIdle()
        assertEquals(AdminRegStep.REVIEW, adminViewModel.uiState.value.step)

        // Step 3: Review -> Submit
        adminViewModel.onEvent(AdminRegEvent.Submit)

        advanceUntilIdle()
        val finalState = adminViewModel.uiState.value
        assertEquals(AdminRegStep.COMPLETE, finalState.step)
        assertNotNull(finalState.registrationResult)
        assertEquals("SAFEOPS", finalState.registrationResult?.referralCode)
    }

    // ─── Worker Registration Tests ─────────────────────────────────────────

    @Test
    fun `Worker referral code validation succeeds for valid code`() = runTest {
        workerViewModel.onEvent(WorkerRegEvent.ReferralCodeChanged("SAFEOPS"))
        workerViewModel.onEvent(WorkerRegEvent.ValidateCode)

        advanceUntilIdle()
        val state = workerViewModel.uiState.value
        assertTrue(state.codeValidated)
        assertNull(state.referralCodeError)
    }

    @Test
    fun `Worker registration completes with full profile`() = runTest {
        // Step 1: Validate code
        workerViewModel.onEvent(WorkerRegEvent.ReferralCodeChanged("SAFEOPS"))
        workerViewModel.onEvent(WorkerRegEvent.ValidateCode)
        advanceUntilIdle()

        workerViewModel.onEvent(WorkerRegEvent.NextStep)
        assertEquals(WorkerRegStep.WORKER_DETAILS, workerViewModel.uiState.value.step)

        // Step 2: Fill worker details
        workerViewModel.onEvent(WorkerRegEvent.FullNameChanged("John Builder"))
        workerViewModel.onEvent(WorkerRegEvent.PhoneNumberChanged("+919876500000"))
        workerViewModel.onEvent(WorkerRegEvent.WorkerRoleSelected(WorkerRole.ENGINEER))
        workerViewModel.onEvent(WorkerRegEvent.WorkLocationChanged("Block A, SSR Site"))
        workerViewModel.onEvent(WorkerRegEvent.NextStep)

        advanceUntilIdle()
        val finalState = workerViewModel.uiState.value
        assertEquals(WorkerRegStep.COMPLETE, finalState.step)
    }
}
