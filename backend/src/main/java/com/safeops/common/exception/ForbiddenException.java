package com.safeops.common.exception;

public class ForbiddenException extends SafeOpsException {
    public ForbiddenException(String message) {
        super(message, "FORBIDDEN");
    }
}
