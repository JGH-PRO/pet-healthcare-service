package com.petcare.petcareapp.repository;

import com.petcare.petcareapp.domain.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClinicRepository extends JpaRepository<Clinic, Long> {
    // Basic CRUD methods are inherited.
    // Add custom query methods if needed in the future, e.g.:
    // List<Clinic> findByNameContainingIgnoreCase(String name);
    // List<Clinic> findByAddressContainingIgnoreCase(String address);
    // List<Clinic> findByServicesOfferedContainingIgnoreCase(String service);
}
