package com.petcare.petcareapp.dto.walklog;

import java.time.LocalDateTime;

public class OngoingWalkDto {
    private Long walkId;
    private Long petId;
    private String petName;
    private LocalDateTime startTime;

    public OngoingWalkDto(Long walkId, Long petId, String petName, LocalDateTime startTime) {
        this.walkId = walkId;
        this.petId = petId;
        this.petName = petName;
        this.startTime = startTime;
    }
    // Getters
    public Long getWalkId() { return walkId; }
    public Long getPetId() { return petId; }
    public String getPetName() { return petName; }
    public LocalDateTime getStartTime() { return startTime; }
}
