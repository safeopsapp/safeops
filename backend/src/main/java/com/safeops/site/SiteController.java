package com.safeops.site;

import com.safeops.common.response.ApiResponse;
import com.safeops.site.dto.SiteDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sites")
@Tag(name = "Sites", description = "Site management endpoints")
@SecurityRequirement(name = "BearerAuth")
public class SiteController {

    private final SiteService siteService;

    public SiteController(SiteService siteService) {
        this.siteService = siteService;
    }

    @GetMapping
    @Operation(summary = "List sites by project")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<SiteDto>>> findByProjectId(@RequestParam UUID projectId) {
        return ResponseEntity.ok(ApiResponse.success(siteService.findByProjectId(projectId)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get site by ID")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<SiteDto>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(siteService.findById(id)));
    }
}
