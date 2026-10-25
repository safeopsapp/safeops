package com.safeops.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class StaffRegistrationRequest {

    @NotBlank(message = "Referral code is required")
    @Size(min = 3, max = 32)
    private String referralCode;

    @NotBlank(message = "Full name is required")
    @Size(max = 255)
    private String fullName;

    @Size(max = 30)
    private String phoneNumber;

    @Size(max = 64)
    private String workerRole;

    @Size(max = 255)
    private String workLocation;

    public StaffRegistrationRequest() {}

    public String getReferralCode() { return referralCode; }
    public void setReferralCode(String referralCode) { this.referralCode = referralCode; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getWorkerRole() { return workerRole; }
    public void setWorkerRole(String workerRole) { this.workerRole = workerRole; }
    public String getWorkLocation() { return workLocation; }
    public void setWorkLocation(String workLocation) { this.workLocation = workLocation; }
}

