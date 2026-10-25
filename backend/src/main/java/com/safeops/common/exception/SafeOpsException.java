package com.safeops.common.exception;

public class SafeOpsException extends RuntimeException {
    private final String code;

    public SafeOpsException(String message) {
        super(message);
        this.code = "INTERNAL_ERROR";
    }

    public SafeOpsException(String message, String code) {
        super(message);
        this.code = code;
    }

    public SafeOpsException(String message, Throwable cause, String code) {
        super(message, cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
