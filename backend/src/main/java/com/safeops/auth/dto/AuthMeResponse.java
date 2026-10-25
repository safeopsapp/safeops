package com.safeops.auth.dto;

import com.safeops.permission.PermissionName;
import com.safeops.role.RoleName;
import com.safeops.security.SafeOpsPrincipal;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class AuthMeResponse {

    private final UUID userId;
    private final String firebaseUid;
    private final String email;
    private final String displayName;
    private final UUID customerId;
    private final String status;
    private final Set<String> roles;
    private final Set<String> permissions;
    private final List<OrganizationSummary> organizations;
    private final List<ProjectSummary> projects;
    private final List<SiteSummary> sites;

    private AuthMeResponse(
            UUID userId, String firebaseUid, String email, String displayName,
            UUID customerId, String status, Set<String> roles, Set<String> permissions,
            List<OrganizationSummary> organizations, List<ProjectSummary> projects, List<SiteSummary> sites
    ) {
        this.userId = userId;
        this.firebaseUid = firebaseUid;
        this.email = email;
        this.displayName = displayName;
        this.customerId = customerId;
        this.status = status;
        this.roles = roles;
        this.permissions = permissions;
        this.organizations = organizations;
        this.projects = projects;
        this.sites = sites;
    }

    public static AuthMeResponse from(SafeOpsPrincipal principal) {
        return new AuthMeResponse(
            principal.getUserId(),
            principal.getFirebaseUid(),
            principal.getEmail(),
            principal.getDisplayName(),
            principal.getCustomerId(),
            principal.getStatus() != null ? principal.getStatus().name() : "ACTIVE",
            principal.getRoles().stream().map(RoleName::name).collect(java.util.stream.Collectors.toSet()),
            principal.getPermissions().stream().map(PermissionName::name).collect(java.util.stream.Collectors.toSet()),
            principal.getOrganizations().stream()
                .map(o -> new OrganizationSummary(o.organizationId(), o.organizationName(), o.role().name()))
                .collect(Collectors.toList()),
            principal.getProjects().stream()
                .map(p -> new ProjectSummary(p.projectId(), p.projectName(), p.role().name()))
                .collect(Collectors.toList()),
            principal.getSites().stream()
                .map(s -> new SiteSummary(s.siteId(), s.siteName(), s.role().name()))
                .collect(Collectors.toList())
        );
    }

    public UUID getUserId() { return userId; }
    public String getFirebaseUid() { return firebaseUid; }
    public String getEmail() { return email; }
    public String getDisplayName() { return displayName; }
    public UUID getCustomerId() { return customerId; }
    public String getStatus() { return status; }
    public Set<String> getRoles() { return roles; }
    public Set<String> getPermissions() { return permissions; }
    public List<OrganizationSummary> getOrganizations() { return organizations; }
    public List<ProjectSummary> getProjects() { return projects; }
    public List<SiteSummary> getSites() { return sites; }

    public record OrganizationSummary(UUID organizationId, String name, String role) {}
    public record ProjectSummary(UUID projectId, String name, String role) {}
    public record SiteSummary(UUID siteId, String name, String role) {}
}
