package com.safeops.app.domain.repository

import com.safeops.app.core.common.Result
import com.safeops.app.domain.model.AuthenticatedSession
import com.safeops.app.domain.model.Permit
import com.safeops.app.domain.model.PermitStatus
import com.safeops.app.domain.model.Role
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(
        emailOrUserId: String,
        password: String,
        selectedRole: Role,
        rememberMe: Boolean
    ): Result<AuthenticatedSession>

    suspend fun logout(): Result<Unit>
}

interface SessionRepository {
    val sessionFlow: Flow<AuthenticatedSession?>
    suspend fun getCurrentSession(): AuthenticatedSession?
    suspend fun saveSession(session: AuthenticatedSession)
    suspend fun clearSession()
}

interface PermitRepository {
    fun getPermitsFlow(siteId: String? = null, status: PermitStatus? = null): Flow<List<Permit>>
    suspend fun getPermitById(permitId: String): Result<Permit>
    suspend fun submitPermit(permit: Permit): Result<Permit>
    suspend fun updatePermitStatus(permitId: String, newStatus: PermitStatus): Result<Permit>
}
