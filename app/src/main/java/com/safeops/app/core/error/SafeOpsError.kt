package com.safeops.app.core.error

/**
 * Standardized SafeOps error domain model.
 */
sealed class SafeOpsError(val message: String, val code: String? = null) {
    data class NetworkUnavailable(val customMessage: String = "No internet connection. Please check your network.") :
        SafeOpsError(customMessage, "ERR_NETWORK_UNAVAILABLE")

    data class Timeout(val customMessage: String = "Operation timed out. Please try again.") :
        SafeOpsError(customMessage, "ERR_TIMEOUT")

    data class Unauthorized(val customMessage: String = "Session expired or invalid credentials. Please log in.") :
        SafeOpsError(customMessage, "ERR_UNAUTHORIZED")

    data class Forbidden(val customMessage: String = "You do not have permission to access this resource.") :
        SafeOpsError(customMessage, "ERR_FORBIDDEN")

    data class NotFound(val customMessage: String = "The requested resource could not be found.") :
        SafeOpsError(customMessage, "ERR_NOT_FOUND")

    data class ValidationError(val field: String, val reason: String) :
        SafeOpsError("$field: $reason", "ERR_VALIDATION")

    data class ServerError(val customMessage: String = "A server error occurred. Please try again later.", val httpCode: Int? = null) :
        SafeOpsError(customMessage, "ERR_SERVER_${httpCode ?: "UNKNOWN"}")

    data class UnknownError(val customMessage: String = "An unexpected error occurred.", val exceptionMessage: String? = null) :
        SafeOpsError(customMessage, "ERR_UNKNOWN")
}
