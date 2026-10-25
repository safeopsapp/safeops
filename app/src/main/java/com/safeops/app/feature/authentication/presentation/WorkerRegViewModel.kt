package com.safeops.app.feature.authentication.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safeops.app.core.common.Result
import com.safeops.app.core.error.ErrorMapper
import com.safeops.app.domain.model.WorkerRegistrationRequest
import com.safeops.app.domain.repository.RegistrationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WorkerRegViewModel(
    private val registrationRepository: RegistrationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkerRegUiState())
    val uiState: StateFlow<WorkerRegUiState> = _uiState.asStateFlow()

    fun onEvent(event: WorkerRegEvent) {
        when (event) {
            // ── Step 1 ──────────────────────────────────────────────
            is WorkerRegEvent.ReferralCodeChanged -> {
                val cleaned = event.value.uppercase().filter { it.isLetterOrDigit() }
                _uiState.update {
                    it.copy(
                        referralCode = cleaned,
                        referralCodeError = null,
                        codeValidated = false,
                        validatedOrgName = "",
                        validatedOrgId = ""
                    )
                }
            }
            is WorkerRegEvent.ValidateCode -> validateReferralCode()
            is WorkerRegEvent.NextStep -> handleNextStep()
            is WorkerRegEvent.PreviousStep -> handlePreviousStep()

            // ── Step 2 ──────────────────────────────────────────────
            is WorkerRegEvent.FullNameChanged -> _uiState.update {
                it.copy(fullName = event.value, fullNameError = null)
            }
            is WorkerRegEvent.PhoneNumberChanged -> _uiState.update {
                it.copy(phoneNumber = event.value, phoneNumberError = null)
            }
            is WorkerRegEvent.WorkerRoleSelected -> _uiState.update {
                it.copy(workerRole = event.role, showRoleDropdown = false)
            }
            is WorkerRegEvent.ToggleRoleDropdown -> _uiState.update {
                it.copy(showRoleDropdown = !it.showRoleDropdown)
            }
            is WorkerRegEvent.WorkLocationChanged -> _uiState.update {
                it.copy(workLocation = event.value, workLocationError = null)
            }
            is WorkerRegEvent.EmailChanged -> _uiState.update {
                it.copy(email = event.value, emailError = null)
            }
            is WorkerRegEvent.PasswordChanged -> _uiState.update {
                it.copy(password = event.value, passwordError = null)
            }
            is WorkerRegEvent.ConfirmPasswordChanged -> _uiState.update {
                it.copy(confirmPassword = event.value, confirmPasswordError = null)
            }

            is WorkerRegEvent.Submit -> submitRegistration()
            is WorkerRegEvent.DismissError -> _uiState.update { it.copy(errorMessage = null) }
        }
    }

    private fun validateReferralCode() {
        val code = _uiState.value.referralCode.trim()
        if (code.isBlank()) {
            _uiState.update { it.copy(referralCodeError = "Please enter your organization referral code") }
            return
        }
        _uiState.update { it.copy(isValidatingCode = true, referralCodeError = null) }

        viewModelScope.launch {
            when (val result = registrationRepository.validateReferralCode(code)) {
                is Result.Success -> {
                    val validation = result.data
                    if (validation.isValid) {
                        _uiState.update {
                            it.copy(
                                isValidatingCode = false,
                                codeValidated = true,
                                validatedOrgName = validation.organizationName,
                                validatedOrgId = validation.orgId,
                                referralCodeError = null
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isValidatingCode = false,
                                codeValidated = false,
                                referralCodeError = validation.errorMessage.ifBlank {
                                    "Invalid referral code. Please check with your organization admin."
                                }
                            )
                        }
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isValidatingCode = false,
                            referralCodeError = ErrorMapper.getUserFriendlyMessage(result.error)
                        )
                    }
                }
                is Result.Loading -> {
                    _uiState.update { it.copy(isValidatingCode = true) }
                }
            }
        }
    }

    private fun handleNextStep() {
        val state = _uiState.value
        when (state.step) {
            WorkerRegStep.REFERRAL_CODE -> {
                if (!state.codeValidated) {
                    validateReferralCode()
                    return
                }
                _uiState.update { it.copy(step = WorkerRegStep.WORKER_DETAILS) }
            }
            WorkerRegStep.WORKER_DETAILS -> {
                if (validateStep2()) submitRegistration()
            }
            WorkerRegStep.COMPLETE -> { /* do nothing */ }
        }
    }

    private fun handlePreviousStep() {
        val state = _uiState.value
        val prev = when (state.step) {
            WorkerRegStep.WORKER_DETAILS -> WorkerRegStep.REFERRAL_CODE
            else -> return
        }
        _uiState.update { it.copy(step = prev) }
    }

    private fun validateStep2(): Boolean {
        val state = _uiState.value
        val nameError = if (state.fullName.isBlank()) "Full name is required" else null
        val phoneError = if (state.phoneNumber.isBlank()) "Phone number is required" else null
        val locationError = if (state.workLocation.isBlank()) "Work location is required" else null
        val emailError = if (state.email.isNotBlank() &&
            !com.safeops.app.core.utils.ValidationUtils.isValidEmail(state.email)
        ) "Enter a valid email address" else null
        val pwError = if (state.email.isNotBlank() && state.password.length < 8)
            "Password must be at least 8 characters" else null
        val confirmError = if (state.password != state.confirmPassword && state.password.isNotBlank())
            "Passwords do not match" else null

        _uiState.update {
            it.copy(
                fullNameError = nameError,
                phoneNumberError = phoneError,
                workLocationError = locationError,
                emailError = emailError,
                passwordError = pwError,
                confirmPasswordError = confirmError
            )
        }
        return listOf(nameError, phoneError, locationError, emailError, pwError, confirmError).all { it == null }
    }

    private fun submitRegistration() {
        val state = _uiState.value
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val request = WorkerRegistrationRequest(
                referralCode = state.referralCode,
                fullName = state.fullName,
                phoneNumber = state.phoneNumber,
                workerRole = state.workerRole,
                workLocation = state.workLocation,
                email = state.email,
                password = state.password,
                confirmPassword = state.confirmPassword
            )

            when (val result = registrationRepository.registerWorker(request)) {
                is Result.Success -> {
                    _uiState.update { it.copy(isLoading = false, step = WorkerRegStep.COMPLETE) }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = ErrorMapper.getUserFriendlyMessage(result.error)
                        )
                    }
                }
                is Result.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }
}
