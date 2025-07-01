package com.petcare.petcareapp.service;

import com.petcare.petcareapp.domain.Product;
import com.petcare.petcareapp.domain.model.ProductCategory;
import com.petcare.petcareapp.domain.model.TargetSpecies;
import com.petcare.petcareapp.dto.product.ProductDto;
import com.petcare.petcareapp.repository.ProductRepository;
import jakarta.persistence.criteria.Predicate; // For JpaSpecificationExecutor
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification; // For JpaSpecificationExecutor
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils; // For checking empty strings

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Page<ProductDto> getAllProducts(Pageable pageable, String name, ProductCategory category, TargetSpecies species) {
        // Using JpaSpecificationExecutor for dynamic query building
        Specification<Product> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(name)) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            if (category != null) {
                predicates.add(criteriaBuilder.equal(root.get("category"), category));
            }
            if (species != null) {
                predicates.add(criteriaBuilder.equal(root.get("targetSpecies"), species));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return productRepository.findAll(spec, pageable).map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public ProductDto getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        return mapToDto(product);
    }

    private ProductDto mapToDto(Product product) {
        if (product == null) return null; // Added null check
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getImageUrl(),
                product.getCategory(),
                product.getTargetSpecies(),
                product.getStockQuantity()
        );
    }
}
