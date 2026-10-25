package com.safeops.app.data.local

import com.safeops.app.core.datastore.UserPreferencesDataSource
import com.safeops.app.core.security.TokenManager
import com.safeops.app.domain.model.AuthenticatedSession
import com.safeops.app.domain.model.Organization
import com.safeops.app.domain.model.Permission
import com.safeops.app.domain.model.Project
import com.safeops.app.domain.model.Role
import com.safeops.app.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

interface SessionLocalDataSource {
    val sessionFlow: Flow<AuthenticatedSession?>
    suspend fun getSession(): AuthenticatedSession?
    suspend fun saveSession(session: AuthenticatedSession)
    suspend fun clearSession()
}

class SessionLocalDataSourceImpl(
    private val tokenManager: TokenManager,
    private val preferencesDataSource: UserPreferencesDataSource
) : SessionLocalDataSource {

    private val _sessionState = MutableStateFlow<AuthenticatedSession?>(null)
    override val sessionFlow: Flow<AuthenticatedSession?> = _sessionState.asStateFlow()

    override suspend fun getSession(): AuthenticatedSession? {
        val current = _sessionState.value
        if (current != null) return current

        val token = tokenManager.getAccessToken() ?: return null
        // Reconstruct session if token exists
        return _sessionState.value
    }

    override suspend fun saveSession(session: AuthenticatedSession) {
        tokenManager.saveTokens(session.accessToken, session.refreshToken)
        preferencesDataSource.setLastSelectedRole(session.role.roleKey)
        preferencesDataSource.setLastUserEmail(session.user.email)
        _sessionState.value = session
    }

    override suspend fun clearSession() {
        tokenManager.clearTokens()
        _sessionState.value = null
    }
}
