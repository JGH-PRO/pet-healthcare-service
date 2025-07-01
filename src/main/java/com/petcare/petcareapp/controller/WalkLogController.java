package com.petcare.petcareapp.controller;

import com.petcare.petcareapp.dto.walklog.EndWalkRequestDto;
import com.petcare.petcareapp.dto.walklog.OngoingWalkDto;
import com.petcare.petcareapp.dto.walklog.StartWalkResponseDto;
import com.petcare.petcareapp.dto.walklog.WalkLogDto;
import com.petcare.petcareapp.service.WalkLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api") // Base path, specific paths in methods
@Tag(name = "Walk Log Management", description = "APIs for starting, ending, and viewing pet walk logs.")
@SecurityRequirement(name = "bearerAuth") // All endpoints under this controller require authentication
public class WalkLogController {

    @Autowired
    private WalkLogService walkLogService;

    @PostMapping("/pets/{petId}/walks/start")
    @Operation(summary = "Start a new walk session for a pet",
               description = "Creates a new walk log with the current system time as the start time. " +
                             "Ensures the pet belongs to the authenticated user and does not already have an ongoing walk.")
    @Parameter(name = "petId", description = "ID of the pet for which to start the walk", required = true, in = ParameterIn.PATH)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Walk started successfully",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = StartWalkResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input (e.g., pet already has an ongoing walk)"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User does not own this pet"),
        @ApiResponse(responseCode = "404", description = "Pet not found")
    })
    public ResponseEntity<StartWalkResponseDto> startWalk(
            @PathVariable Long petId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        try {
            StartWalkResponseDto response = walkLogService.startWalk(petId, userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalStateException | IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (AccessDeniedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage(), e);
        } catch (RuntimeException e) { // Catches PetNotFound
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @PutMapping("/walks/{walkId}/end")
    @Operation(summary = "End an ongoing walk session",
               description = "Updates an existing walk log with end time, duration, distance, route path, and notes. " +
                             "Ensures the walk log belongs to a pet owned by the authenticated user.")
    @Parameter(name = "walkId", description = "ID of the walk session to end", required = true, in = ParameterIn.PATH)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Walk ended successfully",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = WalkLogDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input or walk already ended"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized to end this walk"),
        @ApiResponse(responseCode = "404", description = "WalkLog not found")
    })
    public ResponseEntity<WalkLogDto> endWalk(
            @PathVariable Long walkId,
            @Valid @RequestBody EndWalkRequestDto requestDto,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        try {
            WalkLogDto endedWalk = walkLogService.endWalk(walkId, requestDto, userDetails.getUsername());
            return ResponseEntity.ok(endedWalk);
        } catch (IllegalStateException | IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (AccessDeniedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage(), e);
        } catch (RuntimeException e) { // Catches WalkLogNotFound
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @GetMapping("/walks/{walkId}")
    @Operation(summary = "Get details of a specific walk log",
               description = "Retrieves a specific walk log by its ID. Ensures the walk log belongs to a pet owned by the authenticated user.")
    @Parameter(name = "walkId", description = "ID of the walk log to retrieve", required = true, in = ParameterIn.PATH)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved walk log",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = WalkLogDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized to view this walk log"),
        @ApiResponse(responseCode = "404", description = "WalkLog not found")
    })
    public ResponseEntity<WalkLogDto> getWalkLogDetails(
            @PathVariable Long walkId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        try {
            WalkLogDto walkLogDto = walkLogService.getWalkLogDetails(walkId, userDetails.getUsername());
            return ResponseEntity.ok(walkLogDto);
        } catch (AccessDeniedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage(), e);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @GetMapping("/pets/{petId}/walks")
    @Operation(summary = "List walk logs for a pet",
               description = "Retrieves a paginated list of walk logs for a specific pet owned by the authenticated user. Sorted by start time descending.")
    @Parameter(name = "petId", description = "ID of the pet whose walk logs are to be retrieved", required = true, in = ParameterIn.PATH)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved walk logs",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))), // Page of WalkLogDto
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User does not own this pet"),
        @ApiResponse(responseCode = "404", description = "Pet not found")
    })
    public ResponseEntity<Page<WalkLogDto>> listWalkLogsForPet(
            @PathVariable Long petId,
            @PageableDefault(size = 10, sort = "startTime,desc") Pageable pageable,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Page<WalkLogDto> walkLogs = walkLogService.listWalkLogsForPet(petId, userDetails.getUsername(), pageable);
            return ResponseEntity.ok(walkLogs);
        } catch (AccessDeniedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage(), e);
        } catch (RuntimeException e) { // Catches PetNotFound
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @GetMapping("/pets/{petId}/walks/ongoing")
    @Operation(summary = "Get ongoing walk for a pet",
               description = "Checks if there is an active (started but not ended) walk for the specified pet owned by the authenticated user.")
    @Parameter(name = "petId", description = "ID of the pet to check for an ongoing walk", required = true, in = ParameterIn.PATH)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ongoing walk details if one exists.",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = OngoingWalkDto.class))),
        @ApiResponse(responseCode = "204", description = "No ongoing walk for this pet (No content)."),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User does not own this pet"),
        @ApiResponse(responseCode = "404", description = "Pet not found")
    })
    public ResponseEntity<OngoingWalkDto> getOngoingWalkForPet(
            @PathVariable Long petId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Optional<OngoingWalkDto> ongoingWalkOpt = walkLogService.getOngoingWalkForPet(petId, userDetails.getUsername());
            return ongoingWalkOpt
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.noContent().build());
        } catch (AccessDeniedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage(), e);
        } catch (RuntimeException e) { // Catches PetNotFound
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @DeleteMapping("/walks/{walkId}")
    @Operation(summary = "Delete a walk log",
               description = "Deletes a specific walk log by its ID. Ensures the walk log belongs to a pet owned by the authenticated user.")
    @Parameter(name = "walkId", description = "ID of the walk log to delete", required = true, in = ParameterIn.PATH)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Walk log deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized to delete this walk log"),
        @ApiResponse(responseCode = "404", description = "WalkLog not found")
    })
    public ResponseEntity<Void> deleteWalkLog(
            @PathVariable Long walkId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        try {
            walkLogService.deleteWalkLog(walkId, userDetails.getUsername());
            return ResponseEntity.noContent().build();
        } catch (AccessDeniedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage(), e);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }
}
