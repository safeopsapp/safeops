package com.safeops.app.domain.model

/**
 * Centralized Role Model for SafeOps.
 * Scattered role strings across the codebase are strictly forbidden.
 */
enum class Role(val displayName: String, val roleKey: String) {
    ADMIN("Admin", "admin"),
    ENGINEER("Engineer", "engineer"),
    WORKER("Worker", "worker"),
    SAFETY_OFFICER("Safety Officer", "safety_officer");

    companion object {
        fun fromKey(key: String?): Role {
            return when (key?.lowercase()?.trim()) {
                "admin" -> ADMIN
                "engineer" -> ENGINEER
                "safety_officer", "safetyofficer", "safety-officer" -> SAFETY_OFFICER
                else -> WORKER
            }
        }
    }
}

/**
 * Granular permissions granted to authenticated roles.
 */
enum class Permission {
    VIEW_DASHBOARD,
    MANAGE_WORKERS,
    CREATE_PERMIT,
    APPROVE_PERMIT,
    SUSPEND_PERMIT,
    CLOSE_PERMIT,
    REPORT_HAZARD,
    RESOLVE_HAZARD,
    REPORT_INCIDENT,
    INVESTIGATE_INCIDENT,
    CONDUCT_INSPECTION,
    MANAGE_PROJECTS,
    VIEW_REPORTS,
    VIEW_DOCUMENTS,
    ADMIN_CONFIG;

    companion object {
        fun defaultPermissionsForRole(role: Role): Set<Permission> {
            return when (role) {
                Role.ADMIN -> entries.toSet()
                Role.SAFETY_OFFICER -> setOf(
                    VIEW_DASHBOARD,
                    CREATE_PERMIT,
                    APPROVE_PERMIT,
                    SUSPEND_PERMIT,
                    CLOSE_PERMIT,
                    REPORT_HAZARD,
                    RESOLVE_HAZARD,
                    REPORT_INCIDENT,
                    INVESTIGATE_INCIDENT,
                    CONDUCT_INSPECTION,
                    VIEW_REPORTS,
                    VIEW_DOCUMENTS
                )
                Role.ENGINEER -> setOf(
                    VIEW_DASHBOARD,
                    CREATE_PERMIT,
                    REPORT_HAZARD,
                    REPORT_INCIDENT,
                    CONDUCT_INSPECTION,
                    VIEW_REPORTS,
                    VIEW_DOCUMENTS
                )
                Role.WORKER -> setOf(
                    VIEW_DASHBOARD,
                    CREATE_PERMIT,
                    REPORT_HAZARD,
                    REPORT_INCIDENT,
                    VIEW_DOCUMENTS
                )
            }
        }
    }
}
