package com.petcare.petcareapp.dto.product;

import com.petcare.petcareapp.domain.model.ProductCategory;
import com.petcare.petcareapp.domain.model.TargetSpecies;
import java.math.BigDecimal;

public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private ProductCategory category;
    private TargetSpecies targetSpecies;
    private Integer stockQuantity;

    // Constructor
    public ProductDto() {}

    public ProductDto(Long id, String name, String description, BigDecimal price, String imageUrl,
                      ProductCategory category, TargetSpecies targetSpecies, Integer stockQuantity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
        this.targetSpecies = targetSpecies;
        this.stockQuantity = stockQuantity;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public ProductCategory getCategory() { return category; }
    public void setCategory(ProductCategory category) { this.category = category; }
    public TargetSpecies getTargetSpecies() { return targetSpecies; }
    public void setTargetSpecies(TargetSpecies targetSpecies) { this.targetSpecies = targetSpecies; }
    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
}
