package com.safeops.membership;

import com.safeops.common.audit.Auditable;
import com.safeops.organization.Organization;
import com.safeops.role.Role;
import com.safeops.user.User;
import jakarta.persistence.*;

@Entity
@Table(name = "organization_memberships", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "organization_id"})
}, indexes = {
    @Index(name = "idx_org_memberships_user_id", columnList = "user_id"),
    @Index(name = "idx_org_memberships_organization_id", columnList = "organization_id"),
    @Index(name = "idx_org_memberships_status", columnList = "status")
})
public class OrganizationMembership extends Auditable {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MembershipStatus status = MembershipStatus.ACTIVE;

    @Column(name = "worker_role", length = 64)
    private String workerRole;

    @Column(name = "work_location", length = 255)
    private String workLocation;

    protected OrganizationMembership() {}

    public OrganizationMembership(User user, Organization organization, Role role) {
        this(user, organization, role, MembershipStatus.ACTIVE);
    }

    public OrganizationMembership(User user, Organization organization, Role role, MembershipStatus status) {
        this.user = user;
        this.organization = organization;
        this.role = role;
        this.status = status;
        this.active = (status == MembershipStatus.ACTIVE);
    }

    public User getUser() { return user; }
    public Organization getOrganization() { return organization; }
    public Role getRole() { return role; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public MembershipStatus getStatus() { return status; }
    public void setStatus(MembershipStatus status) {
        this.status = status;
        this.active = (status == MembershipStatus.ACTIVE);
    }
    public String getWorkerRole() { return workerRole; }
    public void setWorkerRole(String workerRole) { this.workerRole = workerRole; }
    public String getWorkLocation() { return workLocation; }
    public void setWorkLocation(String workLocation) { this.workLocation = workLocation; }
}
