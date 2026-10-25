package com.safeops.app.domain.model

/**
 * Industry types for admin organization registration.
 */
enum class IndustryType(val displayName: String) {
    CONSTRUCTION("Construction"),
    OIL_AND_GAS("Oil & Gas"),
    MINING("Mining"),
    MANUFACTURING("Manufacturing"),
    POWER_AND_UTILITIES("Power & Utilities"),
    INFRASTRUCTURE("Infrastructure"),
    REAL_ESTATE("Real Estate"),
    LOGISTICS("Logistics"),
    CHEMICAL("Chemical"),
    OTHER("Other");
}

/**
 * Data class representing admin registration form — Step 1.
 */
data class AdminRegistrationDetails(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val phoneNumber: String = ""
)

/**
 * Data class representing organization details — Step 2.
 */
data class OrganizationDetails(
    val organizationName: String = "",
    val organizationId: String = "",
    val industryType: IndustryType = IndustryType.CONSTRUCTION,
    val address: String = "",
    val contactNumber: String = "",
    val website: String = ""
)

/**
 * Full admin registration request combining both steps.
 */
data class AdminRegistrationRequest(
    val adminDetails: AdminRegistrationDetails,
    val organizationDetails: OrganizationDetails
)

/**
 * Result returned after successful admin registration.
 */
data class AdminRegistrationResult(
    val orgId: String,
    val referralCode: String,
    val organizationName: String,
    val adminName: String
)

/**
 * Worker registration request using referral code.
 */
data class WorkerRegistrationRequest(
    val referralCode: String,
    val fullName: String,
    val phoneNumber: String,
    val workerRole: WorkerRole,
    val workLocation: String,
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = ""
)

/**
 * Referral code validation result.
 */
data class ReferralCodeValidationResult(
    val isValid: Boolean,
    val organizationName: String = "",
    val orgId: String = "",
    val errorMessage: String = ""
)
