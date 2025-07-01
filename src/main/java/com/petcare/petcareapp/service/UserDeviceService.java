package com.petcare.petcareapp.service;

import com.petcare.petcareapp.domain.User;
import com.petcare.petcareapp.domain.UserDevice;
import com.petcare.petcareapp.dto.user.RegisterDeviceRequestDto;
import com.petcare.petcareapp.dto.user.UserDeviceDto;
import com.petcare.petcareapp.repository.UserDeviceRepository;
import com.petcare.petcareapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserDeviceService {

    @Autowired private UserRepository userRepository;
    @Autowired private UserDeviceRepository userDeviceRepository;

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Transactional
    public UserDeviceDto registerDevice(RegisterDeviceRequestDto requestDto, String username) {
        if (!StringUtils.hasText(requestDto.getDeviceToken())) {
            throw new IllegalArgumentException("Device token cannot be empty.");
        }
        if (requestDto.getPlatform() == null) {
            throw new IllegalArgumentException("Device platform must be specified.");
        }

        User user = getUserByUsername(username);
        // This method needs to be added to UserDeviceRepository
        Optional<UserDevice> existingDeviceOpt = userDeviceRepository.findByUserAndDeviceToken(user, requestDto.getDeviceToken());

        UserDevice device;
        if (existingDeviceOpt.isPresent()) {
            device = existingDeviceOpt.get();
            device.setPlatform(requestDto.getPlatform()); // Update platform if changed
            device.setLastLogin(LocalDateTime.now());
        } else {
            device = new UserDevice();
            device.setUser(user);
            device.setDeviceToken(requestDto.getDeviceToken());
            device.setPlatform(requestDto.getPlatform());
            device.setLastLogin(LocalDateTime.now());
            // createdAt is handled by @CreationTimestamp in UserDevice entity
        }
        UserDevice savedDevice = userDeviceRepository.save(device);
        return mapToDto(savedDevice);
    }

    @Transactional
    public void removeDevice(String deviceToken, String username) {
         if (!StringUtils.hasText(deviceToken)) {
            throw new IllegalArgumentException("Device token cannot be empty for removal.");
        }
        User user = getUserByUsername(username);
        // This method needs to be added to UserDeviceRepository
        UserDevice device = userDeviceRepository.findByUserAndDeviceToken(user, deviceToken)
                .orElseThrow(() -> new RuntimeException("Device token not found for this user."));
        userDeviceRepository.delete(device);
    }

    @Transactional(readOnly = true)
    public List<UserDeviceDto> getDevicesForUser(String username) {
        User user = getUserByUsername(username);
        return userDeviceRepository.findByUser(user).stream()
                                 .map(this::mapToDto)
                                 .collect(Collectors.toList());
    }

    private UserDeviceDto mapToDto(UserDevice device) {
        // Assuming UserDevice.getCreatedAt() exists and is populated by @CreationTimestamp
        return new UserDeviceDto(
            device.getId(),
            device.getDeviceToken(),
            device.getPlatform(),
            device.getLastLogin(),
            device.getCreatedAt()
        );
    }
}
