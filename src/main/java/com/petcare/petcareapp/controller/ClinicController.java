package com.petcare.petcareapp.controller;

import com.petcare.petcareapp.dto.clinic.ClinicDto;
import com.petcare.petcareapp.service.ClinicService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/clinics")
@Tag(name = "Clinic Management", description = "APIs for finding vet clinics. Publicly accessible for viewing.")
public class ClinicController {

    @Autowired
    private ClinicService clinicService;

    @GetMapping
    @Operation(summary = "List all vet clinics", description = "Retrieves a list of all available vet clinics.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of clinics",
                         content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClinicDto.class)))
    })
    public ResponseEntity<List<ClinicDto>> getAllClinics() {
        List<ClinicDto> clinics = clinicService.getAllClinics();
        return ResponseEntity.ok(clinics);
    }

    @GetMapping("/{clinicId}")
    @Operation(summary = "Get clinic by ID", description = "Retrieves details for a specific vet clinic by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved clinic details",
                         content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClinicDto.class))),
            @ApiResponse(responseCode = "404", description = "Clinic not found")
    })
    public ResponseEntity<ClinicDto> getClinicById(
            @Parameter(description = "ID of the clinic to retrieve") @PathVariable Long clinicId) {
        try {
            ClinicDto clinic = clinicService.getClinicById(clinicId);
            return ResponseEntity.ok(clinic);
        } catch (RuntimeException e) { // Assuming service throws RuntimeException for not found
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    // Future POST, PUT, DELETE endpoints for admin will require authentication and role checks
    // These would be annotated similarly but also include security requirements.
}
