package com.petcare.petcareapp.controller;

import com.petcare.petcareapp.domain.model.HealthLogType;
import com.petcare.petcareapp.dto.healthlog.CreateHealthLogRequestDto;
import com.petcare.petcareapp.dto.healthlog.HealthLogDto;
import com.petcare.petcareapp.dto.healthlog.UpdateHealthLogRequestDto;
import com.petcare.petcareapp.service.HealthLogService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/pets/{petId}/health-logs")
@Tag(name = "Health Log Management", description = "APIs for managing pet health logs")
public class HealthLogController {

    @Autowired
    private HealthLogService healthLogService;

    @Operation(summary = "Add a new health log", description = "Creates a new health log entry for the specified pet.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Health log created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input, pet not found, or user does not own pet"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    public ResponseEntity<HealthLogDto> addHealthLog(
            @Parameter(description = "ID of the pet") @PathVariable Long petId,
            @Valid @RequestBody CreateHealthLogRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            HealthLogDto createdLog = healthLogService.addHealthLog(petId, requestDto, userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdLog);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @Operation(summary = "List health logs for a pet", description = "Retrieves health logs for a pet, with optional filtering by log type and date range.")
    @Parameters({
        @Parameter(name = "logType", description = "Filter by log type (MEAL, TOILET, WALK, WEIGHT)", required = false, schema = @Schema(implementation = HealthLogType.class)),
        @Parameter(name = "startDate", description = "Filter logs from this date (ISO DATE_TIME format, e.g., 2023-01-01T00:00:00)", required = false, schema = @Schema(type = "string", format = "date-time")),
        @Parameter(name = "endDate", description = "Filter logs up to this date (ISO DATE_TIME format, e.g., 2023-01-31T23:59:59)", required = false, schema = @Schema(type = "string", format = "date-time"))
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of health logs"),
            @ApiResponse(responseCode = "404", description = "Pet not found or user does not own pet"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public ResponseEntity<List<HealthLogDto>> getHealthLogsByPetId(
            @Parameter(description = "ID of the pet") @PathVariable Long petId,
            @RequestParam(required = false) HealthLogType logType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            List<HealthLogDto> logs = healthLogService.getHealthLogsByPetId(petId, userDetails.getUsername(), logType, startDate, endDate);
            return ResponseEntity.ok(logs);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @Operation(summary = "Get a specific health log", description = "Retrieves a single health log by its ID and pet ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved health log"),
            @ApiResponse(responseCode = "404", description = "Health log or pet not found, or user does not own pet"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/{logId}")
    public ResponseEntity<HealthLogDto> getHealthLogById(
            @Parameter(description = "ID of the pet") @PathVariable Long petId,
            @Parameter(description = "ID of the health log") @PathVariable Long logId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            HealthLogDto logDto = healthLogService.getHealthLogById(petId, logId, userDetails.getUsername());
            return ResponseEntity.ok(logDto);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @Operation(summary = "Update a health log", description = "Updates an existing health log entry.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Health log updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Health log or pet not found, or user does not own pet"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("/{logId}")
    public ResponseEntity<HealthLogDto> updateHealthLog(
            @Parameter(description = "ID of the pet") @PathVariable Long petId,
            @Parameter(description = "ID of the health log to update") @PathVariable Long logId,
            @Valid @RequestBody UpdateHealthLogRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            HealthLogDto updatedLog = healthLogService.updateHealthLog(petId, logId, requestDto, userDetails.getUsername());
            return ResponseEntity.ok(updatedLog);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @Operation(summary = "Delete a health log", description = "Deletes a health log entry by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Health log deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Health log or pet not found, or user does not own pet"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @DeleteMapping("/{logId}")
    public ResponseEntity<Void> deleteHealthLog(
            @Parameter(description = "ID of the pet") @PathVariable Long petId,
            @Parameter(description = "ID of the health log to delete") @PathVariable Long logId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            healthLogService.deleteHealthLog(petId, logId, userDetails.getUsername());
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }
}
