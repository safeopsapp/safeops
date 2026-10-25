package com.safeops.organization;

import com.safeops.common.exception.ResourceNotFoundException;
import com.safeops.customer.Customer;
import com.safeops.customer.CustomerRepository;
import com.safeops.organization.dto.OrganizationDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final CustomerRepository customerRepository;

    public OrganizationService(OrganizationRepository organizationRepository, CustomerRepository customerRepository) {
        this.organizationRepository = organizationRepository;
        this.customerRepository = customerRepository;
    }

    public List<OrganizationDto> findByCustomerId(UUID customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found: " + customerId);
        }
        return organizationRepository.findAllByCustomerId(customerId).stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    public OrganizationDto findById(UUID id) {
        return organizationRepository.findById(id)
            .map(this::toDto)
            .orElseThrow(() -> new ResourceNotFoundException("Organization not found: " + id));
    }

    @Transactional
    public OrganizationDto create(UUID customerId, String name, String slug) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + customerId));
        Organization organization = new Organization(name, slug, customer);
        return toDto(organizationRepository.save(organization));
    }

    private OrganizationDto toDto(Organization org) {
        return new OrganizationDto(org.getId(), org.getName(), org.getSlug(), org.getDescription(), org.isActive(), org.getCustomer().getId(), org.getCreatedAt(), org.getUpdatedAt());
    }
}
