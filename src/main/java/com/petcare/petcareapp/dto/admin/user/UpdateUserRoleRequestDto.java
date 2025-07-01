package com.petcare.petcareapp.dto.admin.user;

// Add validation like @NotBlank for role later
public class UpdateUserRoleRequestDto {
    private String role;

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
