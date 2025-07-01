package com.petcare.petcareapp.dto.walklog;

import java.time.LocalDateTime;
// Add validation annotations later if needed

public class EndWalkRequestDto {
    private LocalDateTime endTime;
    private Integer durationMinutes; // Optional, can be calculated if endTime is provided
    private Double distanceKm;
    private String routePath; // e.g., polyline or JSON array of coordinates
    private String notes;

    // Getters and Setters
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }
    public String getRoutePath() { return routePath; }
    public void setRoutePath(String routePath) { this.routePath = routePath; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
