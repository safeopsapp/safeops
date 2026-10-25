package com.safeops.app.feature.authentication.presentation

import com.safeops.app.domain.model.AdminRegistrationDetails
import com.safeops.app.domain.model.AdminRegistrationResult
import com.safeops.app.domain.model.IndustryType
import com.safeops.app.domain.model.OrganizationDetails

enum class AdminRegStep(val number: Int, val label: String) {
    ADMIN_DETAILS(1, "Admin Details"),
    ORGANIZATION(2, "Organization"),
    REVIEW(3, "Review"),
    COMPLETE(4, "Complete")
}

data class AdminRegUiState(
    val step: AdminRegStep = AdminRegStep.ADMIN_DETAILS,

    // Step 1 — Admin Details
    val fullName: String = "",
    val fullNameError: String? = null,
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val confirmPassword: String = "",
    val confirmPasswordError: String? = null,
    val phoneNumber: String = "",

    // Password strength indicators
    val hasMinLength: Boolean = false,
    val hasUppercase: Boolean = false,
    val hasNumber: Boolean = false,
    val hasSpecialChar: Boolean = false,

    // Step 2 — Organization Details
    val orgName: String = "",
    val orgNameError: String? = null,
    val orgId: String = "",
    val orgIdError: String? = null,
    val orgIdAvailable: Boolean? = null,
    val industryType: IndustryType = IndustryType.CONSTRUCTION,
    val showIndustryDropdown: Boolean = false,
    val address: String = "",
    val addressError: String? = null,
    val contactNumber: String = "",
    val contactNumberError: String? = null,
    val website: String = "",

    // Loading / Errors
    val isLoading: Boolean = false,
    val errorMessage: String? = null,

    // Step 4 — Result
    val registrationResult: AdminRegistrationResult? = null
)

sealed interface AdminRegEvent {
    // Step 1
    data class FullNameChanged(val value: String) : AdminRegEvent
    data class EmailChanged(val value: String) : AdminRegEvent
    data class PasswordChanged(val value: String) : AdminRegEvent
    data class ConfirmPasswordChanged(val value: String) : AdminRegEvent
    data class PhoneNumberChanged(val value: String) : AdminRegEvent

    // Step 2
    data class OrgNameChanged(val value: String) : AdminRegEvent
    data class OrgIdChanged(val value: String) : AdminRegEvent
    data class IndustryTypeSelected(val type: IndustryType) : AdminRegEvent
    data object ToggleIndustryDropdown : AdminRegEvent
    data class AddressChanged(val value: String) : AdminRegEvent
    data class ContactNumberChanged(val value: String) : AdminRegEvent
    data class WebsiteChanged(val value: String) : AdminRegEvent

    // Navigation
    data object NextStep : AdminRegEvent
    data object PreviousStep : AdminRegEvent
    data class NavigateToStep(val step: AdminRegStep) : AdminRegEvent
    data object Submit : AdminRegEvent
    data object GoToDashboard : AdminRegEvent
    data object DismissError : AdminRegEvent
}
