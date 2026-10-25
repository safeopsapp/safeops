package com.safeops.app

import com.safeops.app.core.error.ErrorMapper
import com.safeops.app.core.error.SafeOpsError
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ErrorMapperTest {

    @Test
    fun `UnknownHostException maps to NetworkUnavailable error`() {
        val error = ErrorMapper.mapThrowable(UnknownHostException("Unable to resolve host"))
        assertTrue(error is SafeOpsError.NetworkUnavailable)
    }

    @Test
    fun `SocketTimeoutException maps to Timeout error`() {
        val error = ErrorMapper.mapThrowable(SocketTimeoutException("Timeout"))
        assertTrue(error is SafeOpsError.Timeout)
    }

    @Test
    fun `User friendly messages are properly extracted`() {
        val networkError = SafeOpsError.NetworkUnavailable("Custom network message")
        assertEquals("Custom network message", ErrorMapper.getUserFriendlyMessage(networkError))

        val validationError = SafeOpsError.ValidationError("Email", "Invalid format")
        assertEquals("Invalid format", ErrorMapper.getUserFriendlyMessage(validationError))
    }
}
