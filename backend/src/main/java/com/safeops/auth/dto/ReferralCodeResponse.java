package com.safeops.auth.dto;

import java.util.UUID;

public class ReferralCodeResponse {
    private final boolean valid;
    private final UUID organizationId;
    private final String orgId;
    private final String organizationName;
    private final String errorMessage;

    public ReferralCodeResponse(boolean valid, UUID organizationId, String orgId, String organizationName, String errorMessage) {
        this.valid = valid;
        this.organizationId = organizationId;
        this.orgId = orgId;
        this.organizationName = organizationName;
        this.errorMessage = errorMessage;
    }

    public static ReferralCodeResponse success(UUID organizationId, String orgId, String organizationName) {
        return new ReferralCodeResponse(true, organizationId, orgId, organizationName, null);
    }

    public static ReferralCodeResponse invalid(String message) {
        return new ReferralCodeResponse(false, null, null, null, message);
    }

    public boolean isValid() { return valid; }
    public UUID getOrganizationId() { return organizationId; }
    public String getOrgId() { return orgId; }
    public String getOrganizationName() { return organizationName; }
    public String getErrorMessage() { return errorMessage; }
}

