package com.safeops.organization.dto;

import java.time.Instant;
import java.util.UUID;

public class OrganizationDto {
    private UUID id;
    private String name;
    private String slug;
    private String description;
    private boolean active;
    private UUID customerId;
    private Instant createdAt;
    private Instant updatedAt;

    public OrganizationDto() {}

    public OrganizationDto(UUID id, String name, String slug, String description, boolean active, UUID customerId, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.active = active;
        this.customerId = customerId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getSlug() { return slug; }
    public String getDescription() { return description; }
    public boolean isActive() { return active; }
    public UUID getCustomerId() { return customerId; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
