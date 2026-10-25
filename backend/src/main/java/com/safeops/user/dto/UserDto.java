package com.safeops.user.dto;

import java.time.Instant;
import java.util.UUID;

public class UserDto {
    private UUID id;
    private String firebaseUid;
    private String email;
    private String displayName;
    private String photoUrl;
    private boolean active;
    private boolean emailVerified;
    private UUID customerId;
    private Instant createdAt;
    private Instant updatedAt;

    public UserDto() {}

    public UserDto(UUID id, String firebaseUid, String email, String displayName, String photoUrl, boolean active, boolean emailVerified, UUID customerId, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.firebaseUid = firebaseUid;
        this.email = email;
        this.displayName = displayName;
        this.photoUrl = photoUrl;
        this.active = active;
        this.emailVerified = emailVerified;
        this.customerId = customerId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public String getFirebaseUid() { return firebaseUid; }
    public String getEmail() { return email; }
    public String getDisplayName() { return displayName; }
    public String getPhotoUrl() { return photoUrl; }
    public boolean isActive() { return active; }
    public boolean isEmailVerified() { return emailVerified; }
    public UUID getCustomerId() { return customerId; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
