package com.safeops.organization;

import com.safeops.common.audit.Auditable;
import com.safeops.customer.Customer;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "organizations", indexes = {
    @Index(name = "idx_organizations_slug", columnList = "slug", unique = true),
    @Index(name = "idx_organizations_referral_code", columnList = "referral_code", unique = true),
    @Index(name = "idx_organizations_customer_id", columnList = "customer_id")
})
public class Organization extends Auditable {

    @NotBlank
    @Size(max = 255)
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank
    @Size(max = 100)
    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @NotBlank
    @Size(max = 32)
    @Column(name = "referral_code", nullable = false, unique = true)
    private String referralCode;

    @Size(max = 500)
    @Column(name = "description")
    private String description;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    protected Organization() {}

    public Organization(String name, String slug, Customer customer) {
        this.name = name;
        this.slug = slug;
        this.referralCode = slug != null ? slug.toUpperCase() : "";
        this.customer = customer;
    }

    public Organization(String name, String slug, String referralCode, Customer customer) {
        this.name = name;
        this.slug = slug;
        this.referralCode = referralCode;
        this.customer = customer;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getReferralCode() { return referralCode; }
    public void setReferralCode(String referralCode) { this.referralCode = referralCode; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
}
