package com.petcare.petcareapp.dto.user;

import com.petcare.petcareapp.domain.model.UserDevicePlatform;
import java.time.LocalDateTime;

public class UserDeviceDto {
    private Long id;
    private String deviceToken;
    private UserDevicePlatform platform;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;

    // Constructor to match UserDeviceService.mapToDto
    public UserDeviceDto(Long id, String deviceToken, UserDevicePlatform platform, LocalDateTime lastLogin, LocalDateTime createdAt) {
        this.id = id;
        this.deviceToken = deviceToken;
        this.platform = platform;
        this.lastLogin = lastLogin;
        this.createdAt = createdAt;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public UserDevicePlatform getPlatform() {
        return platform;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Setters can be added if needed for other use cases, but not strictly required for this DTO.
}
