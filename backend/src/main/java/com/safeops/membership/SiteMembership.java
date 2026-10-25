package com.safeops.membership;

import com.safeops.common.audit.Auditable;
import com.safeops.role.Role;
import com.safeops.site.Site;
import com.safeops.user.User;
import jakarta.persistence.*;

@Entity
@Table(name = "site_memberships", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "site_id"})
}, indexes = {
    @Index(name = "idx_site_memberships_user_id", columnList = "user_id"),
    @Index(name = "idx_site_memberships_site_id", columnList = "site_id")
})
public class SiteMembership extends Auditable {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    protected SiteMembership() {}

    public SiteMembership(User user, Site site, Role role) {
        this.user = user;
        this.site = site;
        this.role = role;
    }

    public User getUser() { return user; }
    public Site getSite() { return site; }
    public Role getRole() { return role; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
