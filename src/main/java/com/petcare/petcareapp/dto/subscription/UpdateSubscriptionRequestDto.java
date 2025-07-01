package com.petcare.petcareapp.dto.subscription;

import com.petcare.petcareapp.domain.model.SubscriptionFrequency;

public class UpdateSubscriptionRequestDto {
    private Integer quantity; // Optional
    private SubscriptionFrequency frequency; // Optional
    private String shippingAddress; // Optional
    private Boolean isActive; // Optional, for explicit activation/deactivation

    // Getters and Setters
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public SubscriptionFrequency getFrequency() { return frequency; }
    public void setFrequency(SubscriptionFrequency frequency) { this.frequency = frequency; }
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
