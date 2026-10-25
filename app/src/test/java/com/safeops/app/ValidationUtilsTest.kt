package com.safeops.app

import com.safeops.app.core.utils.ValidationUtils
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidationUtilsTest {

    @Test
    fun `isValidEmail validates emails properly`() {
        assertTrue(ValidationUtils.isValidEmail("john.doe@safeops.app"))
        assertTrue(ValidationUtils.isValidEmail("user+tag@domain.co"))
        assertFalse(ValidationUtils.isValidEmail("plainaddress"))
        assertFalse(ValidationUtils.isValidEmail(""))
    }

    @Test
    fun `isValidUserId validates alphanumeric ids`() {
        assertTrue(ValidationUtils.isValidUserId("USR-1029"))
        assertTrue(ValidationUtils.isValidUserId("admin1"))
        assertFalse(ValidationUtils.isValidUserId("ab"))
        assertFalse(ValidationUtils.isValidUserId("  "))
    }

    @Test
    fun `isValidPassword validates minimum 6 characters`() {
        assertTrue(ValidationUtils.isValidPassword("secret123"))
        assertTrue(ValidationUtils.isValidPassword("123456"))
        assertFalse(ValidationUtils.isValidPassword("12345"))
        assertFalse(ValidationUtils.isValidPassword(""))
    }
}
