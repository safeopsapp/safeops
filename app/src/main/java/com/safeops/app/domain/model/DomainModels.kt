package com.safeops.app.domain.model

data class User(
    val id: String,
    val email: String,
    val fullName: String,
    val role: Role,
    val avatarUrl: String? = null,
    val phoneNumber: String? = null,
    val organizationId: String,
    val isActive: Boolean = true
)

data class Organization(
    val id: String,
    val name: String,
    val code: String,
    val activeSitesCount: Int = 0
)

data class Project(
    val id: String,
    val code: String,
    val name: String,
    val location: String,
    val organizationId: String,
    val status: String = "ACTIVE"
)

data class Worker(
    val id: String,
    val employeeId: String,
    val fullName: String,
    val trade: String,
    val certificationStatus: String,
    val assignedSiteId: String? = null
)

enum class PermitStatus(val displayName: String) {
    DRAFT("Draft"),
    SUBMITTED("Submitted"),
    UNDER_REVIEW("Under Review"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    ACTIVE("Active"),
    SUSPENDED("Suspended"),
    CLOSED("Closed"),
    ARCHIVED("Archived");

    val isEditable: Boolean get() = this == DRAFT
    val isActionableBySafetyOfficer: Boolean get() = this == SUBMITTED || this == UNDER_REVIEW || this == ACTIVE
}

data class Permit(
    val id: String,
    val permitNumber: String,
    val title: String,
    val siteId: String,
    val siteName: String,
    val hazardType: String,
    val status: PermitStatus,
    val applicantId: String,
    val applicantName: String,
    val approverId: String? = null,
    val approverName: String? = null,
    val createdAt: Long,
    val validUntil: Long
)

data class Hazard(
    val id: String,
    val title: String,
    val description: String,
    val siteId: String,
    val severity: String,
    val status: String,
    val reportedBy: String,
    val reportedAt: Long
)

data class Incident(
    val id: String,
    val incidentNumber: String,
    val title: String,
    val severity: String,
    val siteId: String,
    val reportedAt: Long,
    val status: String
)

data class Inspection(
    val id: String,
    val title: String,
    val siteId: String,
    val inspectorId: String,
    val status: String,
    val scheduledDate: Long,
    val score: Int? = null
)

data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: Long,
    val isRead: Boolean = false,
    val type: String
)

data class Document(
    val id: String,
    val title: String,
    val fileType: String,
    val fileUrl: String,
    val uploadedAt: Long,
    val sizeBytes: Long
)

data class AuthenticatedSession(
    val user: User,
    val organization: Organization,
    val role: Role,
    val currentProject: Project?,
    val permissions: Set<Permission>,
    val accessToken: String,
    val refreshToken: String? = null,
    val expiresAt: Long
) {
    fun hasPermission(permission: Permission): Boolean {
        return permissions.contains(permission)
    }
}
