package com.petcare.petcareapp.dto.healthlog;

import com.petcare.petcareapp.domain.model.HealthLogType;
import java.time.LocalDateTime;

public class CreateHealthLogRequestDto {
    private HealthLogType logType;
    private LocalDateTime logDate; // Consider if client should set this or server defaults to now
    private String description;
    private Double quantity;
    private String unit;

    // Getters and Setters
    public HealthLogType getLogType() { return logType; }
    public void setLogType(HealthLogType logType) { this.logType = logType; }
    public LocalDateTime getLogDate() { return logDate; }
    public void setLogDate(LocalDateTime logDate) { this.logDate = logDate; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
}
