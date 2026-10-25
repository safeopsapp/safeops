package com.safeops.app.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.safeops.app.core.common.Result
import com.safeops.app.core.error.ErrorMapper
import com.safeops.app.core.error.SafeOpsError
import com.safeops.app.core.utils.await
import com.safeops.app.data.remote.dto.AuthMeDto
import com.safeops.app.domain.model.AuthenticatedSession
import com.safeops.app.domain.model.Organization
import com.safeops.app.domain.model.Permission
import com.safeops.app.domain.model.Project
import com.safeops.app.domain.model.Role
import com.safeops.app.domain.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface AuthRemoteDataSource {
    suspend fun authenticate(
        emailOrUserId: String,
        password: String,
        role: Role
    ): Result<AuthenticatedSession>

    suspend fun authenticateWithGoogle(
        googleIdToken: String,
        role: Role
    ): Result<AuthenticatedSession>
}

class AuthRemoteDataSourceImpl(
    private val firebaseAuthProvider: () -> FirebaseAuth = { FirebaseAuth.getInstance() },
    private val apiClient: SafeOpsApiClient = SafeOpsApiClient()
) : AuthRemoteDataSource {

    override suspend fun authenticate(
        emailOrUserId: String,
        password: String,
        role: Role
    ): Result<AuthenticatedSession> = withContext(Dispatchers.IO) {
        val auth = firebaseAuthProvider()
        try {
            val email = if (emailOrUserId.contains("@")) emailOrUserId.trim() else "${emailOrUserId.trim()}@safeops.app"
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
                ?: return@withContext Result.Error(SafeOpsError.Unauthorized("Firebase authentication returned no user session."))

            val tokenResult = firebaseUser.getIdToken(true).await()
            val idToken = tokenResult.token
                ?: return@withContext Result.Error(SafeOpsError.Unauthorized("Failed to retrieve Firebase ID token."))

            // Validate against backend /api/v1/auth/me
            val me = try {
                apiClient.getMe(idToken)
            } catch (e: Exception) {
                auth.signOut()
                return@withContext Result.Error(
                    SafeOpsError.Unauthorized("Account does not exist. Please register as an Admin or join with a referral code first.")
                )
            }

            mapMeDtoToSession(auth, me, firebaseUser.uid, idToken, tokenResult.expirationTimestamp, role)
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Result.Error(SafeOpsError.Unauthorized("Invalid email or password."))
        } catch (e: FirebaseAuthInvalidUserException) {
            Result.Error(SafeOpsError.Unauthorized("Account does not exist. Please register as an Admin or join with a referral code first."))
        } catch (e: Exception) {
            auth.signOut()
            Result.Error(ErrorMapper.mapThrowable(e))
        }
    }

    override suspend fun authenticateWithGoogle(
        googleIdToken: String,
        role: Role
    ): Result<AuthenticatedSession> = withContext(Dispatchers.IO) {
        val auth = firebaseAuthProvider()
        try {
            val credential = GoogleAuthProvider.getCredential(googleIdToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user
                ?: return@withContext Result.Error(SafeOpsError.Unauthorized("Google authentication returned no user session."))

            val tokenResult = firebaseUser.getIdToken(true).await()
            val idToken = tokenResult.token
                ?: return@withContext Result.Error(SafeOpsError.Unauthorized("Failed to retrieve authorization token."))

            // Validate authorization token & check if account exists in SafeOps database
            val me = try {
                apiClient.getMe(idToken)
            } catch (e: Exception) {
                auth.signOut()
                return@withContext Result.Error(
                    SafeOpsError.Unauthorized("Account does not exist. Please register as an Admin or join with a referral code first.")
                )
            }

            mapMeDtoToSession(auth, me, firebaseUser.uid, idToken, tokenResult.expirationTimestamp, role)
        } catch (e: Exception) {
            auth.signOut()
            Result.Error(ErrorMapper.mapThrowable(e))
        }
    }

    private fun mapMeDtoToSession(
        auth: FirebaseAuth,
        me: AuthMeDto,
        firebaseUid: String,
        idToken: String,
        expirationTimestampSeconds: Long,
        fallbackRole: Role
    ): Result<AuthenticatedSession> {
        val status = me.status?.uppercase() ?: "ACTIVE"
        when (status) {
            "PENDING" -> {
                auth.signOut()
                return Result.Error(
                    SafeOpsError.Unauthorized("Your account registration is pending administrator approval.")
                )
            }
            "SUSPENDED" -> {
                auth.signOut()
                return Result.Error(
                    SafeOpsError.Unauthorized("Account is suspended. Please contact your organization administrator.")
                )
            }
            "DEACTIVATED" -> {
                auth.signOut()
                return Result.Error(
                    SafeOpsError.Unauthorized("Account is deactivated.")
                )
            }
        }

        // Determine effective role from verified backend permissions
        val effectiveRole = when {
            me.roles.contains("ADMIN") || me.roles.contains("ROLE_ADMIN") -> Role.ADMIN
            me.roles.contains("SAFETY_OFFICER") || me.roles.contains("ROLE_SAFETY_OFFICER") || me.roles.contains("EHS_OFFICER") -> Role.SAFETY_OFFICER
            me.roles.contains("ENGINEER") || me.roles.contains("ROLE_ENGINEER") -> Role.ENGINEER
            me.roles.contains("WORKER") || me.roles.contains("ROLE_WORKER") -> Role.WORKER
            else -> fallbackRole
        }

        val orgSummary = me.organizations.firstOrNull()
        val org = Organization(
            id = orgSummary?.effectiveId?.ifBlank { null } ?: "org_default",
            name = orgSummary?.name?.ifBlank { null } ?: "SafeOps Organization",
            code = orgSummary?.slug?.uppercase() ?: "SAFE",
            activeSitesCount = me.sites.size
        )

        val projectSummary = me.projects.firstOrNull()
        val project = projectSummary?.let {
            Project(
                id = it.effectiveId,
                code = it.code ?: "PRJ",
                name = it.name.ifBlank { "Construction Site Project" },
                location = "Site Location",
                organizationId = it.organizationId ?: org.id
            )
        }

        val domainUser = User(
            id = me.effectiveId.ifBlank { firebaseUid },
            email = me.email ?: "",
            fullName = me.displayName?.ifBlank { null } ?: "SafeOps User",
            role = effectiveRole,
            organizationId = org.id,
            isActive = (status == "ACTIVE")
        )

        val session = AuthenticatedSession(
            user = domainUser,
            organization = org,
            role = effectiveRole,
            currentProject = project,
            permissions = Permission.defaultPermissionsForRole(effectiveRole),
            accessToken = idToken,
            refreshToken = null,
            expiresAt = (expirationTimestampSeconds * 1000L).coerceAtLeast(System.currentTimeMillis() + 3600000L)
        )

        return Result.Success(session)
    }
}
