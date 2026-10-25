package com.safeops.app.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.safeops.app.core.common.Result
import com.safeops.app.core.error.ErrorMapper
import com.safeops.app.core.error.SafeOpsError
import com.safeops.app.core.utils.await
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
        try {
            val email = if (emailOrUserId.contains("@")) emailOrUserId.trim() else "${emailOrUserId.trim()}@safeops.app"
            val auth = firebaseAuthProvider()
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
                ?: return@withContext Result.Error(SafeOpsError.Unauthorized("Firebase authentication returned no user session."))

            val tokenResult = firebaseUser.getIdToken(true).await()
            val idToken = tokenResult.token
                ?: return@withContext Result.Error(SafeOpsError.Unauthorized("Failed to retrieve Firebase ID token."))

            // Call SafeOps backend /api/v1/auth/me
            val me = apiClient.getMe(idToken)

            val status = me.status?.uppercase() ?: "ACTIVE"
            when (status) {
                "PENDING" -> return@withContext Result.Error(
                    SafeOpsError.Unauthorized("Your account registration is pending administrator approval.")
                )
                "SUSPENDED" -> return@withContext Result.Error(
                    SafeOpsError.Unauthorized("Account is suspended. Please contact your organization administrator.")
                )
                "DEACTIVATED" -> return@withContext Result.Error(
                    SafeOpsError.Unauthorized("Account is deactivated.")
                )
            }

            val orgSummary = me.organizations.firstOrNull()
            val org = Organization(
                id = orgSummary?.id ?: "org_default",
                name = orgSummary?.name ?: "SafeOps Organization",
                code = orgSummary?.slug?.uppercase() ?: "SAFE",
                activeSitesCount = me.sites.size
            )

            val projectSummary = me.projects.firstOrNull()
            val project = projectSummary?.let {
                Project(
                    id = it.id,
                    code = it.code,
                    name = it.name,
                    location = "Site Location",
                    organizationId = it.organizationId
                )
            }

            val domainUser = User(
                id = me.id ?: firebaseUser.uid,
                email = me.email ?: email,
                fullName = me.displayName ?: firebaseUser.displayName ?: "SafeOps User",
                role = role,
                organizationId = org.id,
                isActive = (status == "ACTIVE")
            )

            val session = AuthenticatedSession(
                user = domainUser,
                organization = org,
                role = role,
                currentProject = project,
                permissions = Permission.defaultPermissionsForRole(role),
                accessToken = idToken,
                refreshToken = null,
                expiresAt = (tokenResult.expirationTimestamp * 1000L).coerceAtLeast(System.currentTimeMillis() + 3600000L)
            )

            Result.Success(session)
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Result.Error(SafeOpsError.Unauthorized("Invalid email or password."))
        } catch (e: FirebaseAuthInvalidUserException) {
            Result.Error(SafeOpsError.Unauthorized("No account found for this email address."))
        } catch (e: Exception) {
            Result.Error(ErrorMapper.mapThrowable(e))
        }
    }
}
