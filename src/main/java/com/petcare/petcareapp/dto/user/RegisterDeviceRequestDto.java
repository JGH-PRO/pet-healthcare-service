package com.petcare.petcareapp.dto.user;

import com.petcare.petcareapp.domain.model.UserDevicePlatform;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RegisterDeviceRequestDto {

    @NotBlank(message = "Device token cannot be blank")
    private String deviceToken;

    @NotNull(message = "Platform cannot be null")
    private UserDevicePlatform platform;

    // Getters
    public String getDeviceToken() {
        return deviceToken;
    }

    public UserDevicePlatform getPlatform() {
        return platform;
    }

    // Setters
    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public void setPlatform(UserDevicePlatform platform) {
        this.platform = platform;
    }
}
