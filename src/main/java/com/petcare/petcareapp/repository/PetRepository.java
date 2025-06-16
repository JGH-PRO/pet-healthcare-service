package com.petcare.petcareapp.repository;

import com.petcare.petcareapp.domain.Pet;
import com.petcare.petcareapp.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    List<Pet> findByOwner(User owner);
    Optional<Pet> findByIdAndOwner(Long id, User owner);
}
