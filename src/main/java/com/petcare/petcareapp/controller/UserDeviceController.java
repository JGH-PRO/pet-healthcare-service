package com.petcare.petcareapp.controller;

import com.petcare.petcareapp.dto.user.RegisterDeviceRequestDto;
import com.petcare.petcareapp.dto.user.UserDeviceDto;
import com.petcare.petcareapp.service.UserDeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@RequestMapping("/api/users/me/devices")
@Tag(name = "User Device Management", description = "APIs for managing user device tokens for FCM notifications.")
@SecurityRequirement(name = "bearerAuth")
public class UserDeviceController {

    @Autowired
    private UserDeviceService userDeviceService;

    @PostMapping
    @Operation(summary = "Register a device for notifications",
               description = "Registers or updates a device token for the authenticated user. If the token already exists for the user, its last login time is updated.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Device registered/updated successfully",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDeviceDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input (e.g., empty token or platform)"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated")
    })
    public ResponseEntity<UserDeviceDto> registerDevice(
            @Valid @RequestBody RegisterDeviceRequestDto requestDto,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        try {
            UserDeviceDto deviceDto = userDeviceService.registerDevice(requestDto, userDetails.getUsername());
            return ResponseEntity.ok(deviceDto);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @GetMapping
    @Operation(summary = "List registered devices for current user",
               description = "Retrieves a list of all registered devices for the authenticated user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved device list",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDeviceDto.class))) // Actually Page<UserDeviceDto> or List<UserDeviceDto>
    })
    public ResponseEntity<List<UserDeviceDto>> getRegisteredDevices(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        List<UserDeviceDto> devices = userDeviceService.getDevicesForUser(userDetails.getUsername());
        return ResponseEntity.ok(devices);
    }

    @DeleteMapping
    @Operation(summary = "Remove/unregister a device",
               description = "Removes a specific device token registration for the authenticated user. The token to be removed is passed as a request parameter.")
    @Parameter(name = "deviceToken", description = "The FCM device token to unregister", required = true, example = "bk3RNwTe3H0:CI2k_HHwgIpoDKCIZvvDMExUdFQ3P1...")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Device unregistered successfully"),
        @ApiResponse(responseCode = "400", description = "Device token parameter missing or invalid"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Device token not found for this user")
    })
    public ResponseEntity<Void> removeDevice(
            @RequestParam String deviceToken,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        try {
            userDeviceService.removeDevice(deviceToken, userDetails.getUsername());
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (RuntimeException e) { // Catches "not found"
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }
}
