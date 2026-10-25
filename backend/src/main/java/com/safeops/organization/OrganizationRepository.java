package com.safeops.organization;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, UUID> {
    Optional<Organization> findBySlug(String slug);
    Optional<Organization> findByReferralCode(String referralCode);
    boolean existsByReferralCode(String referralCode);
    boolean existsBySlug(String slug);
    List<Organization> findAllByCustomerId(UUID customerId);
    boolean existsBySlugAndCustomerId(String slug, UUID customerId);
}
