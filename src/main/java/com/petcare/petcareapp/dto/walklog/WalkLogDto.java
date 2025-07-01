package com.petcare.petcareapp.dto.walklog;

import java.time.LocalDateTime;

public class WalkLogDto {
    private Long id;
    private Long petId;
    private String petName; // Denormalized for convenience
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer durationMinutes;
    private Double distanceKm;
    private String routePath;
    private String notes;
    private LocalDateTime createdAt;

    public WalkLogDto(Long id, Long petId, String petName, LocalDateTime startTime, LocalDateTime endTime,
                      Integer durationMinutes, Double distanceKm, String routePath, String notes, LocalDateTime createdAt) {
        this.id = id;
        this.petId = petId;
        this.petName = petName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationMinutes = durationMinutes;
        this.distanceKm = distanceKm;
        this.routePath = routePath;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    // Getters
    public Long getId() { return id; }
    public Long getPetId() { return petId; }
    public String getPetName() { return petName; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public Double getDistanceKm() { return distanceKm; }
    public String getRoutePath() { return routePath; }
    public String getNotes() { return notes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
