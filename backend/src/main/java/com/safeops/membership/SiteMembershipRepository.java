package com.safeops.membership;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SiteMembershipRepository extends JpaRepository<SiteMembership, UUID> {
    List<SiteMembership> findByUserId(UUID userId);
    List<SiteMembership> findBySiteId(UUID siteId);
    boolean existsByUserIdAndSiteId(UUID userId, UUID siteId);
    Optional<SiteMembership> findByUserIdAndSiteId(UUID userId, UUID siteId);
}
