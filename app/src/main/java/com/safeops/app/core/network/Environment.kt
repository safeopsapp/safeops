package com.safeops.app.core.network

import com.safeops.app.BuildConfig

/**
 * Environment configuration for SafeOps.
 * Prevents hardcoding URLs in UI or feature modules.
 */
enum class SafeOpsEnvironment(
    val baseUrl: String,
    val isLoggingEnabled: Boolean
) {
    DEVELOPMENT(
        baseUrl = "http://10.0.2.2:8080/api/v1/",
        isLoggingEnabled = true
    ),
    STAGING(
        baseUrl = "https://staging-api.safeops.app/api/v1/",
        isLoggingEnabled = true
    ),
    PRODUCTION(
        baseUrl = "https://api.safeops.app/api/v1/",
        isLoggingEnabled = false
    );

    companion object {
        val current: SafeOpsEnvironment = if (BuildConfig.DEBUG) DEVELOPMENT else PRODUCTION
        val activeBaseUrl: String = try {
            BuildConfig.BASE_URL.ifBlank { current.baseUrl }
        } catch (_: Throwable) {
            current.baseUrl
        }
    }
}

data class NetworkConfig(
    val baseUrl: String = SafeOpsEnvironment.activeBaseUrl,
    val connectTimeoutSeconds: Long = 30L,
    val readTimeoutSeconds: Long = 30L,
    val writeTimeoutSeconds: Long = 30L,
    val isLoggingEnabled: Boolean = BuildConfig.DEBUG
)
