package com.safeops.auth;

import com.google.firebase.auth.FirebaseToken;
import com.safeops.auth.dto.*;
import com.safeops.common.exception.UnauthorizedException;
import com.safeops.common.response.ApiResponse;
import com.safeops.security.FirebaseTokenVerifier;
import com.safeops.security.SafeOpsUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication & Onboarding", description = "Authentication, registration, referral validation, and membership approval endpoints")
public class AuthController {

    private final AuthService authService;
    private final FirebaseTokenVerifier tokenVerifier;

    public AuthController(AuthService authService, FirebaseTokenVerifier tokenVerifier) {
        this.authService = authService;
        this.tokenVerifier = tokenVerifier;
    }

    @GetMapping("/me")
    @Operation(summary = "Get authenticated user context", security = @SecurityRequirement(name = "BearerAuth"))
    public ResponseEntity<ApiResponse<AuthMeResponse>> me(
            @AuthenticationPrincipal SafeOpsUserDetails userDetails
    ) {
        if (userDetails == null) {
            throw new UnauthorizedException("Not authenticated");
        }
        AuthMeResponse response = AuthMeResponse.from(userDetails.getPrincipal());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/sync")
    @Operation(summary = "Sync Firebase user to SafeOps", description = "Updates user record from verified Firebase token for an active registered member")
    public ResponseEntity<ApiResponse<AuthMeResponse>> sync(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        FirebaseToken firebaseToken = extractAndVerifyToken(authorizationHeader);
        var principal = authService.syncAndBuildPrincipal(firebaseToken);
        return ResponseEntity.ok(ApiResponse.success("User synchronized", AuthMeResponse.from(principal)));
    }

    @GetMapping("/referral/{code}")
    @Operation(summary = "Validate organization referral code", description = "Public endpoint to check if an organization referral code is valid and active")
    public ResponseEntity<ApiResponse<ReferralCodeResponse>> validateReferral(
            @PathVariable("code") String code
    ) {
        ReferralCodeResponse result = authService.validateReferralCode(code);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/register-admin")
    @Operation(summary = "Register organization administrator", description = "Onboards an organization and assigns the caller as initial ADMIN")
    public ResponseEntity<ApiResponse<AdminRegistrationResponse>> registerAdmin(
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody AdminRegistrationRequest request
    ) {
        FirebaseToken firebaseToken = extractAndVerifyToken(authorizationHeader);
        AdminRegistrationResponse response = authService.registerAdmin(firebaseToken, request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Organization and administrator registered successfully", response));
    }

    @PostMapping("/register-staff")
    @Operation(summary = "Register staff member with referral code", description = "Creates a staff account under an organization with PENDING membership status")
    public ResponseEntity<ApiResponse<String>> registerStaff(
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody StaffRegistrationRequest request
    ) {
        FirebaseToken firebaseToken = extractAndVerifyToken(authorizationHeader);
        authService.registerStaff(firebaseToken, request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Registration submitted successfully. Awaiting organization administrator approval.", "PENDING_APPROVAL"));
    }

    @GetMapping("/organizations/{orgId}/memberships/pending")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List pending membership requests", security = @SecurityRequirement(name = "BearerAuth"))
    public ResponseEntity<ApiResponse<List<PendingMembershipResponse>>> getPendingMemberships(
            @PathVariable("orgId") UUID orgId,
            @AuthenticationPrincipal SafeOpsUserDetails userDetails
    ) {
        if (userDetails == null) {
            throw new UnauthorizedException("Not authenticated");
        }
        List<PendingMembershipResponse> pending = authService.getPendingMemberships(orgId, userDetails.getPrincipal());
        return ResponseEntity.ok(ApiResponse.success(pending));
    }

    @PutMapping("/organizations/{orgId}/memberships/{membershipId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Approve staff membership request", security = @SecurityRequirement(name = "BearerAuth"))
    public ResponseEntity<ApiResponse<String>> approveMembership(
            @PathVariable("orgId") UUID orgId,
            @PathVariable("membershipId") UUID membershipId,
            @AuthenticationPrincipal SafeOpsUserDetails userDetails
    ) {
        if (userDetails == null) {
            throw new UnauthorizedException("Not authenticated");
        }
        authService.approveMembership(orgId, membershipId, userDetails.getPrincipal());
        return ResponseEntity.ok(ApiResponse.success("Staff membership approved and activated"));
    }

    @PutMapping("/organizations/{orgId}/memberships/{membershipId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reject staff membership request", security = @SecurityRequirement(name = "BearerAuth"))
    public ResponseEntity<ApiResponse<String>> rejectMembership(
            @PathVariable("orgId") UUID orgId,
            @PathVariable("membershipId") UUID membershipId,
            @AuthenticationPrincipal SafeOpsUserDetails userDetails
    ) {
        if (userDetails == null) {
            throw new UnauthorizedException("Not authenticated");
        }
        authService.rejectMembership(orgId, membershipId, userDetails.getPrincipal());
        return ResponseEntity.ok(ApiResponse.success("Staff membership request rejected"));
    }

    private FirebaseToken extractAndVerifyToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Valid Bearer ID token is required in Authorization header");
        }
        String idToken = authorizationHeader.substring(7);
        return tokenVerifier.verifyToken(idToken);
    }
}
