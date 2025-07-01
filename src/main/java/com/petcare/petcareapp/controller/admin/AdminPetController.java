package com.petcare.petcareapp.controller.admin;

import com.petcare.petcareapp.dto.admin.pet.AdminPetDto;
import com.petcare.petcareapp.dto.pet.UpdatePetRequestDto;
import com.petcare.petcareapp.service.admin.AdminPetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/pets")
@Tag(name = "Admin: Pet Management", description = "APIs for administrators to manage all pet profiles.")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPetController {

    @Autowired
    private AdminPetService adminPetService;

    @GetMapping
    @Operation(summary = "List all pets (Admin)", description = "Retrieves a paginated list of all pet profiles in the system.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of pets", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))), // Page of AdminPetDto
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Page<AdminPetDto>> getAllPets(
            @Parameter(description = "Pagination and sorting parameters") @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        Page<AdminPetDto> pets = adminPetService.getAllPets(pageable);
        return ResponseEntity.ok(pets);
    }

    @GetMapping("/{petId}")
    @Operation(summary = "Get pet by ID (Admin)", description = "Retrieves details for a specific pet by its ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved pet details", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AdminPetDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Pet not found")
    })
    public ResponseEntity<AdminPetDto> getPetById(
            @Parameter(name = "petId", description = "ID of the pet to retrieve", required = true) @PathVariable Long petId) {
        try {
            AdminPetDto petDto = adminPetService.getPetById(petId);
            return ResponseEntity.ok(petDto);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @PutMapping("/{petId}")
    @Operation(summary = "Update pet details (Admin)", description = "Updates the details of a specific pet.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pet details updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AdminPetDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Pet not found")
    })
    public ResponseEntity<AdminPetDto> updatePet(
            @Parameter(name = "petId", description = "ID of the pet to update", required = true) @PathVariable Long petId,
            @Valid @RequestBody UpdatePetRequestDto requestDto) {
        try {
            AdminPetDto updatedPet = adminPetService.updatePet(petId, requestDto);
            return ResponseEntity.ok(updatedPet);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @DeleteMapping("/{petId}")
    @Operation(summary = "Delete pet by ID (Admin)", description = "Deletes a pet profile from the system.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Pet deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Pet not found")
    })
    public ResponseEntity<Void> deletePet(
            @Parameter(name = "petId", description = "ID of the pet to delete", required = true) @PathVariable Long petId) {
        try {
            adminPetService.deletePet(petId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }
}
