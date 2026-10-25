package com.safeops.app.core.logging

import android.util.Log

/**
 * Security-focused Logger that automatically redacts sensitive credentials, tokens, and passwords.
 */
object SafeOpsLogger {
    private const val TAG = "SafeOps"

    private val SENSITIVE_PATTERNS = listOf(
        Regex("(?i)(password\\s*[:=]\\s*)([^\\s,;]+)"),
        Regex("(?i)(token\\s*[:=]\\s*)([^\\s,;]+)"),
        Regex("(?i)(authorization\\s*[:=]\\s*Bearer\\s+)([^\\s,;]+)"),
        Regex("(?i)(secret\\s*[:=]\\s*)([^\\s,;]+)")
    )

    fun d(tag: String = TAG, message: String) {
        log(Log.DEBUG, tag, message)
    }

    fun i(tag: String = TAG, message: String) {
        log(Log.INFO, tag, message)
    }

    fun w(tag: String = TAG, message: String, throwable: Throwable? = null) {
        log(Log.WARN, tag, message, throwable)
    }

    fun e(tag: String = TAG, message: String, throwable: Throwable? = null) {
        log(Log.ERROR, tag, message, throwable)
    }

    private fun log(priority: Int, tag: String, message: String, throwable: Throwable? = null) {
        val sanitized = sanitize(message)
        when (priority) {
            Log.DEBUG -> Log.d(tag, sanitized, throwable)
            Log.INFO -> Log.i(tag, sanitized, throwable)
            Log.WARN -> Log.w(tag, sanitized, throwable)
            Log.ERROR -> Log.e(tag, sanitized, throwable)
        }
    }

    fun sanitize(input: String): String {
        var sanitized = input
        for (pattern in SENSITIVE_PATTERNS) {
            sanitized = pattern.replace(sanitized, "$1[REDACTED]")
        }
        return sanitized
    }
}
