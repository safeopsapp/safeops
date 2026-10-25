package com.safeops.project;

import com.safeops.common.response.ApiResponse;
import com.safeops.project.dto.ProjectDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects")
@Tag(name = "Projects", description = "Project management endpoints")
@SecurityRequirement(name = "BearerAuth")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    @Operation(summary = "List projects by organization")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<ProjectDto>>> findByOrganizationId(@RequestParam UUID organizationId) {
        return ResponseEntity.ok(ApiResponse.success(projectService.findByOrganizationId(organizationId)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get project by ID")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ProjectDto>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(projectService.findById(id)));
    }
}
