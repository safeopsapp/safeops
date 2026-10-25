package com.safeops.common.exception;

public class ResourceNotFoundException extends SafeOpsException {
    public ResourceNotFoundException(String message) {
        super(message, "RESOURCE_NOT_FOUND");
    }
}
