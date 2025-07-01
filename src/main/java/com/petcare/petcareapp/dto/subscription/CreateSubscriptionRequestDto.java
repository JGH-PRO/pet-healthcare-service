package com.petcare.petcareapp.dto.subscription;

import com.petcare.petcareapp.domain.model.SubscriptionFrequency;
// Add validation annotations later (e.g., @NotNull, @Min(1) for quantity)
public class CreateSubscriptionRequestDto {
    private Long productId;
    private Integer quantity;
    private SubscriptionFrequency frequency;
    private String shippingAddress; // Assuming simple string for now

    // Getters and Setters
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public SubscriptionFrequency getFrequency() { return frequency; }
    public void setFrequency(SubscriptionFrequency frequency) { this.frequency = frequency; }
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
}
