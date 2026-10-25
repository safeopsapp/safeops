package com.safeops.app.data.repository

import com.safeops.app.core.common.DispatcherProvider
import com.safeops.app.core.common.Result
import com.safeops.app.core.datastore.UserPreferencesDataSource
import com.safeops.app.data.local.SessionLocalDataSource
import com.safeops.app.data.remote.AuthRemoteDataSource
import com.safeops.app.domain.model.AuthenticatedSession
import com.safeops.app.domain.model.Permit
import com.safeops.app.domain.model.PermitStatus
import com.safeops.app.domain.model.Role
import com.safeops.app.domain.repository.AuthRepository
import com.safeops.app.domain.repository.PermitRepository
import com.safeops.app.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class AuthRepositoryImpl(
    private val remoteDataSource: AuthRemoteDataSource,
    private val sessionLocalDataSource: SessionLocalDataSource,
    private val userPreferencesDataSource: UserPreferencesDataSource,
    private val dispatchers: DispatcherProvider
) : AuthRepository {

    override suspend fun login(
        emailOrUserId: String,
        password: String,
        selectedRole: Role,
        rememberMe: Boolean
    ): Result<AuthenticatedSession> = withContext(dispatchers.io) {
        userPreferencesDataSource.setRememberMe(rememberMe)
        if (rememberMe) {
            userPreferencesDataSource.setLastUserEmail(emailOrUserId)
            userPreferencesDataSource.setLastSelectedRole(selectedRole.roleKey)
        } else {
            userPreferencesDataSource.setLastUserEmail(null)
        }

        val result = remoteDataSource.authenticate(emailOrUserId, password, selectedRole)
        if (result is Result.Success) {
            sessionLocalDataSource.saveSession(result.data)
        }
        result
    }

    override suspend fun loginWithGoogle(
        googleIdToken: String,
        selectedRole: Role
    ): Result<AuthenticatedSession> = withContext(dispatchers.io) {
        val result = remoteDataSource.authenticateWithGoogle(googleIdToken, selectedRole)
        if (result is Result.Success) {
            sessionLocalDataSource.saveSession(result.data)
        }
        result
    }

    override suspend fun logout(): Result<Unit> = withContext(dispatchers.io) {
        sessionLocalDataSource.clearSession()
        Result.Success(Unit)
    }
}

class SessionRepositoryImpl(
    private val localDataSource: SessionLocalDataSource,
    private val dispatchers: DispatcherProvider
) : SessionRepository {

    override val sessionFlow: Flow<AuthenticatedSession?> = localDataSource.sessionFlow

    override suspend fun getCurrentSession(): AuthenticatedSession? = withContext(dispatchers.io) {
        localDataSource.getSession()
    }

    override suspend fun saveSession(session: AuthenticatedSession) = withContext(dispatchers.io) {
        localDataSource.saveSession(session)
    }

    override suspend fun clearSession() = withContext(dispatchers.io) {
        localDataSource.clearSession()
    }
}

class PermitRepositoryImpl(
    private val dispatchers: DispatcherProvider
) : PermitRepository {

    private val samplePermits = listOf(
        Permit(
            id = "pmt_001",
            permitNumber = "PTW-2026-0891",
            title = "Hot Work - Welding on Level 3 Girder",
            siteId = "proj_metro_01",
            siteName = "Metro Line 4",
            hazardType = "Fire & Sparks",
            status = PermitStatus.ACTIVE,
            applicantId = "usr_01",
            applicantName = "David Chen",
            approverId = "usr_02",
            approverName = "Sarah Jenkins",
            createdAt = System.currentTimeMillis() - 86400000L,
            validUntil = System.currentTimeMillis() + 86400000L
        ),
        Permit(
            id = "pmt_002",
            permitNumber = "PTW-2026-0892",
            title = "Confined Space Entry - Tank Inspection",
            siteId = "proj_metro_01",
            siteName = "Metro Line 4",
            hazardType = "Atmospheric / Gas",
            status = PermitStatus.UNDER_REVIEW,
            applicantId = "usr_03",
            applicantName = "Marcus Brody",
            approverId = null,
            approverName = null,
            createdAt = System.currentTimeMillis() - 14400000L,
            validUntil = System.currentTimeMillis() + 172800000L
        ),
        Permit(
            id = "pmt_003",
            permitNumber = "PTW-2026-0893",
            title = "Excavation Work - Trench Trench Depth 3.5m",
            siteId = "proj_metro_01",
            siteName = "Metro Line 4",
            hazardType = "Cave-in & Ground collapse",
            status = PermitStatus.APPROVED,
            applicantId = "usr_04",
            applicantName = "Aleksei Romanov",
            approverId = "usr_02",
            approverName = "Sarah Jenkins",
            createdAt = System.currentTimeMillis() - 28800000L,
            validUntil = System.currentTimeMillis() + 259200000L
        )
    )

    private val permitsState = MutableStateFlow(samplePermits)

    override fun getPermitsFlow(siteId: String?, status: PermitStatus?): Flow<List<Permit>> {
        return permitsState.asStateFlow().map { list ->
            list.filter { item ->
                (siteId == null || item.siteId == siteId) &&
                (status == null || item.status == status)
            }
        }
    }

    override suspend fun getPermitById(permitId: String): Result<Permit> = withContext(dispatchers.io) {
        val found = permitsState.value.firstOrNull { it.id == permitId }
        if (found != null) {
            Result.Success(found)
        } else {
            Result.Error(com.safeops.app.core.error.SafeOpsError.NotFound("Permit $permitId not found"))
        }
    }

    override suspend fun submitPermit(permit: Permit): Result<Permit> = withContext(dispatchers.io) {
        val updatedList = permitsState.value + permit
        permitsState.value = updatedList
        Result.Success(permit)
    }

    override suspend fun updatePermitStatus(
        permitId: String,
        newStatus: PermitStatus
    ): Result<Permit> = withContext(dispatchers.io) {
        val current = permitsState.value.firstOrNull { it.id == permitId }
        if (current == null) {
            return@withContext Result.Error(com.safeops.app.core.error.SafeOpsError.NotFound("Permit $permitId not found"))
        }
        val updated = current.copy(status = newStatus)
        permitsState.value = permitsState.value.map { if (it.id == permitId) updated else it }
        Result.Success(updated)
    }
}
