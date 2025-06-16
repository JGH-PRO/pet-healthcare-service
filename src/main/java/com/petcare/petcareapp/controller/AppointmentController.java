package com.petcare.petcareapp.controller;

import com.petcare.petcareapp.domain.model.HealthLogType; // Not used here, but was in previous version of script for this controller. Removing.
import com.petcare.petcareapp.dto.appointment.AppointmentDto;
import com.petcare.petcareapp.dto.appointment.CreateAppointmentRequestDto;
import com.petcare.petcareapp.service.AppointmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat; // Needed for @DateTimeFormat if used in params
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import java.time.LocalDateTime; // Was missing if @DateTimeFormat used for LocalDateTime
import java.util.List;

@RestController
@RequestMapping("/api") // Base path, specific paths defined in methods
@Tag(name = "Appointment Management", description = "APIs for booking and managing vet appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping("/appointments")
    @Operation(summary = "Create a new appointment", description = "Books a new vet appointment for one of the authenticated user's pets. The pet must be owned by the authenticated user. Default appointment status is PENDING.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Appointment created successfully",
                         content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppointmentDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input (e.g., pet or clinic not found, invalid date)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
            @ApiResponse(responseCode = "403", description = "Forbidden - user does not own the pet")
    })
    public ResponseEntity<AppointmentDto> createAppointment(
            @Valid @RequestBody CreateAppointmentRequestDto requestDto,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        try {
            AppointmentDto createdAppointment = appointmentService.createAppointment(requestDto, userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAppointment);
        } catch (AccessDeniedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage(), e);
        } catch (RuntimeException e) { // Catch other runtime exceptions like pet/clinic not found
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @GetMapping("/users/me/appointments")
    @Operation(summary = "List appointments for current user", description = "Retrieves a list of all appointments for the pets belonging to the currently authenticated user, ordered by date descending.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved appointments list",
                         content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppointmentDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated")
    })
    public ResponseEntity<List<AppointmentDto>> getAppointmentsForCurrentUser(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        List<AppointmentDto> appointments = appointmentService.getAppointmentsForUser(userDetails.getUsername());
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/pets/{petId}/appointments")
    @Operation(summary = "List appointments for a specific pet", description = "Retrieves a list of all appointments for a specific pet, if the pet belongs to the authenticated user. Ordered by date descending.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved appointments list",
                         content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppointmentDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - user does not own this pet"),
            @ApiResponse(responseCode = "404", description = "Pet not found")
    })
    public ResponseEntity<List<AppointmentDto>> getAppointmentsForPet(
            @Parameter(description = "ID of the pet whose appointments are to be retrieved", required = true) @PathVariable Long petId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        try {
            List<AppointmentDto> appointments = appointmentService.getAppointmentsForPet(petId, userDetails.getUsername());
            return ResponseEntity.ok(appointments);
        } catch (AccessDeniedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage(), e);
        } catch (RuntimeException e) { // e.g. Pet not found
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }
    // Future: GET /appointments/{appointmentId}, PUT /appointments/{appointmentId}, DELETE /appointments/{appointmentId}
    // These would need similar @Operation, @ApiResponses, and @Parameter annotations.
}
