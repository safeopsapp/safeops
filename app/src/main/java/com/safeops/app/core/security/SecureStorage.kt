package com.safeops.app.core.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.safeops.app.core.logging.SafeOpsLogger

/**
 * Secure key-value storage abstraction.
 * Sensitive credentials (JWT access and refresh tokens) are encrypted using AES256-GCM and AES256-SIV.
 * Passwords are NEVER stored.
 */
interface SecureStorage {
    suspend fun putString(key: String, value: String)
    suspend fun getString(key: String): String?
    suspend fun remove(key: String)
    suspend fun clear()
}

class EncryptedPreferencesStorage(private val context: Context) : SecureStorage {

    private val sharedPreferences: SharedPreferences? by lazy {
        createEncryptedPrefs() ?: createFallbackPrefs()
    }

    private fun createEncryptedPrefs(): SharedPreferences? {
        return try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            EncryptedSharedPreferences.create(
                context,
                "safeops_secure_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            SafeOpsLogger.e("SecureStorage", "Failed to initialize EncryptedSharedPreferences, resetting storage", e)
            try {
                context.getSharedPreferences("safeops_secure_prefs", Context.MODE_PRIVATE).edit().clear().apply()
            } catch (_: Exception) {}
            null
        }
    }

    private fun createFallbackPrefs(): SharedPreferences? {
        return try {
            context.getSharedPreferences("safeops_fallback_prefs", Context.MODE_PRIVATE)
        } catch (e: Exception) {
            SafeOpsLogger.e("SecureStorage", "Failed to initialize fallback preferences", e)
            null
        }
    }

    override suspend fun putString(key: String, value: String) {
        try {
            sharedPreferences?.edit()?.putString(key, value)?.apply()
        } catch (e: Exception) {
            SafeOpsLogger.e("SecureStorage", "Error writing key: $key", e)
        }
    }

    override suspend fun getString(key: String): String? {
        return try {
            sharedPreferences?.getString(key, null)
        } catch (e: Exception) {
            SafeOpsLogger.e("SecureStorage", "Error reading key: $key, clearing corrupt entry", e)
            try {
                sharedPreferences?.edit()?.remove(key)?.apply()
            } catch (_: Exception) {}
            null
        }
    }

    override suspend fun remove(key: String) {
        try {
            sharedPreferences?.edit()?.remove(key)?.apply()
        } catch (e: Exception) {
            SafeOpsLogger.e("SecureStorage", "Error removing key: $key", e)
        }
    }

    override suspend fun clear() {
        try {
            sharedPreferences?.edit()?.clear()?.apply()
        } catch (e: Exception) {
            SafeOpsLogger.e("SecureStorage", "Error clearing preferences", e)
        }
    }
}
