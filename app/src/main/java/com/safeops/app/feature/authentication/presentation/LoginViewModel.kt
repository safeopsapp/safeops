package com.safeops.app.feature.authentication.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safeops.app.core.common.Result
import com.safeops.app.core.datastore.UserPreferencesDataSource
import com.safeops.app.core.error.ErrorMapper
import com.safeops.app.domain.model.Role
import com.safeops.app.domain.usecase.LoginUseCase
import com.safeops.app.domain.usecase.LoginWithGoogleUseCase
import com.safeops.app.domain.usecase.ValidateCredentialsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase? = null,
    private val validateCredentialsUseCase: ValidateCredentialsUseCase,
    private val userPreferencesDataSource: UserPreferencesDataSource
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        loadSavedPreferences()
    }

    private fun loadSavedPreferences() {
        viewModelScope.launch {
            try {
                val rememberMe = userPreferencesDataSource.rememberMeFlow.first()
                val savedEmail = if (rememberMe) userPreferencesDataSource.lastUserEmailFlow.first() else null
                val savedRoleKey = if (rememberMe) userPreferencesDataSource.lastSelectedRoleFlow.first() else null
                val domainRole = if (savedRoleKey != null) Role.fromKey(savedRoleKey) else Role.WORKER
                val loginRole = if (domainRole == Role.ADMIN) LoginRole.ADMIN else LoginRole.STAFF

                _uiState.update { current ->
                    current.copy(
                        email = savedEmail ?: "",
                        rememberMe = rememberMe,
                        selectedLoginRole = loginRole
                    )
                }
            } catch (_: Exception) {}
        }
    }

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> {
                _uiState.update { it.copy(email = event.value, emailError = null) }
            }
            is LoginEvent.PasswordChanged -> {
                _uiState.update { it.copy(password = event.value, passwordError = null) }
            }
            is LoginEvent.SelectLoginRole -> {
                _uiState.update { it.copy(selectedLoginRole = event.role) }
            }
            is LoginEvent.ToggleRememberMe -> {
                _uiState.update { it.copy(rememberMe = !it.rememberMe) }
            }
            is LoginEvent.Submit -> performSignIn()
            is LoginEvent.GoogleSignIn -> performGoogleSignIn(event.idToken)
            is LoginEvent.GoogleSignInFailed -> {
                _uiState.update { it.copy(isLoading = false, errorMessage = event.message) }
            }
            is LoginEvent.DismissError -> {
                _uiState.update { it.copy(errorMessage = null) }
            }
        }
    }

    private fun performGoogleSignIn(googleIdToken: String) {
        val googleUseCase = loginWithGoogleUseCase
        if (googleUseCase == null) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "Google Sign-In is not configured.") }
            return
        }

        val state = _uiState.value
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val domainRole = when (state.selectedLoginRole) {
                LoginRole.ADMIN -> Role.ADMIN
                LoginRole.STAFF -> Role.WORKER
            }

            val result = googleUseCase(
                googleIdToken = googleIdToken,
                selectedRole = domainRole
            )

            when (result) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(isLoading = false, successSession = result.data)
                    }
                }
                is Result.Error -> {
                    val message = ErrorMapper.getUserFriendlyMessage(result.error)
                    _uiState.update { it.copy(isLoading = false, errorMessage = message) }
                }
                is Result.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    private fun performSignIn() {
        val state = _uiState.value
        val validation = validateCredentialsUseCase(state.email, state.password)

        if (!validation.isValid) {
            _uiState.update {
                it.copy(
                    emailError = validation.emailError,
                    passwordError = validation.passwordError
                )
            }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val domainRole = when (state.selectedLoginRole) {
                LoginRole.ADMIN -> Role.ADMIN
                LoginRole.STAFF -> Role.WORKER
            }
            val result = loginUseCase(
                emailOrUserId = state.email,
                password = state.password,
                selectedRole = domainRole,
                rememberMe = state.rememberMe
            )

            when (result) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(isLoading = false, successSession = result.data)
                    }
                }
                is Result.Error -> {
                    val message = ErrorMapper.getUserFriendlyMessage(result.error)
                    _uiState.update { it.copy(isLoading = false, errorMessage = message) }
                }
                is Result.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }
}
