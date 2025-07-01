package com.petcare.petcareapp.dto.subscription;

import com.petcare.petcareapp.domain.model.SubscriptionFrequency;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class SubscriptionDto {
    private Long id;
    private Long userId;
    private Long productId;
    private String productName; // Denormalized
    private Integer quantity;
    private SubscriptionFrequency frequency;
    private LocalDate startDate;
    private LocalDate nextDeliveryDate;
    private boolean isActive;
    private String shippingAddress;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public SubscriptionDto(Long id, Long userId, Long productId, String productName, Integer quantity,
                           SubscriptionFrequency frequency, LocalDate startDate, LocalDate nextDeliveryDate,
                           boolean isActive, String shippingAddress, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.frequency = frequency;
        this.startDate = startDate;
        this.nextDeliveryDate = nextDeliveryDate;
        this.isActive = isActive;
        this.shippingAddress = shippingAddress;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters (setters usually not needed for response DTOs if using constructor)
    public Long getId() { return id;}
    public Long getUserId() { return userId;}
    public Long getProductId() { return productId;}
    public String getProductName() { return productName;}
    public Integer getQuantity() { return quantity;}
    public SubscriptionFrequency getFrequency() { return frequency;}
    public LocalDate getStartDate() { return startDate;}
    public LocalDate getNextDeliveryDate() { return nextDeliveryDate;}
    public boolean isActive() { return isActive;}
    public String getShippingAddress() { return shippingAddress;}
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
