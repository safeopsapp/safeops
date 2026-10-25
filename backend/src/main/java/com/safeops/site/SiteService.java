package com.safeops.site;

import com.safeops.common.exception.ResourceNotFoundException;
import com.safeops.project.Project;
import com.safeops.project.ProjectRepository;
import com.safeops.site.dto.SiteDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SiteService {
    private final SiteRepository siteRepository;
    private final ProjectRepository projectRepository;

    public SiteService(SiteRepository siteRepository, ProjectRepository projectRepository) {
        this.siteRepository = siteRepository;
        this.projectRepository = projectRepository;
    }

    public List<SiteDto> findByProjectId(UUID projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found: " + projectId);
        }
        return siteRepository.findAllByProjectId(projectId).stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    public SiteDto findById(UUID id) {
        return siteRepository.findById(id)
            .map(this::toDto)
            .orElseThrow(() -> new ResourceNotFoundException("Site not found: " + id));
    }

    @Transactional
    public SiteDto create(UUID projectId, String name, String slug) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));
        Site site = new Site(name, slug, project.getCustomer(), project.getOrganization(), project);
        return toDto(siteRepository.save(site));
    }

    private SiteDto toDto(Site s) {
        return new SiteDto(s.getId(), s.getName(), s.getSlug(), s.getDescription(), s.getLocation(), s.isActive(), s.getCustomer().getId(), s.getOrganization().getId(), s.getProject().getId(), s.getCreatedAt(), s.getUpdatedAt());
    }
}
