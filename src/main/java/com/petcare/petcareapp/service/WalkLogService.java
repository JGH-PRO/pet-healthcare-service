package com.petcare.petcareapp.service;

import com.petcare.petcareapp.domain.Pet;
import com.petcare.petcareapp.domain.User;
import com.petcare.petcareapp.domain.WalkLog;
import com.petcare.petcareapp.dto.walklog.EndWalkRequestDto;
import com.petcare.petcareapp.dto.walklog.OngoingWalkDto;
import com.petcare.petcareapp.dto.walklog.StartWalkResponseDto;
import com.petcare.petcareapp.dto.walklog.WalkLogDto;
import com.petcare.petcareapp.repository.PetRepository;
import com.petcare.petcareapp.repository.UserRepository;
import com.petcare.petcareapp.repository.WalkLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class WalkLogService {

    @Autowired private WalkLogRepository walkLogRepository;
    @Autowired private PetRepository petRepository;
    @Autowired private UserRepository userRepository;

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    private Pet getPetIfOwnedBy(Long petId, User owner) {
        Pet pet = petRepository.findById(petId)
            .orElseThrow(() -> new RuntimeException("Pet not found with id: " + petId));
        if (!pet.getOwner().equals(owner)) {
            throw new AccessDeniedException("User does not own this pet.");
        }
        return pet;
    }

    @Transactional
    public StartWalkResponseDto startWalk(Long petId, String username) {
        User user = getUserByUsername(username);
        Pet pet = getPetIfOwnedBy(petId, user);

        // Check if there's already an ongoing walk for this pet
        if (walkLogRepository.findByPetAndEndTimeIsNull(pet).isPresent()) {
            throw new IllegalStateException("Pet " + pet.getName() + " already has an ongoing walk.");
        }

        WalkLog walkLog = new WalkLog();
        walkLog.setPet(pet);
        walkLog.setStartTime(LocalDateTime.now());

        WalkLog savedWalkLog = walkLogRepository.save(walkLog);
        return new StartWalkResponseDto(savedWalkLog.getId(), pet.getId(), savedWalkLog.getStartTime());
    }

    @Transactional
    public WalkLogDto endWalk(Long walkId, EndWalkRequestDto requestDto, String username) {
        User user = getUserByUsername(username);
        WalkLog walkLog = walkLogRepository.findById(walkId)
            .orElseThrow(() -> new RuntimeException("WalkLog not found with id: " + walkId));

        // Verify ownership
        if (!walkLog.getPet().getOwner().equals(user)) {
            throw new AccessDeniedException("User is not authorized to end this walk.");
        }
        if (walkLog.getEndTime() != null) {
            throw new IllegalStateException("This walk has already ended.");
        }

        walkLog.setEndTime(requestDto.getEndTime() != null ? requestDto.getEndTime() : LocalDateTime.now());
        if (requestDto.getDurationMinutes() != null) {
            walkLog.setDurationMinutes(requestDto.getDurationMinutes());
        } else if (walkLog.getStartTime() != null && walkLog.getEndTime() != null) {
            walkLog.setDurationMinutes((int) Duration.between(walkLog.getStartTime(), walkLog.getEndTime()).toMinutes());
        }
        walkLog.setDistanceKm(requestDto.getDistanceKm());
        walkLog.setRoutePath(requestDto.getRoutePath());
        walkLog.setNotes(requestDto.getNotes());

        WalkLog updatedWalkLog = walkLogRepository.save(walkLog);
        return mapToDto(updatedWalkLog);
    }

    @Transactional(readOnly = true)
    public WalkLogDto getWalkLogDetails(Long walkId, String username) {
        User user = getUserByUsername(username);
        WalkLog walkLog = walkLogRepository.findByIdAndPet_Owner(walkId, user)
            .orElseThrow(() -> new RuntimeException("WalkLog not found or access denied."));
        return mapToDto(walkLog);
    }

    @Transactional(readOnly = true)
    public Page<WalkLogDto> listWalkLogsForPet(Long petId, String username, Pageable pageable) {
        User user = getUserByUsername(username);
        Pet pet = getPetIfOwnedBy(petId, user);
        return walkLogRepository.findByPetOrderByStartTimeDesc(pet, pageable).map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public Optional<OngoingWalkDto> getOngoingWalkForPet(Long petId, String username) {
        User user = getUserByUsername(username);
        Pet pet = getPetIfOwnedBy(petId, user);
        return walkLogRepository.findByPetAndEndTimeIsNull(pet).map(this::mapToOngoingDto);
    }

    @Transactional
    public void deleteWalkLog(Long walkId, String username) {
        User user = getUserByUsername(username);
        WalkLog walkLog = walkLogRepository.findByIdAndPet_Owner(walkId, user)
             .orElseThrow(() -> new RuntimeException("WalkLog not found or access denied for deletion."));
        walkLogRepository.delete(walkLog);
    }

    private WalkLogDto mapToDto(WalkLog log) {
        return new WalkLogDto(log.getId(), log.getPet().getId(), log.getPet().getName(),
                              log.getStartTime(), log.getEndTime(), log.getDurationMinutes(),
                              log.getDistanceKm(), log.getRoutePath(), log.getNotes(), log.getCreatedAt());
    }

    private OngoingWalkDto mapToOngoingDto(WalkLog log) {
        return new OngoingWalkDto(log.getId(), log.getPet().getId(), log.getPet().getName(), log.getStartTime());
    }
}
