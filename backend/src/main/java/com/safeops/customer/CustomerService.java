package com.safeops.customer;

import com.safeops.common.exception.ResourceNotFoundException;
import com.safeops.customer.dto.CustomerDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CustomerService {
    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<CustomerDto> findAll() {
        return customerRepository.findAll().stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    public CustomerDto findById(UUID id) {
        return customerRepository.findById(id)
            .map(this::toDto)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + id));
    }

    @Transactional
    public CustomerDto create(String name, String slug) {
        log.info("Creating customer: {}", slug);
        Customer customer = new Customer(name, slug);
        return toDto(customerRepository.save(customer));
    }

    private CustomerDto toDto(Customer c) {
        return new CustomerDto(c.getId(), c.getName(), c.getSlug(), c.getDescription(), c.isActive(), c.getCreatedAt(), c.getUpdatedAt());
    }
}
