package com.safeops.permission;

import com.safeops.common.audit.Auditable;
import jakarta.persistence.*;

@Entity
@Table(name = "permissions")
public class Permission extends Auditable {

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, unique = true)
    private PermissionName name;

    @Column(name = "description")
    private String description;

    protected Permission() {}

    public Permission(PermissionName name, String description) {
        this.name = name;
        this.description = description;
    }

    public PermissionName getName() { return name; }
    public String getDescription() { return description; }
}
