package com.petcare.petcareapp.dto.admin.user;

// import java.util.List; // Removed as not used

public class AdminUserDto {
    private Long id;
    private String username;
    private String email;
    private String role;
    // Add other fields like enabled, accountLocked etc. if they exist in User entity and are relevant for admin

    public AdminUserDto() {}

    public AdminUserDto(Long id, String username, String email, String role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
