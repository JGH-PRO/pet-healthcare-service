package com.petcare.petcareapp.repository;

import com.petcare.petcareapp.domain.Pet;
import com.petcare.petcareapp.domain.User;
import com.petcare.petcareapp.domain.WalkLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface WalkLogRepository extends JpaRepository<WalkLog, Long> {
    Page<WalkLog> findByPetOrderByStartTimeDesc(Pet pet, Pageable pageable);
    Optional<WalkLog> findByPetAndEndTimeIsNull(Pet pet);
    // For ownership check when fetching/deleting a specific walk by its ID
    Optional<WalkLog> findByIdAndPet_Owner(Long walkId, User owner);
    List<WalkLog> findByPet(Pet pet); // Used by service for ongoing check
}
