package com.safeops.common.exception;

public class ValidationException extends SafeOpsException {
    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR");
    }
}
