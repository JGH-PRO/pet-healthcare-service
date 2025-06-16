package com.petcare.petcareapp.dto.healthlog;

import com.petcare.petcareapp.domain.model.HealthLogType;
import java.time.LocalDateTime;

public class HealthLogDto {
    private Long id;
    private Long petId;
    private HealthLogType logType;
    private LocalDateTime logDate;
    private String description;
    private Double quantity;
    private String unit;

    // Constructor, Getters, and Setters
    public HealthLogDto() {}

    public HealthLogDto(Long id, Long petId, HealthLogType logType, LocalDateTime logDate, String description, Double quantity, String unit) {
        this.id = id;
        this.petId = petId;
        this.logType = logType;
        this.logDate = logDate;
        this.description = description;
        this.quantity = quantity;
        this.unit = unit;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPetId() { return petId; }
    public void setPetId(Long petId) { this.petId = petId; }
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
