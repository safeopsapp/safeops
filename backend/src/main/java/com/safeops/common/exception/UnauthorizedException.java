package com.safeops.common.exception;

public class UnauthorizedException extends SafeOpsException {
    public UnauthorizedException(String message) {
        super(message, "UNAUTHORIZED");
    }
}
