package com.safeops.app.feature.authentication.presentation

import com.safeops.app.domain.model.WorkerRole

// ─── Worker Registration State ────────────────────────────────────────
enum class WorkerRegStep { REFERRAL_CODE, WORKER_DETAILS, COMPLETE }

data class WorkerRegUiState(
    val step: WorkerRegStep = WorkerRegStep.REFERRAL_CODE,

    // Step 1 — Referral code entry
    val referralCode: String = "",
    val referralCodeError: String? = null,
    val isValidatingCode: Boolean = false,
    val validatedOrgName: String = "",
    val validatedOrgId: String = "",
    val codeValidated: Boolean = false,

    // Step 2 — Worker details
    val fullName: String = "",
    val fullNameError: String? = null,
    val phoneNumber: String = "",
    val phoneNumberError: String? = null,
    val workerRole: WorkerRole = WorkerRole.FIELD_WORKER,
    val showRoleDropdown: Boolean = false,
    val workLocation: String = "",
    val workLocationError: String? = null,
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val confirmPassword: String = "",
    val confirmPasswordError: String? = null,

    // Loading / Error
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed interface WorkerRegEvent {
    // Step 1
    data class ReferralCodeChanged(val value: String) : WorkerRegEvent
    data object ValidateCode : WorkerRegEvent

    // Step 2
    data class FullNameChanged(val value: String) : WorkerRegEvent
    data class PhoneNumberChanged(val value: String) : WorkerRegEvent
    data class WorkerRoleSelected(val role: WorkerRole) : WorkerRegEvent
    data object ToggleRoleDropdown : WorkerRegEvent
    data class WorkLocationChanged(val value: String) : WorkerRegEvent
    data class EmailChanged(val value: String) : WorkerRegEvent
    data class PasswordChanged(val value: String) : WorkerRegEvent
    data class ConfirmPasswordChanged(val value: String) : WorkerRegEvent

    // Navigation
    data object NextStep : WorkerRegEvent
    data object PreviousStep : WorkerRegEvent
    data object Submit : WorkerRegEvent
    data object DismissError : WorkerRegEvent
}
