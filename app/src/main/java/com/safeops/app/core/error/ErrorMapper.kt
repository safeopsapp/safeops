package com.safeops.app.core.error

import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Maps technical exceptions into user-facing domain errors.
 */
object ErrorMapper {

    fun mapThrowable(throwable: Throwable): SafeOpsError {
        return when (throwable) {
            is UnknownHostException -> SafeOpsError.NetworkUnavailable()
            is SocketTimeoutException -> SafeOpsError.Timeout()
            is IOException -> SafeOpsError.NetworkUnavailable("Network communication error. Please check connection.")
            is IllegalArgumentException -> SafeOpsError.ValidationError("Input", throwable.message ?: "Invalid parameter")
            else -> SafeOpsError.UnknownError("An unexpected error occurred.", throwable.localizedMessage)
        }
    }

    fun getUserFriendlyMessage(error: SafeOpsError): String {
        return when (error) {
            is SafeOpsError.NetworkUnavailable -> error.customMessage
            is SafeOpsError.Timeout -> error.customMessage
            is SafeOpsError.Unauthorized -> error.customMessage
            is SafeOpsError.Forbidden -> error.customMessage
            is SafeOpsError.NotFound -> error.customMessage
            is SafeOpsError.ValidationError -> error.reason
            is SafeOpsError.ServerError -> error.customMessage
            is SafeOpsError.UnknownError -> error.customMessage
        }
    }
}
