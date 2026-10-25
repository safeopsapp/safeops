package com.safeops.organization;

import com.safeops.common.response.ApiResponse;
import com.safeops.organization.dto.OrganizationDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations")
@Tag(name = "Organizations", description = "Organization management endpoints")
@SecurityRequirement(name = "BearerAuth")
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping
    @Operation(summary = "List organizations by customer")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<OrganizationDto>>> findByCustomerId(@RequestParam UUID customerId) {
        return ResponseEntity.ok(ApiResponse.success(organizationService.findByCustomerId(customerId)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get organization by ID")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<OrganizationDto>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(organizationService.findById(id)));
    }
}
