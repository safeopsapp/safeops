package com.safeops.site;

import com.safeops.common.audit.Auditable;
import com.safeops.customer.Customer;
import com.safeops.organization.Organization;
import com.safeops.project.Project;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "sites", indexes = {
    @Index(name = "idx_sites_slug", columnList = "slug", unique = true),
    @Index(name = "idx_sites_customer_id", columnList = "customer_id"),
    @Index(name = "idx_sites_organization_id", columnList = "organization_id"),
    @Index(name = "idx_sites_project_id", columnList = "project_id")
})
public class Site extends Auditable {

    @NotBlank
    @Size(max = 255)
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank
    @Size(max = 100)
    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @Size(max = 500)
    @Column(name = "description")
    private String description;

    @Size(max = 255)
    @Column(name = "location")
    private String location;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    protected Site() {}

    public Site(String name, String slug, Customer customer, Organization organization, Project project) {
        this.name = name;
        this.slug = slug;
        this.customer = customer;
        this.organization = organization;
        this.project = project;
    }

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Customer getCustomer() { return customer; }
    public Organization getOrganization() { return organization; }
    public Project getProject() { return project; }
}
