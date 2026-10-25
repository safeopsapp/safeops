package com.safeops.security;

import com.safeops.permission.PermissionName;
import com.safeops.role.RoleName;
import com.safeops.user.UserStatus;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class SafeOpsPrincipal {

    private final String firebaseUid;
    private final UUID userId;
    private final String email;
    private final String displayName;
    private final UUID customerId;
    private final UserStatus status;
    private final List<OrganizationContext> organizations;
    private final List<ProjectContext> projects;
    private final List<SiteContext> sites;
    private final Set<RoleName> roles;
    private final Set<PermissionName> permissions;

    private SafeOpsPrincipal(Builder builder) {
        this.firebaseUid = builder.firebaseUid;
        this.userId = builder.userId;
        this.email = builder.email;
        this.displayName = builder.displayName;
        this.customerId = builder.customerId;
        this.status = builder.status != null ? builder.status : UserStatus.ACTIVE;
        this.organizations = Collections.unmodifiableList(builder.organizations);
        this.projects = Collections.unmodifiableList(builder.projects);
        this.sites = Collections.unmodifiableList(builder.sites);
        this.roles = Collections.unmodifiableSet(builder.roles);
        this.permissions = Collections.unmodifiableSet(builder.permissions);
    }

    public String getFirebaseUid() { return firebaseUid; }
    public UUID getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getDisplayName() { return displayName; }
    public UUID getCustomerId() { return customerId; }
    public UserStatus getStatus() { return status; }
    public List<OrganizationContext> getOrganizations() { return organizations; }
    public List<ProjectContext> getProjects() { return projects; }
    public List<SiteContext> getSites() { return sites; }
    public Set<RoleName> getRoles() { return roles; }
    public Set<PermissionName> getPermissions() { return permissions; }

    public boolean hasPermission(PermissionName permission) {
        return permissions.contains(permission);
    }

    public boolean hasRole(RoleName role) {
        return roles.contains(role);
    }

    public boolean belongsToOrganization(UUID organizationId) {
        return organizations.stream()
            .anyMatch(org -> org.organizationId().equals(organizationId));
    }

    public boolean belongsToProject(UUID projectId) {
        return projects.stream()
            .anyMatch(proj -> proj.projectId().equals(projectId));
    }

    public boolean belongsToSite(UUID siteId) {
        return sites.stream()
            .anyMatch(site -> site.siteId().equals(siteId));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String firebaseUid;
        private UUID userId;
        private String email;
        private String displayName;
        private UUID customerId;
        private UserStatus status;
        private List<OrganizationContext> organizations = new java.util.ArrayList<>();
        private List<ProjectContext> projects = new java.util.ArrayList<>();
        private List<SiteContext> sites = new java.util.ArrayList<>();
        private Set<RoleName> roles = new java.util.HashSet<>();
        private Set<PermissionName> permissions = new java.util.HashSet<>();

        public Builder firebaseUid(String firebaseUid) { this.firebaseUid = firebaseUid; return this; }
        public Builder userId(UUID userId) { this.userId = userId; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder displayName(String displayName) { this.displayName = displayName; return this; }
        public Builder customerId(UUID customerId) { this.customerId = customerId; return this; }
        public Builder status(UserStatus status) { this.status = status; return this; }
        public Builder organizations(List<OrganizationContext> organizations) { this.organizations = organizations; return this; }
        public Builder projects(List<ProjectContext> projects) { this.projects = projects; return this; }
        public Builder sites(List<SiteContext> sites) { this.sites = sites; return this; }
        public Builder roles(Set<RoleName> roles) { this.roles = roles; return this; }
        public Builder permissions(Set<PermissionName> permissions) { this.permissions = permissions; return this; }
        public SafeOpsPrincipal build() { return new SafeOpsPrincipal(this); }
    }

    // Inner record classes for membership context
    public record OrganizationContext(UUID organizationId, String organizationName, RoleName role) {}
    public record ProjectContext(UUID projectId, String projectName, RoleName role) {}
    public record SiteContext(UUID siteId, String siteName, RoleName role) {}
}
