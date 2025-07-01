package com.petcare.petcareapp.service;

import com.petcare.petcareapp.domain.Pet;
import com.petcare.petcareapp.domain.User;
import com.petcare.petcareapp.dto.pet.CreatePetRequestDto;
import com.petcare.petcareapp.dto.pet.PetDto;
import com.petcare.petcareapp.dto.pet.UpdatePetRequestDto;
import com.petcare.petcareapp.repository.PetRepository;
import com.petcare.petcareapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException; // For user not found
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PetService {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private UserRepository userRepository; // To fetch the User entity

    @Transactional
    public PetDto createPet(CreatePetRequestDto petDto, String username) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Pet pet = new Pet();
        pet.setName(petDto.getName());
        pet.setBreed(petDto.getBreed());
        pet.setGender(petDto.getGender());
        pet.setBirthDate(petDto.getBirthDate());
        pet.setWeight(petDto.getWeight());
        pet.setOwner(owner);

        Pet savedPet = petRepository.save(pet);
        return mapToPetDto(savedPet);
    }

    @Transactional(readOnly = true)
    public List<PetDto> getAllPetsByUsername(String username) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return petRepository.findByOwner(owner).stream()
                .map(this::mapToPetDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PetDto getPetByIdAndUsername(Long petId, String username) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        Pet pet = petRepository.findByIdAndOwner(petId, owner)
                .orElseThrow(() -> new RuntimeException("Pet not found with id " + petId + " for user " + username));
        return mapToPetDto(pet);
    }

    @Transactional
    public PetDto updatePet(Long petId, UpdatePetRequestDto petUpdateRequestDto, String username) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        Pet pet = petRepository.findByIdAndOwner(petId, owner)
                .orElseThrow(() -> new RuntimeException("Pet not found with id " + petId + " for user " + username));

        if (petUpdateRequestDto.getName() != null) {
            pet.setName(petUpdateRequestDto.getName());
        }
        if (petUpdateRequestDto.getBreed() != null) {
            pet.setBreed(petUpdateRequestDto.getBreed());
        }
        if (petUpdateRequestDto.getGender() != null) {
            pet.setGender(petUpdateRequestDto.getGender());
        }
        if (petUpdateRequestDto.getBirthDate() != null) {
            pet.setBirthDate(petUpdateRequestDto.getBirthDate());
        }
        if (petUpdateRequestDto.getWeight() != null) {
            pet.setWeight(petUpdateRequestDto.getWeight());
        }

        Pet updatedPet = petRepository.save(pet);
        return mapToPetDto(updatedPet);
    }

    @Transactional
    public void deletePet(Long petId, String username) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        Pet pet = petRepository.findByIdAndOwner(petId, owner)
                .orElseThrow(() -> new RuntimeException("Pet not found with id " + petId + " for user " + username));
        petRepository.delete(pet);
    }

    private PetDto mapToPetDto(Pet pet) {
        return new PetDto(
                pet.getId(),
                pet.getName(),
                pet.getBreed(),
                pet.getGender(),
                pet.getBirthDate(),
                pet.getWeight(),
                pet.getOwner().getId()
        );
    }
}
