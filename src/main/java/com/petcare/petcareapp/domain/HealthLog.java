package com.petcare.petcareapp.domain;

import com.petcare.petcareapp.domain.model.HealthLogType;
import jakarta.persistence.*;
import java.time.LocalDateTime; // Using LocalDateTime for more precision
import java.util.Objects;

@Entity
@Table(name = "health_logs")
public class HealthLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @Enumerated(EnumType.STRING)
    @Column(name = "log_type", nullable = false, length = 20)
    private HealthLogType logType;

    @Column(name = "log_date", nullable = false)
    private LocalDateTime logDate;

    @Column(length = 500)
    private String description; // e.g., "Chicken and Rice", "Park walk", "Regular"

    @Column
    private Double quantity;    // e.g., 100 (for grams of food), 1.5 (for km or hours for walk), 15.5 (for kg weight)

    @Column(length = 20)
    private String unit;        // e.g., "grams", "scoops", "km", "minutes", "kg", "lbs"

    // Constructors
    public HealthLog() {
    }

    public HealthLog(Pet pet, HealthLogType logType, LocalDateTime logDate, String description, Double quantity, String unit) {
        this.pet = pet;
        this.logType = logType;
        this.logDate = logDate;
        this.description = description;
        this.quantity = quantity;
        this.unit = unit;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Pet getPet() { return pet; }
    public void setPet(Pet pet) { this.pet = pet; }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HealthLog healthLog = (HealthLog) o;
        return Objects.equals(id, healthLog.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "HealthLog{" +
               "id=" + id +
               ", petId=" + (pet != null ? pet.getId() : null) +
               ", logType=" + logType +
               ", logDate=" + logDate +
               ", description='" + description + '\'' +
               '}';
    }
}
