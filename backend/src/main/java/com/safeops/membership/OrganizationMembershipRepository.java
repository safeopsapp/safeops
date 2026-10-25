package com.safeops.membership;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganizationMembershipRepository extends JpaRepository<OrganizationMembership, UUID> {
    List<OrganizationMembership> findByUserId(UUID userId);
    List<OrganizationMembership> findByUserIdAndStatus(UUID userId, MembershipStatus status);
    List<OrganizationMembership> findByOrganizationId(UUID organizationId);
    List<OrganizationMembership> findByOrganizationIdAndStatus(UUID organizationId, MembershipStatus status);
    boolean existsByUserIdAndOrganizationId(UUID userId, UUID organizationId);
    Optional<OrganizationMembership> findByUserIdAndOrganizationId(UUID userId, UUID organizationId);
}
