package com.safeops.app.core.network

import android.os.Build
import com.safeops.app.BuildConfig

internal fun resolveDevelopmentUrl(): String {
    val isEmulator = try {
        (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.PRODUCT.contains("sdk_gphone"))
    } catch (_: Throwable) {
        false
    }

    return if (isEmulator) {
        "http://10.0.2.2:8080/api/v1/"
    } else {
        // Physical device connected via USB (adb reverse tcp:8080 tcp:8080)
        "http://127.0.0.1:8080/api/v1/"
    }
}

/**
 * Environment configuration for SafeOps.
 * Prevents hardcoding URLs in UI or feature modules.
 */
enum class SafeOpsEnvironment(
    val baseUrl: String,
    val isLoggingEnabled: Boolean
) {
    DEVELOPMENT(
        baseUrl = resolveDevelopmentUrl(),
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
