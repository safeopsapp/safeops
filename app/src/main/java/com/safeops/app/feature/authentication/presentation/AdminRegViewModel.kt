package com.safeops.app.feature.authentication.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safeops.app.core.common.Result
import com.safeops.app.core.error.ErrorMapper
import com.safeops.app.domain.model.AdminRegistrationDetails
import com.safeops.app.domain.model.AdminRegistrationRequest
import com.safeops.app.domain.model.OrganizationDetails
import com.safeops.app.domain.repository.RegistrationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AdminRegViewModel(
    private val registrationRepository: RegistrationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminRegUiState())
    val uiState: StateFlow<AdminRegUiState> = _uiState.asStateFlow()

    fun onEvent(event: AdminRegEvent) {
        when (event) {
            // ── Step 1 fields ──────────────────────────────────────
            is AdminRegEvent.FullNameChanged -> _uiState.update {
                it.copy(fullName = event.value, fullNameError = null)
            }
            is AdminRegEvent.EmailChanged -> _uiState.update {
                it.copy(email = event.value, emailError = null)
            }
            is AdminRegEvent.PasswordChanged -> {
                val pw = event.value
                _uiState.update {
                    it.copy(
                        password = pw,
                        passwordError = null,
                        hasMinLength = pw.length >= 8,
                        hasUppercase = pw.any { c -> c.isUpperCase() },
                        hasNumber = pw.any { c -> c.isDigit() },
                        hasSpecialChar = pw.any { c -> !c.isLetterOrDigit() }
                    )
                }
            }
            is AdminRegEvent.ConfirmPasswordChanged -> _uiState.update {
                it.copy(confirmPassword = event.value, confirmPasswordError = null)
            }
            is AdminRegEvent.PhoneNumberChanged -> _uiState.update {
                it.copy(phoneNumber = event.value)
            }

            // ── Step 2 fields ──────────────────────────────────────
            is AdminRegEvent.OrgNameChanged -> _uiState.update {
                it.copy(orgName = event.value, orgNameError = null)
            }
            is AdminRegEvent.OrgIdChanged -> {
                val cleaned = event.value.uppercase().filter { c -> c.isLetterOrDigit() }
                _uiState.update { it.copy(orgId = cleaned, orgIdError = null, orgIdAvailable = null) }
                checkOrgIdAvailability(cleaned)
            }
            is AdminRegEvent.IndustryTypeSelected -> _uiState.update {
                it.copy(industryType = event.type, showIndustryDropdown = false)
            }
            is AdminRegEvent.ToggleIndustryDropdown -> _uiState.update {
                it.copy(showIndustryDropdown = !it.showIndustryDropdown)
            }
            is AdminRegEvent.AddressChanged -> _uiState.update {
                it.copy(address = event.value, addressError = null)
            }
            is AdminRegEvent.ContactNumberChanged -> _uiState.update {
                it.copy(contactNumber = event.value, contactNumberError = null)
            }
            is AdminRegEvent.WebsiteChanged -> _uiState.update {
                it.copy(website = event.value)
            }

            // ── Navigation ─────────────────────────────────────────
            is AdminRegEvent.NextStep -> handleNextStep()
            is AdminRegEvent.PreviousStep -> handlePreviousStep()
            is AdminRegEvent.NavigateToStep -> {
                // Only allow navigating back to completed steps
                val currentOrdinal = _uiState.value.step.ordinal
                if (event.step.ordinal < currentOrdinal) {
                    _uiState.update { it.copy(step = event.step) }
                }
            }
            is AdminRegEvent.Submit -> submitRegistration()
            is AdminRegEvent.GoToDashboard -> { /* Handled in NavHost */ }
            is AdminRegEvent.DismissError -> _uiState.update { it.copy(errorMessage = null) }
        }
    }

    private fun handleNextStep() {
        val state = _uiState.value
        when (state.step) {
            AdminRegStep.ADMIN_DETAILS -> {
                if (validateStep1()) {
                    _uiState.update { it.copy(step = AdminRegStep.ORGANIZATION) }
                }
            }
            AdminRegStep.ORGANIZATION -> {
                if (validateStep2()) {
                    _uiState.update { it.copy(step = AdminRegStep.REVIEW) }
                }
            }
            AdminRegStep.REVIEW -> submitRegistration()
            AdminRegStep.COMPLETE -> { /* Do nothing */ }
        }
    }

    private fun handlePreviousStep() {
        val state = _uiState.value
        val prevStep = when (state.step) {
            AdminRegStep.ORGANIZATION -> AdminRegStep.ADMIN_DETAILS
            AdminRegStep.REVIEW -> AdminRegStep.ORGANIZATION
            AdminRegStep.COMPLETE -> AdminRegStep.REVIEW
            else -> return
        }
        _uiState.update { it.copy(step = prevStep) }
    }

    private fun validateStep1(): Boolean {
        val state = _uiState.value
        var valid = true

        val nameError = if (state.fullName.isBlank()) "Full name is required" else null
        val emailError = if (!com.safeops.app.core.utils.ValidationUtils.isValidEmail(state.email))
            "Enter a valid email address" else null
        val pwError = when {
            state.password.length < 8 -> "Password must be at least 8 characters"
            !state.hasUppercase -> "Must include at least one uppercase letter"
            !state.hasNumber -> "Must include at least one number"
            !state.hasSpecialChar -> "Must include at least one special character"
            else -> null
        }
        val confirmError = if (state.password != state.confirmPassword) "Passwords do not match" else null

        if (nameError != null || emailError != null || pwError != null || confirmError != null) valid = false

        _uiState.update {
            it.copy(
                fullNameError = nameError,
                emailError = emailError,
                passwordError = pwError,
                confirmPasswordError = confirmError
            )
        }
        return valid
    }

    private fun validateStep2(): Boolean {
        val state = _uiState.value
        var valid = true

        val orgNameError = if (state.orgName.isBlank()) "Organization name is required" else null
        val orgIdError = when {
            state.orgId.isBlank() -> "Organization ID is required"
            state.orgId.length < 3 -> "Organization ID must be at least 3 characters"
            else -> null
        }
        val addressError = if (state.address.isBlank()) "Address is required" else null
        val contactError = if (state.contactNumber.isBlank()) "Contact number is required" else null

        if (orgNameError != null || orgIdError != null || addressError != null || contactError != null) valid = false

        _uiState.update {
            it.copy(
                orgNameError = orgNameError,
                orgIdError = orgIdError,
                addressError = addressError,
                contactNumberError = contactError
            )
        }
        return valid
    }

    private fun checkOrgIdAvailability(orgId: String) {
        if (orgId.length < 3) return
        viewModelScope.launch {
            val result = registrationRepository.isOrgIdAvailable(orgId)
            if (result is Result.Success) {
                _uiState.update { it.copy(orgIdAvailable = result.data) }
            }
        }
    }

    private fun submitRegistration() {
        val state = _uiState.value
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val request = AdminRegistrationRequest(
                adminDetails = AdminRegistrationDetails(
                    fullName = state.fullName,
                    email = state.email,
                    password = state.password,
                    confirmPassword = state.confirmPassword,
                    phoneNumber = state.phoneNumber
                ),
                organizationDetails = OrganizationDetails(
                    organizationName = state.orgName,
                    organizationId = state.orgId,
                    industryType = state.industryType,
                    address = state.address,
                    contactNumber = state.contactNumber,
                    website = state.website
                )
            )

            when (val result = registrationRepository.registerAdmin(request)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            step = AdminRegStep.COMPLETE,
                            registrationResult = result.data
                        )
                    }
                }
                is Result.Error -> {
                    val message = ErrorMapper.getUserFriendlyMessage(result.error)
                    _uiState.update { it.copy(isLoading = false, errorMessage = message) }
                }
                is Result.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }
}
