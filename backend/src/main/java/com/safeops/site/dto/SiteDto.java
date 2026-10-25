package com.safeops.site.dto;

import java.time.Instant;
import java.util.UUID;

public class SiteDto {
    private UUID id;
    private String name;
    private String slug;
    private String description;
    private String location;
    private boolean active;
    private UUID customerId;
    private UUID organizationId;
    private UUID projectId;
    private Instant createdAt;
    private Instant updatedAt;

    public SiteDto() {}

    public SiteDto(UUID id, String name, String slug, String description, String location, boolean active, UUID customerId, UUID organizationId, UUID projectId, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.location = location;
        this.active = active;
        this.customerId = customerId;
        this.organizationId = organizationId;
        this.projectId = projectId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getSlug() { return slug; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public boolean isActive() { return active; }
    public UUID getCustomerId() { return customerId; }
    public UUID getOrganizationId() { return organizationId; }
    public UUID getProjectId() { return projectId; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
