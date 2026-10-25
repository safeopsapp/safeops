package com.safeops.membership;

import com.safeops.common.audit.Auditable;
import com.safeops.project.Project;
import com.safeops.role.Role;
import com.safeops.user.User;
import jakarta.persistence.*;

@Entity
@Table(name = "project_memberships", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "project_id"})
}, indexes = {
    @Index(name = "idx_project_memberships_user_id", columnList = "user_id"),
    @Index(name = "idx_project_memberships_project_id", columnList = "project_id")
})
public class ProjectMembership extends Auditable {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    protected ProjectMembership() {}

    public ProjectMembership(User user, Project project, Role role) {
        this.user = user;
        this.project = project;
        this.role = role;
    }

    public User getUser() { return user; }
    public Project getProject() { return project; }
    public Role getRole() { return role; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
