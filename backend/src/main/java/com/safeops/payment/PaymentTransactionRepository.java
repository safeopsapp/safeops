package com.safeops.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, UUID> {
    Optional<PaymentTransaction> findByTransactionReference(String transactionReference);
    List<PaymentTransaction> findByOrganizationIdOrderByCreatedAtDesc(UUID organizationId);
    List<PaymentTransaction> findByCustomerIdOrderByCreatedAtDesc(UUID customerId);
    boolean existsByTransactionReference(String transactionReference);
}

