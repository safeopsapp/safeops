package com.safeops.role;

import com.safeops.common.audit.Auditable;
import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role extends Auditable {

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, unique = true)
    private RoleName name;

    @Column(name = "description")
    private String description;

    protected Role() {}

    public Role(RoleName name, String description) {
        this.name = name;
        this.description = description;
    }

    public RoleName getName() { return name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
