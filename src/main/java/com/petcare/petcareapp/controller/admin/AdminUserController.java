package com.petcare.petcareapp.controller.admin;

import com.petcare.petcareapp.dto.admin.user.AdminUserDto;
import com.petcare.petcareapp.dto.admin.user.UpdateUserRoleRequestDto;
import com.petcare.petcareapp.service.admin.AdminUserService;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/users")
@Tag(name = "Admin: User Management", description = "APIs for administrators to manage user accounts.")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;

    @GetMapping
    @Operation(summary = "List all users (Admin)", description = "Retrieves a paginated list of all user accounts.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of users", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))), // Page of AdminUserDto
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Page<AdminUserDto>> getAllUsers(
            @Parameter(description="Pagination and sorting parameters") @PageableDefault(size = 20, sort = "username") Pageable pageable) {
        Page<AdminUserDto> users = adminUserService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user by ID (Admin)", description = "Retrieves details for a specific user by their ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user details", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AdminUserDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<AdminUserDto> getUserById(
            @Parameter(name = "userId", description = "ID of the user to retrieve", required = true) @PathVariable Long userId) {
        try {
            AdminUserDto userDto = adminUserService.getUserById(userId);
            return ResponseEntity.ok(userDto);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @PutMapping("/{userId}/role")
    @Operation(summary = "Update user role (Admin)", description = "Updates the role of a specific user. Valid roles: ROLE_USER, ROLE_ADMIN.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User role updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AdminUserDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid role specified or other validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Cannot remove the last admin role"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<AdminUserDto> updateUserRole(
            @Parameter(name = "userId", description = "ID of the user whose role is to be updated", required = true) @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRoleRequestDto roleRequest) {
        try {
            AdminUserDto updatedUser = adminUserService.updateUserRole(userId, roleRequest.getRole());
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (AccessDeniedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage(), e);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete user by ID (Admin)", description = "Deletes a user account. Admins cannot delete their own accounts via this endpoint.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "User deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin cannot delete own account"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(name = "userId", description = "ID of the user to delete", required = true) @PathVariable Long userId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails adminDetails) {
        try {
            adminUserService.deleteUser(userId, adminDetails.getUsername());
            return ResponseEntity.noContent().build();
        } catch (AccessDeniedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage(), e);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }
}
