package com.petcare.petcareapp.repository;

import com.petcare.petcareapp.domain.Product;
import com.petcare.petcareapp.domain.model.ProductCategory;
import com.petcare.petcareapp.domain.model.TargetSpecies;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // For dynamic queries
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    // Basic finders - JpaSpecificationExecutor will be used for combined filters
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Product> findByCategory(ProductCategory category, Pageable pageable);
    Page<Product> findByTargetSpecies(TargetSpecies targetSpecies, Pageable pageable);
}
