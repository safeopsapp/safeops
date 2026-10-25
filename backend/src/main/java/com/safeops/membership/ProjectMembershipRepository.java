package com.safeops.membership;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectMembershipRepository extends JpaRepository<ProjectMembership, UUID> {
    List<ProjectMembership> findByUserId(UUID userId);
    List<ProjectMembership> findByProjectId(UUID projectId);
    boolean existsByUserIdAndProjectId(UUID userId, UUID projectId);
    Optional<ProjectMembership> findByUserIdAndProjectId(UUID userId, UUID projectId);
}
