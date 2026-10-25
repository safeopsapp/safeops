package com.safeops.app.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AuthMeDto(
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
)

@Serializable
data class OrgSummaryDto(
    val id: String,
    val name: String,
    val slug: String,
    val role: String? = null
)

@Serializable
data class ProjectSummaryDto(
    val id: String,
    val organizationId: String,
    val name: String,
    val code: String,
    val role: String? = null
)

@Serializable
data class SiteSummaryDto(
    val id: String,
    val projectId: String,
    val name: String,
    val code: String
)

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
    val organizationId: String? = null,
    val organizationName: String? = null,
    val referralCode: String? = null,
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

