package com.safeops.app.core.utils

object ValidationUtils {

    private val EMAIL_REGEX = Regex(
        "[a-zA-Z0-9+._%\\-]{1,256}" +
        "@" +
        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
        "(" +
        "\\." +
        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
        ")+"
    )

    fun isValidEmail(email: String): Boolean {
        if (email.isBlank()) return false
        return EMAIL_REGEX.matches(email.trim())
    }

    fun isValidUserId(userId: String): Boolean {
        return userId.trim().length >= 3
    }

    fun isValidEmailOrUserId(input: String): Boolean {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return false
        return if (trimmed.contains("@")) {
            isValidEmail(trimmed)
        } else {
            isValidUserId(trimmed)
        }
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }
}
