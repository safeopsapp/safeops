package com.safeops.auth.dto;

import java.time.Instant;
import java.util.UUID;

public class PendingMembershipResponse {
    private final UUID membershipId;
    private final UUID userId;
    private final String fullName;
    private final String email;
    private final String role;
    private final String workerRole;
    private final String workLocation;
    private final String status;
    private final Instant requestedAt;

    public PendingMembershipResponse(
            UUID membershipId,
            UUID userId,
            String fullName,
            String email,
            String role,
            String workerRole,
            String workLocation,
            String status,
            Instant requestedAt
    ) {
        this.membershipId = membershipId;
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.workerRole = workerRole;
        this.workLocation = workLocation;
        this.status = status;
        this.requestedAt = requestedAt;
    }

    public UUID getMembershipId() { return membershipId; }
    public UUID getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getWorkerRole() { return workerRole; }
    public String getWorkLocation() { return workLocation; }
    public String getStatus() { return status; }
    public Instant getRequestedAt() { return requestedAt; }
}

