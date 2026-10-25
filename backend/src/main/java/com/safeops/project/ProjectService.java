package com.safeops.project;

import com.safeops.common.exception.ResourceNotFoundException;
import com.safeops.organization.Organization;
import com.safeops.organization.OrganizationRepository;
import com.safeops.project.dto.ProjectDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final OrganizationRepository organizationRepository;

    public ProjectService(ProjectRepository projectRepository, OrganizationRepository organizationRepository) {
        this.projectRepository = projectRepository;
        this.organizationRepository = organizationRepository;
    }

    public List<ProjectDto> findByOrganizationId(UUID organizationId) {
        if (!organizationRepository.existsById(organizationId)) {
            throw new ResourceNotFoundException("Organization not found: " + organizationId);
        }
        return projectRepository.findAllByOrganizationId(organizationId).stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    public ProjectDto findById(UUID id) {
        return projectRepository.findById(id)
            .map(this::toDto)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + id));
    }

    @Transactional
    public ProjectDto create(UUID organizationId, String name, String slug) {
        Organization org = organizationRepository.findById(organizationId)
            .orElseThrow(() -> new ResourceNotFoundException("Organization not found: " + organizationId));
        Project project = new Project(name, slug, org.getCustomer(), org);
        return toDto(projectRepository.save(project));
    }

    private ProjectDto toDto(Project p) {
        return new ProjectDto(p.getId(), p.getName(), p.getSlug(), p.getDescription(), p.getLocation(), p.isActive(), p.getCustomer().getId(), p.getOrganization().getId(), p.getCreatedAt(), p.getUpdatedAt());
    }
}
