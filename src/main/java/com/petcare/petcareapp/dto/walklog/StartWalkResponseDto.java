package com.petcare.petcareapp.dto.walklog;

import java.time.LocalDateTime;

public class StartWalkResponseDto {
    private Long walkId;
    private Long petId;
    private LocalDateTime startTime;

    public StartWalkResponseDto(Long walkId, Long petId, LocalDateTime startTime) {
        this.walkId = walkId;
        this.petId = petId;
        this.startTime = startTime;
    }

    // Getters
    public Long getWalkId() { return walkId; }
    public Long getPetId() { return petId; }
    public LocalDateTime getStartTime() { return startTime; }
}
