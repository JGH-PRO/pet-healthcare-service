package com.petcare.petcareapp.service;

import com.petcare.petcareapp.domain.Pet;
import com.petcare.petcareapp.domain.User;
import com.petcare.petcareapp.domain.Vaccination;
import com.petcare.petcareapp.dto.vaccination.CreateVaccinationRequestDto;
import com.petcare.petcareapp.dto.vaccination.UpdateVaccinationRequestDto;
import com.petcare.petcareapp.dto.vaccination.VaccinationDto;
import com.petcare.petcareapp.repository.PetRepository;
import com.petcare.petcareapp.repository.UserRepository;
import com.petcare.petcareapp.repository.VaccinationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VaccinationService {

    @Autowired
    private VaccinationRepository vaccinationRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private UserRepository userRepository; // To verify user if needed, though pet ownership is key

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
    public VaccinationDto addVaccination(Long petId, CreateVaccinationRequestDto requestDto, String username) {
        Pet pet = getPetIfOwnedBy(petId, username);

        Vaccination vaccination = new Vaccination();
        vaccination.setPet(pet);
        vaccination.setVaccineName(requestDto.getVaccineName());
        vaccination.setVaccinationDate(requestDto.getVaccinationDate());
        vaccination.setNotes(requestDto.getNotes());

        Vaccination savedVaccination = vaccinationRepository.save(vaccination);
        return mapToDto(savedVaccination);
    }

    @Transactional(readOnly = true)
    public List<VaccinationDto> getVaccinationsByPetId(Long petId, String username) {
        Pet pet = getPetIfOwnedBy(petId, username); // Ensures pet exists and is owned by user
        return vaccinationRepository.findByPet(pet).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VaccinationDto getVaccinationById(Long petId, Long vaccinationId, String username) {
        Pet pet = getPetIfOwnedBy(petId, username); // Ensures pet ownership context
        Vaccination vaccination = vaccinationRepository.findByIdAndPet(vaccinationId, pet)
                .orElseThrow(() -> new RuntimeException("Vaccination record not found with id: " + vaccinationId + " for this pet."));
        return mapToDto(vaccination);
    }

    @Transactional
    public VaccinationDto updateVaccination(Long petId, Long vaccinationId, UpdateVaccinationRequestDto requestDto, String username) {
        Pet pet = getPetIfOwnedBy(petId, username); // Ensures pet ownership
        Vaccination vaccination = vaccinationRepository.findByIdAndPet(vaccinationId, pet)
                .orElseThrow(() -> new RuntimeException("Vaccination record not found with id: " + vaccinationId));

        if (requestDto.getVaccineName() != null) {
            vaccination.setVaccineName(requestDto.getVaccineName());
        }
        if (requestDto.getVaccinationDate() != null) {
            vaccination.setVaccinationDate(requestDto.getVaccinationDate());
        }
        if (requestDto.getNotes() != null) {
            vaccination.setNotes(requestDto.getNotes());
        }

        Vaccination updatedVaccination = vaccinationRepository.save(vaccination);
        return mapToDto(updatedVaccination);
    }

    @Transactional
    public void deleteVaccination(Long petId, Long vaccinationId, String username) {
        Pet pet = getPetIfOwnedBy(petId, username); // Ensures pet ownership
        Vaccination vaccination = vaccinationRepository.findByIdAndPet(vaccinationId, pet)
                .orElseThrow(() -> new RuntimeException("Vaccination record not found with id: " + vaccinationId));
        vaccinationRepository.delete(vaccination);
    }

    private VaccinationDto mapToDto(Vaccination vaccination) {
        return new VaccinationDto(
                vaccination.getId(),
                vaccination.getVaccineName(),
                vaccination.getVaccinationDate(),
                vaccination.getNotes(),
                vaccination.getPet().getId()
        );
    }
}
