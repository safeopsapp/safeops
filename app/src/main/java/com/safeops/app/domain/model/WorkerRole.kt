package com.safeops.app.domain.model

/**
 * Worker sub-roles available during worker registration.
 * These are field specializations within the broader WORKER account type.
 */
enum class WorkerRole(val displayName: String, val key: String) {
    FIELD_WORKER("Field Worker", "field_worker"),
    ENGINEER("Engineer", "engineer"),
    SAFETY_OFFICER("Safety Officer", "safety_officer"),
    SUPERVISOR("Supervisor", "supervisor"),
    ELECTRICIAN("Electrician", "electrician"),
    PLUMBER("Plumber", "plumber"),
    WELDER("Welder", "welder"),
    CRANE_OPERATOR("Crane Operator", "crane_operator"),
    OTHER("Other", "other");

    companion object {
        fun fromKey(key: String?): WorkerRole =
            entries.firstOrNull { it.key == key?.lowercase() } ?: FIELD_WORKER
    }
}
