package com.safeops.user;

import com.safeops.common.audit.Auditable;
import com.safeops.customer.Customer;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_users_firebase_uid", columnList = "firebase_uid", unique = true),
    @Index(name = "idx_users_email", columnList = "email", unique = true),
    @Index(name = "idx_users_customer_id", columnList = "customer_id"),
    @Index(name = "idx_users_status", columnList = "status")
})
public class User extends Auditable {

    @NotBlank
    @Size(max = 128)
    @Column(name = "firebase_uid", nullable = false, unique = true)
    private String firebaseUid;

    @Email
    @NotBlank
    @Size(max = 255)
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Size(max = 255)
    @Column(name = "display_name")
    private String displayName;

    @Size(max = 500)
    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    protected User() {}

    public User(String firebaseUid, String email) {
        this.firebaseUid = firebaseUid;
        this.email = email;
    }

    public String getFirebaseUid() { return firebaseUid; }
    public void setFirebaseUid(String firebaseUid) { this.firebaseUid = firebaseUid; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }
    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
}
