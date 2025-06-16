package com.petcare.petcareapp.repository;

import com.petcare.petcareapp.domain.HealthLog;
import com.petcare.petcareapp.domain.Pet;
import com.petcare.petcareapp.domain.model.HealthLogType; // If filtering by type
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime; // If filtering by date
import java.util.List;
import java.util.Optional;

@Repository
public interface HealthLogRepository extends JpaRepository<HealthLog, Long> {
    List<HealthLog> findByPet(Pet pet);
    Optional<HealthLog> findByIdAndPet(Long id, Pet pet);

    // For potential future filtering:
    List<HealthLog> findByPetAndLogType(Pet pet, HealthLogType logType);
    List<HealthLog> findByPetAndLogDateBetween(Pet pet, LocalDateTime startDate, LocalDateTime endDate);
    List<HealthLog> findByPetAndLogTypeAndLogDateBetween(Pet pet, HealthLogType logType, LocalDateTime startDate, LocalDateTime endDate);
}
