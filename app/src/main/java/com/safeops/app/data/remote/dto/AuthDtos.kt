package com.safeops.app.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponseDto<T>(
    val success: Boolean = false,
    val message: String? = null,
    val data: T? = null,
    val error: String? = null
)

@Serializable
data class AuthMeDto(
    val userId: String? = null,
    val id: String? = null,
    val firebaseUid: String? = null,
    val email: String? = null,
    val displayName: String? = null,
    val photoUrl: String? = null,
    val status: String? = "ACTIVE",
    val organizations: List<OrgSummaryDto> = emptyList(),
    val projects: List<ProjectSummaryDto> = emptyList(),
    val sites: List<SiteSummaryDto> = emptyList(),
    val roles: Set<String> = emptySet(),
    val permissions: Set<String> = emptySet()
) {
    val effectiveId: String get() = id ?: userId ?: firebaseUid ?: ""
}

@Serializable
data class OrgSummaryDto(
    val organizationId: String? = null,
    val id: String? = null,
    val name: String = "",
    val slug: String? = null,
    val role: String? = null
) {
    val effectiveId: String get() = id ?: organizationId ?: ""
}

@Serializable
data class ProjectSummaryDto(
    val projectId: String? = null,
    val id: String? = null,
    val organizationId: String? = null,
    val name: String = "",
    val code: String? = null,
    val role: String? = null
) {
    val effectiveId: String get() = id ?: projectId ?: ""
}

@Serializable
data class SiteSummaryDto(
    val siteId: String? = null,
    val id: String? = null,
    val projectId: String? = null,
    val name: String = "",
    val code: String? = null
) {
    val effectiveId: String get() = id ?: siteId ?: ""
}

@Serializable
data class ReferralCodeDto(
    val valid: Boolean = false,
    val organizationName: String? = null,
    val orgId: String? = null,
    val errorMessage: String? = null
)

@Serializable
data class AdminRegistrationRequestDto(
    val fullName: String,
    val organizationName: String,
    val organizationId: String
)

@Serializable
data class AdminRegistrationResponseDto(
    val customerId: String? = null,
    val organizationId: String? = null,
    val orgId: String? = null,
    val organizationName: String? = null,
    val referralCode: String? = null,
    val adminName: String? = null,
    val adminEmail: String? = null,
    val message: String? = null
)

@Serializable
data class StaffRegistrationRequestDto(
    val referralCode: String,
    val fullName: String,
    val workerRole: String,
    val workLocation: String
)

@Serializable
data class MessageResponseDto(
    val status: String? = null,
    val message: String? = null
)

