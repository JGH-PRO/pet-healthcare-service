package com.petcare.petcareapp.controller;

import com.petcare.petcareapp.dto.vaccination.CreateVaccinationRequestDto;
import com.petcare.petcareapp.dto.vaccination.UpdateVaccinationRequestDto;
import com.petcare.petcareapp.dto.vaccination.VaccinationDto;
import com.petcare.petcareapp.service.VaccinationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/pets/{petId}/vaccinations")
@Tag(name = "Vaccination Management", description = "APIs for managing pet vaccination records")
public class VaccinationController {

    @Autowired
    private VaccinationService vaccinationService;

    @Operation(summary = "Add a new vaccination record", description = "Creates a new vaccination record for the specified pet.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Vaccination record created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input, pet not found, or user does not own pet"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    public ResponseEntity<VaccinationDto> addVaccination(
            @Parameter(description = "ID of the pet") @PathVariable Long petId,
            @Valid @RequestBody CreateVaccinationRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            VaccinationDto createdVaccination = vaccinationService.addVaccination(petId, requestDto, userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdVaccination);
        } catch (RuntimeException e) {
             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @Operation(summary = "List all vaccination records for a pet", description = "Retrieves all vaccination records associated with the given pet ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of vaccination records"),
            @ApiResponse(responseCode = "404", description = "Pet not found or user does not own pet"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public ResponseEntity<List<VaccinationDto>> getVaccinationsByPetId(
            @Parameter(description = "ID of the pet") @PathVariable Long petId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            List<VaccinationDto> vaccinations = vaccinationService.getVaccinationsByPetId(petId, userDetails.getUsername());
            return ResponseEntity.ok(vaccinations);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @Operation(summary = "Get a specific vaccination record", description = "Retrieves a single vaccination record by its ID and pet ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved vaccination record"),
            @ApiResponse(responseCode = "404", description = "Vaccination record or pet not found, or user does not own pet"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/{vaccinationId}")
    public ResponseEntity<VaccinationDto> getVaccinationById(
            @Parameter(description = "ID of the pet") @PathVariable Long petId,
            @Parameter(description = "ID of the vaccination record") @PathVariable Long vaccinationId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            VaccinationDto vaccinationDto = vaccinationService.getVaccinationById(petId, vaccinationId, userDetails.getUsername());
            return ResponseEntity.ok(vaccinationDto);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @Operation(summary = "Update a vaccination record", description = "Updates an existing vaccination record.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vaccination record updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Vaccination record or pet not found, or user does not own pet"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("/{vaccinationId}")
    public ResponseEntity<VaccinationDto> updateVaccination(
            @Parameter(description = "ID of the pet") @PathVariable Long petId,
            @Parameter(description = "ID of the vaccination record to update") @PathVariable Long vaccinationId,
            @Valid @RequestBody UpdateVaccinationRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            VaccinationDto updatedVaccination = vaccinationService.updateVaccination(petId, vaccinationId, requestDto, userDetails.getUsername());
            return ResponseEntity.ok(updatedVaccination);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @Operation(summary = "Delete a vaccination record", description = "Deletes a vaccination record by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Vaccination record deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Vaccination record or pet not found, or user does not own pet"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @DeleteMapping("/{vaccinationId}")
    public ResponseEntity<Void> deleteVaccination(
            @Parameter(description = "ID of the pet") @PathVariable Long petId,
            @Parameter(description = "ID of the vaccination record to delete") @PathVariable Long vaccinationId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            vaccinationService.deleteVaccination(petId, vaccinationId, userDetails.getUsername());
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }
}
