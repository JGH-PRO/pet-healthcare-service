package com.petcare.petcareapp.controller;
import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.tags.Tag;

import com.petcare.petcareapp.dto.pet.CreatePetRequestDto;
import com.petcare.petcareapp.dto.pet.PetDto;
import com.petcare.petcareapp.dto.pet.UpdatePetRequestDto;
import com.petcare.petcareapp.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Tag(name = "Pet Management", description = "Endpoints for managing pet profiles")
@RestController
@RequestMapping("/api/pets")
public class PetController {

    @Autowired
    private PetService petService;

    @Operation(summary = "Create a new pet", description = "Adds a new pet profile for the authenticated user.")
    @PostMapping
    public ResponseEntity<PetDto> createPet(@Valid @RequestBody CreatePetRequestDto createPetRequestDto,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        PetDto createdPet = petService.createPet(createPetRequestDto, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPet);
    }

    @Operation(summary = "List all pets for the user", description = "Retrieves a list of all pets belonging to the authenticated user.")
    @GetMapping
    public ResponseEntity<List<PetDto>> getAllPets(@AuthenticationPrincipal UserDetails userDetails) {
        List<PetDto> pets = petService.getAllPetsByUsername(userDetails.getUsername());
        return ResponseEntity.ok(pets);
    }

    @GetMapping("/{petId}")
    public ResponseEntity<PetDto> getPetById(@PathVariable Long petId,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        try {
            PetDto petDto = petService.getPetByIdAndUsername(petId, userDetails.getUsername());
            return ResponseEntity.ok(petDto);
        } catch (RuntimeException e) { // Catch specific exceptions like PetNotFound if defined
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/{petId}")
    public ResponseEntity<PetDto> updatePet(@PathVariable Long petId,
                                            @Valid @RequestBody UpdatePetRequestDto updatePetRequestDto,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            PetDto updatedPet = petService.updatePet(petId, updatePetRequestDto, userDetails.getUsername());
            return ResponseEntity.ok(updatedPet);
        } catch (RuntimeException e) { // Catch specific exceptions
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Or BAD_REQUEST depending on error
        }
    }

    @DeleteMapping("/{petId}")
    public ResponseEntity<Void> deletePet(@PathVariable Long petId,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        try {
            petService.deletePet(petId, userDetails.getUsername());
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) { // Catch specific exceptions
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
