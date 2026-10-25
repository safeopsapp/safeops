package com.safeops.app.core.security

/**
 * Token manager handling secure storage and retrieval of authentication tokens.
 */
interface TokenManager {
    suspend fun saveTokens(accessToken: String, refreshToken: String?)
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun clearTokens()
}

class TokenManagerImpl(private val secureStorage: SecureStorage) : TokenManager {

    companion object {
        private const val KEY_ACCESS_TOKEN = "sec_access_token"
        private const val KEY_REFRESH_TOKEN = "sec_refresh_token"
    }

    override suspend fun saveTokens(accessToken: String, refreshToken: String?) {
        secureStorage.putString(KEY_ACCESS_TOKEN, accessToken)
        if (refreshToken != null) {
            secureStorage.putString(KEY_REFRESH_TOKEN, refreshToken)
        } else {
            secureStorage.remove(KEY_REFRESH_TOKEN)
        }
    }

    override suspend fun getAccessToken(): String? {
        return secureStorage.getString(KEY_ACCESS_TOKEN)
    }

    override suspend fun getRefreshToken(): String? {
        return secureStorage.getString(KEY_REFRESH_TOKEN)
    }

    override suspend fun clearTokens() {
        secureStorage.remove(KEY_ACCESS_TOKEN)
        secureStorage.remove(KEY_REFRESH_TOKEN)
    }
}
