package com.safeops.auth.dto;

import java.util.UUID;

public class AdminRegistrationResponse {
    private final UUID customerId;
    private final UUID organizationId;
    private final String orgId;
    private final String referralCode;
    private final String organizationName;
    private final String adminName;

    public AdminRegistrationResponse(
            UUID customerId,
            UUID organizationId,
            String orgId,
            String referralCode,
            String organizationName,
            String adminName
    ) {
        this.customerId = customerId;
        this.organizationId = organizationId;
        this.orgId = orgId;
        this.referralCode = referralCode;
        this.organizationName = organizationName;
        this.adminName = adminName;
    }

    public UUID getCustomerId() { return customerId; }
    public UUID getOrganizationId() { return organizationId; }
    public String getOrgId() { return orgId; }
    public String getReferralCode() { return referralCode; }
    public String getOrganizationName() { return organizationName; }
    public String getAdminName() { return adminName; }
}

