package com.safeops.app.domain.usecase

import com.safeops.app.core.common.Result
import com.safeops.app.core.error.SafeOpsError
import com.safeops.app.core.utils.ValidationUtils
import com.safeops.app.domain.model.AuthenticatedSession
import com.safeops.app.domain.model.Role
import com.safeops.app.domain.repository.AuthRepository
import com.safeops.app.domain.repository.SessionRepository

data class ValidationResult(
    val isValid: Boolean,
    val emailError: String? = null,
    val passwordError: String? = null
)

class ValidateCredentialsUseCase {
    operator fun invoke(emailOrUserId: String, password: String): ValidationResult {
        val isEmailValid = ValidationUtils.isValidEmailOrUserId(emailOrUserId)
        val isPasswordValid = ValidationUtils.isValidPassword(password)

        val emailError = if (!isEmailValid) {
            if (emailOrUserId.isBlank()) "User ID or Email is required" else "Enter a valid User ID or Email"
        } else null

        val passwordError = if (!isPasswordValid) {
            if (password.isBlank()) "Password is required" else "Password must be at least 6 characters"
        } else null

        return ValidationResult(
            isValid = isEmailValid && isPasswordValid,
            emailError = emailError,
            passwordError = passwordError
        )
    }
}

class LoginUseCase(
    private val authRepository: AuthRepository,
    private val sessionRepository: SessionRepository,
    private val validateCredentialsUseCase: ValidateCredentialsUseCase
) {
    suspend operator fun invoke(
        emailOrUserId: String,
        password: String,
        selectedRole: Role,
        rememberMe: Boolean
    ): Result<AuthenticatedSession> {
        val validation = validateCredentialsUseCase(emailOrUserId, password)
        if (!validation.isValid) {
            val field = if (validation.emailError != null) "Email/User ID" else "Password"
            val reason = validation.emailError ?: validation.passwordError ?: "Invalid input"
            return Result.Error(SafeOpsError.ValidationError(field, reason))
        }

        val loginResult = authRepository.login(
            emailOrUserId = emailOrUserId.trim(),
            password = password,
            selectedRole = selectedRole,
            rememberMe = rememberMe
        )

        if (loginResult is Result.Success) {
            sessionRepository.saveSession(loginResult.data)
        }

        return loginResult
    }
}

class LoginWithGoogleUseCase(
    private val authRepository: AuthRepository,
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(
        googleIdToken: String,
        selectedRole: Role
    ): Result<AuthenticatedSession> {
        val loginResult = authRepository.loginWithGoogle(
            googleIdToken = googleIdToken,
            selectedRole = selectedRole
        )

        if (loginResult is Result.Success) {
            sessionRepository.saveSession(loginResult.data)
        }

        return loginResult
    }
}

class GetSessionUseCase(
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(): AuthenticatedSession? {
        return sessionRepository.getCurrentSession()
    }
}

class LogoutUseCase(
    private val authRepository: AuthRepository,
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        sessionRepository.clearSession()
        return authRepository.logout()
    }
}

class ResolveRoleNavigationUseCase {
    operator fun invoke(session: AuthenticatedSession?): String {
        if (session == null) return "login"
        return when (session.role) {
            Role.ADMIN -> "dashboard/admin"
            Role.ENGINEER -> "dashboard/engineer"
            Role.WORKER -> "dashboard/worker"
            Role.SAFETY_OFFICER -> "dashboard/safety_officer"
        }
    }
}
