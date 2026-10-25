package com.safeops.app.feature.authentication.presentation

import com.safeops.app.domain.model.AuthenticatedSession

// ─── Login State ──────────────────────────────────────────────────────
data class LoginUiState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val selectedLoginRole: LoginRole = LoginRole.STAFF,
    val rememberMe: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successSession: AuthenticatedSession? = null
)

// ─── Login Events ─────────────────────────────────────────────────────
sealed interface LoginEvent {
    data class EmailChanged(val value: String) : LoginEvent
    data class PasswordChanged(val value: String) : LoginEvent
    data class SelectLoginRole(val role: LoginRole) : LoginEvent
    data object ToggleRememberMe : LoginEvent
    data object Submit : LoginEvent
    data object DismissError : LoginEvent
}

// Kept for backward compatibility with existing tests
sealed interface ScreenState {
    data object Idle : ScreenState
    data object Loading : ScreenState
    data class Success(val session: AuthenticatedSession) : ScreenState
    data class Error(val message: String) : ScreenState
}

// Kept for backward compatibility with existing tests
sealed interface LoginUiEvent {
    data class OnEmailChanged(val emailOrUserId: String) : LoginUiEvent
    data class OnPasswordChanged(val password: String) : LoginUiEvent
    data class OnRoleSelected(val role: com.safeops.app.domain.model.Role) : LoginUiEvent
    data class OnRememberMeToggled(val rememberMe: Boolean) : LoginUiEvent
    data object OnSignInClicked : LoginUiEvent
    data object OnGoogleSignInClicked : LoginUiEvent
    data object OnForgotPasswordClicked : LoginUiEvent
    data object OnDismissError : LoginUiEvent
}
