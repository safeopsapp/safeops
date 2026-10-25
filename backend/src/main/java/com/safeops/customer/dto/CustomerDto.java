package com.safeops.customer.dto;

import java.time.Instant;
import java.util.UUID;

public class CustomerDto {
    private UUID id;
    private String name;
    private String slug;
    private String description;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;

    // private constructor, use builder or all-args constructor
    public CustomerDto() {}

    public CustomerDto(UUID id, String name, String slug, String description, boolean active, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // getters
    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getSlug() { return slug; }
    public String getDescription() { return description; }
    public boolean isActive() { return active; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
