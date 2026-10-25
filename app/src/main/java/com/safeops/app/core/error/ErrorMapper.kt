package com.safeops.app.core.error

import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Maps technical exceptions into user-facing domain errors.
 */
object ErrorMapper {

    fun mapThrowable(throwable: Throwable): SafeOpsError {
        return when (throwable) {
            is UnknownHostException -> SafeOpsError.NetworkUnavailable("Server not reachable. Please check your internet connection.")
            is SocketTimeoutException -> SafeOpsError.Timeout("Connection timed out. Please check if the backend is reachable.")
            is ConnectException -> SafeOpsError.NetworkUnavailable("Cannot connect to backend server. Ensure it is running.")
            is com.google.firebase.auth.FirebaseAuthUserCollisionException ->
                SafeOpsError.ValidationError("email", "This email address is already in use. Please sign in or use a different email.")
            is com.google.firebase.auth.FirebaseAuthWeakPasswordException ->
                SafeOpsError.ValidationError("password", throwable.reason ?: "Password is too weak.")
            is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException ->
                SafeOpsError.Unauthorized(throwable.localizedMessage ?: "Invalid email or credentials.")
            is com.google.firebase.auth.FirebaseAuthInvalidUserException ->
                SafeOpsError.NotFound(throwable.localizedMessage ?: "No account found with this email.")
            is com.google.firebase.FirebaseNetworkException ->
                SafeOpsError.NetworkUnavailable("Firebase network error. Please check your internet connection.")
            is IOException -> SafeOpsError.NetworkUnavailable(throwable.message ?: "Network communication error.")
            is IllegalArgumentException -> SafeOpsError.ValidationError("Input", throwable.message ?: "Invalid parameter")
            else -> SafeOpsError.UnknownError(throwable.localizedMessage ?: "Registration failed. Please try again.", throwable.localizedMessage)
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
