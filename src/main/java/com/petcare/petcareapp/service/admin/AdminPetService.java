package com.petcare.petcareapp.service.admin;

import com.petcare.petcareapp.domain.Pet;
// import com.petcare.petcareapp.domain.User; // Needed if changing owner
import com.petcare.petcareapp.dto.admin.pet.AdminPetDto;
import com.petcare.petcareapp.dto.pet.UpdatePetRequestDto; // Reusing user's update DTO
import com.petcare.petcareapp.repository.PetRepository;
import com.petcare.petcareapp.repository.UserRepository; // Needed if changing owner
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
// import org.springframework.security.core.userdetails.UsernameNotFoundException; // If fetching user by name

@Service
public class AdminPetService {

    @Autowired
    private PetRepository petRepository;
    @Autowired
    private UserRepository userRepository; // For future use e.g. reassigning owner

    @Transactional(readOnly = true)
    public Page<AdminPetDto> getAllPets(Pageable pageable) {
        return petRepository.findAll(pageable).map(this::mapToAdminPetDto);
    }

    @Transactional(readOnly = true)
    public AdminPetDto getPetById(Long petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found with id: " + petId));
        return mapToAdminPetDto(pet);
    }

    @Transactional
    public AdminPetDto updatePet(Long petId, UpdatePetRequestDto requestDto) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found with id: " + petId));

        // Fields that an admin can update (similar to user update for now)
        if (requestDto.getName() != null) pet.setName(requestDto.getName());
        if (requestDto.getBreed() != null) pet.setBreed(requestDto.getBreed());
        if (requestDto.getGender() != null) pet.setGender(requestDto.getGender());
        if (requestDto.getBirthDate() != null) pet.setBirthDate(requestDto.getBirthDate());
        if (requestDto.getWeight() != null) pet.setWeight(requestDto.getWeight());
        // Admin could potentially change owner, but this is more complex:
        // if (requestDto.getOwnerId() != null) { // Assuming UpdatePetRequestDto has ownerId for admin
        //    User newOwner = userRepository.findById(requestDto.getOwnerId())
        //        .orElseThrow(() -> new RuntimeException("New owner not found"));
        //    pet.setOwner(newOwner);
        // }

        Pet updatedPet = petRepository.save(pet);
        return mapToAdminPetDto(updatedPet);
    }

    @Transactional
    public void deletePet(Long petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found with id: " + petId));
        // Consider implications: e.g., related appointments, health logs.
        // For now, direct deletion. Cascading should handle related entities if configured.
        petRepository.delete(pet);
    }

    private AdminPetDto mapToAdminPetDto(Pet pet) {
        return new AdminPetDto(
                pet.getId(),
                pet.getName(),
                pet.getBreed(),
                pet.getGender(),
                pet.getBirthDate(),
                pet.getWeight(),
                pet.getOwner() != null ? pet.getOwner().getId() : null,
                pet.getOwner() != null ? pet.getOwner().getUsername() : "N/A"
        );
    }
}
