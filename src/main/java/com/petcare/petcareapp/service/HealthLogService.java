package com.petcare.petcareapp.service;

import com.petcare.petcareapp.domain.HealthLog;
import com.petcare.petcareapp.domain.Pet;
import com.petcare.petcareapp.domain.User;
import com.petcare.petcareapp.domain.model.HealthLogType;
import com.petcare.petcareapp.dto.healthlog.CreateHealthLogRequestDto;
import com.petcare.petcareapp.dto.healthlog.HealthLogDto;
import com.petcare.petcareapp.dto.healthlog.UpdateHealthLogRequestDto;
import com.petcare.petcareapp.repository.HealthLogRepository;
import com.petcare.petcareapp.repository.PetRepository;
import com.petcare.petcareapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HealthLogService {

    @Autowired
    private HealthLogRepository healthLogRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private UserRepository userRepository;

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    private Pet getPetIfOwnedBy(Long petId, String username) {
        User owner = getUserByUsername(username);
        Pet pet = petRepository.findById(petId)
            .orElseThrow(() -> new RuntimeException("Pet not found with id: " + petId));
        // Ensure pet.getOwner() is not null before calling equals
        if (pet.getOwner() == null || !pet.getOwner().equals(owner)) {
            throw new AccessDeniedException("User does not own this pet or pet has no owner");
        }
        return pet;
    }

    @Transactional
    public HealthLogDto addHealthLog(Long petId, CreateHealthLogRequestDto requestDto, String username) {
        Pet pet = getPetIfOwnedBy(petId, username);

        HealthLog healthLog = new HealthLog();
        healthLog.setPet(pet);
        healthLog.setLogType(requestDto.getLogType());
        // If logDate is not sent by client, default to now. Client should usually send it.
        healthLog.setLogDate(requestDto.getLogDate() != null ? requestDto.getLogDate() : LocalDateTime.now());
        healthLog.setDescription(requestDto.getDescription());
        healthLog.setQuantity(requestDto.getQuantity());
        healthLog.setUnit(requestDto.getUnit());

        HealthLog savedHealthLog = healthLogRepository.save(healthLog);
        return mapToDto(savedHealthLog);
    }

    @Transactional(readOnly = true)
    public List<HealthLogDto> getHealthLogsByPetId(Long petId, String username, HealthLogType logType, LocalDateTime startDate, LocalDateTime endDate) {
        Pet pet = getPetIfOwnedBy(petId, username);
        List<HealthLog> logs;
        if (logType != null && startDate != null && endDate != null) {
            logs = healthLogRepository.findByPetAndLogTypeAndLogDateBetween(pet, logType, startDate, endDate);
        } else if (logType != null) {
            logs = healthLogRepository.findByPetAndLogType(pet, logType);
        } else if (startDate != null && endDate != null) {
            logs = healthLogRepository.findByPetAndLogDateBetween(pet, startDate, endDate);
        } else {
            logs = healthLogRepository.findByPet(pet);
        }
        return logs.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public HealthLogDto getHealthLogById(Long petId, Long logId, String username) {
        Pet pet = getPetIfOwnedBy(petId, username);
        HealthLog healthLog = healthLogRepository.findByIdAndPet(logId, pet)
                .orElseThrow(() -> new RuntimeException("Health log not found with id: " + logId + " for this pet."));
        return mapToDto(healthLog);
    }

    @Transactional
    public HealthLogDto updateHealthLog(Long petId, Long logId, UpdateHealthLogRequestDto requestDto, String username) {
        Pet pet = getPetIfOwnedBy(petId, username);
        HealthLog healthLog = healthLogRepository.findByIdAndPet(logId, pet)
                .orElseThrow(() -> new RuntimeException("Health log not found with id: " + logId));

        if (requestDto.getLogType() != null) healthLog.setLogType(requestDto.getLogType());
        if (requestDto.getLogDate() != null) healthLog.setLogDate(requestDto.getLogDate());
        if (requestDto.getDescription() != null) healthLog.setDescription(requestDto.getDescription());
        if (requestDto.getQuantity() != null) healthLog.setQuantity(requestDto.getQuantity());
        if (requestDto.getUnit() != null) healthLog.setUnit(requestDto.getUnit());

        HealthLog updatedHealthLog = healthLogRepository.save(healthLog);
        return mapToDto(updatedHealthLog);
    }

    @Transactional
    public void deleteHealthLog(Long petId, Long logId, String username) {
        Pet pet = getPetIfOwnedBy(petId, username);
        HealthLog healthLog = healthLogRepository.findByIdAndPet(logId, pet)
                .orElseThrow(() -> new RuntimeException("Health log not found with id: " + logId));
        healthLogRepository.delete(healthLog);
    }

    private HealthLogDto mapToDto(HealthLog healthLog) {
        return new HealthLogDto(
                healthLog.getId(),
                healthLog.getPet().getId(),
                healthLog.getLogType(),
                healthLog.getLogDate(),
                healthLog.getDescription(),
                healthLog.getQuantity(),
                healthLog.getUnit()
        );
    }
}
