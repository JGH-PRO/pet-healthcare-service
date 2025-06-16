package com.petcare.petcareapp.repository;

import com.petcare.petcareapp.domain.Pet;
import com.petcare.petcareapp.domain.Vaccination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VaccinationRepository extends JpaRepository<Vaccination, Long> {
    List<Vaccination> findByPet(Pet pet);
    Optional<Vaccination> findByIdAndPet(Long id, Pet pet);
    // If we need to find by pet ID directly:
    List<Vaccination> findByPetId(Long petId);
    Optional<Vaccination> findByIdAndPetId(Long id, Long petId);
}
