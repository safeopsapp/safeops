package com.safeops.app

import com.safeops.app.core.common.Result
import com.safeops.app.core.datastore.UserPreferencesDataSource
import com.safeops.app.domain.model.AuthenticatedSession
import com.safeops.app.domain.model.Organization
import com.safeops.app.domain.model.Permission
import com.safeops.app.domain.model.Role
import com.safeops.app.domain.model.User
import com.safeops.app.domain.repository.AuthRepository
import com.safeops.app.domain.repository.SessionRepository
import com.safeops.app.domain.usecase.LoginUseCase
import com.safeops.app.domain.usecase.ValidateCredentialsUseCase
import com.safeops.app.feature.authentication.presentation.LoginEvent
import com.safeops.app.feature.authentication.presentation.LoginRole
import com.safeops.app.feature.authentication.presentation.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val fakeAuthRepository = object : AuthRepository {
        override suspend fun login(
            emailOrUserId: String,
            password: String,
            selectedRole: Role,
            rememberMe: Boolean
        ): Result<AuthenticatedSession> {
            val user = User("u1", emailOrUserId, "Test User", selectedRole, null, null, "org1")
            val org = Organization("org1", "Test Org", "TORG", 2)
            val session = AuthenticatedSession(
                user = user,
                organization = org,
                role = selectedRole,
                currentProject = null,
                permissions = Permission.defaultPermissionsForRole(selectedRole),
                accessToken = "test_token",
                expiresAt = System.currentTimeMillis() + 3600000
            )
            return Result.Success(session)
        }

        override suspend fun logout(): Result<Unit> = Result.Success(Unit)
    }

    private val fakeSessionRepository = object : SessionRepository {
        var savedSession: AuthenticatedSession? = null
        override val sessionFlow: Flow<AuthenticatedSession?> = flowOf(null)
        override suspend fun getCurrentSession(): AuthenticatedSession? = savedSession
        override suspend fun saveSession(session: AuthenticatedSession) { savedSession = session }
        override suspend fun clearSession() { savedSession = null }
    }

    private val fakePreferencesDataSource = object : UserPreferencesDataSource {
        override val rememberMeFlow: Flow<Boolean> = flowOf(true)
        override val lastUserEmailFlow: Flow<String?> = flowOf("saved@safeops.app")
        override val lastSelectedRoleFlow: Flow<String?> = flowOf("admin")
        override val isDarkModeFlow: Flow<Boolean?> = flowOf(false)

        override suspend fun setRememberMe(rememberMe: Boolean) {}
        override suspend fun setLastUserEmail(email: String?) {}
        override suspend fun setLastSelectedRole(roleName: String) {}
        override suspend fun setDarkMode(isDark: Boolean?) {}
        override suspend fun clear() {}
    }

    private val validateCredentialsUseCase = ValidateCredentialsUseCase()
    private val loginUseCase = LoginUseCase(fakeAuthRepository, fakeSessionRepository, validateCredentialsUseCase)

    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel(
            loginUseCase = loginUseCase,
            validateCredentialsUseCase = validateCredentialsUseCase,
            userPreferencesDataSource = fakePreferencesDataSource
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Initial state loads saved preferences`() = runTest {
        advanceUntilIdle()
        assertEquals("saved@safeops.app", viewModel.uiState.value.email)
        assertEquals(LoginRole.ADMIN, viewModel.uiState.value.selectedLoginRole)
        assertTrue(viewModel.uiState.value.rememberMe)
    }

    @Test
    fun `Validation error occurs when email is blank on sign in`() = runTest {
        viewModel.onEvent(LoginEvent.EmailChanged(""))
        viewModel.onEvent(LoginEvent.PasswordChanged("123456"))
        viewModel.onEvent(LoginEvent.Submit)

        advanceUntilIdle()
        assertNotNull(viewModel.uiState.value.emailError)
    }

    @Test
    fun `Successful sign in updates state with authenticated session`() = runTest {
        viewModel.onEvent(LoginEvent.EmailChanged("engineer@safeops.app"))
        viewModel.onEvent(LoginEvent.PasswordChanged("password123"))
        viewModel.onEvent(LoginEvent.SelectLoginRole(LoginRole.STAFF))
        viewModel.onEvent(LoginEvent.Submit)

        advanceUntilIdle()
        assertNotNull(viewModel.uiState.value.successSession)
        assertEquals("engineer@safeops.app", viewModel.uiState.value.successSession?.user?.email)
    }
}
