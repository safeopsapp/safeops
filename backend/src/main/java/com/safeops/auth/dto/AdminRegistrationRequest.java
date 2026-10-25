package com.safeops.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AdminRegistrationRequest {

    @NotBlank(message = "Full name is required")
    @Size(max = 255)
    private String fullName;

    @Size(max = 30)
    private String phoneNumber;

    @NotBlank(message = "Organization name is required")
    @Size(max = 255)
    private String organizationName;

    @NotBlank(message = "Organization ID is required")
    @Size(min = 3, max = 32)
    private String organizationId;

    @Size(max = 100)
    private String industryType;

    @Size(max = 255)
    private String address;

    @Size(max = 30)
    private String contactNumber;

    @Size(max = 255)
    private String website;

    public AdminRegistrationRequest() {}

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getOrganizationName() { return organizationName; }
    public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }
    public String getOrganizationId() { return organizationId; }
    public void setOrganizationId(String organizationId) { this.organizationId = organizationId; }
    public String getIndustryType() { return industryType; }
    public void setIndustryType(String industryType) { this.industryType = industryType; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
}

