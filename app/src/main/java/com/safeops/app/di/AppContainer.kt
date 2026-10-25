package com.safeops.app.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.safeops.app.core.common.DefaultDispatcherProvider
import com.safeops.app.core.common.DispatcherProvider
import com.safeops.app.core.datastore.UserPreferencesDataSource
import com.safeops.app.core.datastore.UserPreferencesDataSourceImpl
import com.safeops.app.core.security.EncryptedPreferencesStorage
import com.safeops.app.core.security.SecureStorage
import com.safeops.app.core.security.TokenManager
import com.safeops.app.core.security.TokenManagerImpl
import com.safeops.app.data.local.SessionLocalDataSource
import com.safeops.app.data.local.SessionLocalDataSourceImpl
import com.safeops.app.data.remote.AuthRemoteDataSource
import com.safeops.app.data.remote.AuthRemoteDataSourceImpl
import com.safeops.app.data.repository.AuthRepositoryImpl
import com.safeops.app.data.repository.PermitRepositoryImpl
import com.safeops.app.data.repository.RegistrationRepositoryImpl
import com.safeops.app.data.repository.SessionRepositoryImpl
import com.safeops.app.domain.repository.AuthRepository
import com.safeops.app.domain.repository.PermitRepository
import com.safeops.app.domain.repository.RegistrationRepository
import com.safeops.app.domain.repository.SessionRepository
import com.safeops.app.domain.usecase.GetSessionUseCase
import com.safeops.app.domain.usecase.LoginUseCase
import com.safeops.app.domain.usecase.LogoutUseCase
import com.safeops.app.domain.usecase.ResolveRoleNavigationUseCase
import com.safeops.app.domain.usecase.ValidateCredentialsUseCase
import com.safeops.app.feature.authentication.presentation.AdminRegViewModel
import com.safeops.app.feature.authentication.presentation.LoginViewModel
import com.safeops.app.feature.authentication.presentation.WorkerRegViewModel

/**
 * Dependency Injection Container implementing Inversion of Control.
 * Clean Architecture compliant without manual global singleton anti-patterns.
 */
interface AppContainer {
    val dispatchers: DispatcherProvider
    val secureStorage: SecureStorage
    val tokenManager: TokenManager
    val userPreferencesDataSource: UserPreferencesDataSource
    val authRemoteDataSource: AuthRemoteDataSource
    val sessionLocalDataSource: SessionLocalDataSource
    val authRepository: AuthRepository
    val sessionRepository: SessionRepository
    val permitRepository: PermitRepository
    val registrationRepository: RegistrationRepository
    val validateCredentialsUseCase: ValidateCredentialsUseCase
    val loginUseCase: LoginUseCase
    val getSessionUseCase: GetSessionUseCase
    val logoutUseCase: LogoutUseCase
    val resolveRoleNavigationUseCase: ResolveRoleNavigationUseCase
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    override val dispatchers: DispatcherProvider by lazy {
        DefaultDispatcherProvider()
    }

    override val secureStorage: SecureStorage by lazy {
        EncryptedPreferencesStorage(context)
    }

    override val tokenManager: TokenManager by lazy {
        TokenManagerImpl(secureStorage)
    }

    override val userPreferencesDataSource: UserPreferencesDataSource by lazy {
        UserPreferencesDataSourceImpl(context)
    }

    override val authRemoteDataSource: AuthRemoteDataSource by lazy {
        AuthRemoteDataSourceImpl()
    }

    override val sessionLocalDataSource: SessionLocalDataSource by lazy {
        SessionLocalDataSourceImpl(tokenManager, userPreferencesDataSource)
    }

    override val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(authRemoteDataSource, sessionLocalDataSource, userPreferencesDataSource, dispatchers)
    }

    override val sessionRepository: SessionRepository by lazy {
        SessionRepositoryImpl(sessionLocalDataSource, dispatchers)
    }

    override val permitRepository: PermitRepository by lazy {
        PermitRepositoryImpl(dispatchers)
    }

    override val registrationRepository: RegistrationRepository by lazy {
        RegistrationRepositoryImpl()
    }

    override val validateCredentialsUseCase: ValidateCredentialsUseCase by lazy {
        ValidateCredentialsUseCase()
    }

    override val loginUseCase: LoginUseCase by lazy {
        LoginUseCase(authRepository, sessionRepository, validateCredentialsUseCase)
    }

    override val getSessionUseCase: GetSessionUseCase by lazy {
        GetSessionUseCase(sessionRepository)
    }

    override val logoutUseCase: LogoutUseCase by lazy {
        LogoutUseCase(authRepository, sessionRepository)
    }

    override val resolveRoleNavigationUseCase: ResolveRoleNavigationUseCase by lazy {
        ResolveRoleNavigationUseCase()
    }
}

class SafeOpsViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(
                    loginUseCase = container.loginUseCase,
                    validateCredentialsUseCase = container.validateCredentialsUseCase,
                    userPreferencesDataSource = container.userPreferencesDataSource
                ) as T
            }
            modelClass.isAssignableFrom(AdminRegViewModel::class.java) -> {
                AdminRegViewModel(
                    registrationRepository = container.registrationRepository
                ) as T
            }
            modelClass.isAssignableFrom(WorkerRegViewModel::class.java) -> {
                WorkerRegViewModel(
                    registrationRepository = container.registrationRepository
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
