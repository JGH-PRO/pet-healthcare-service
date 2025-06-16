package com.petcare.petcareapp.controller;
import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.tags.Tag;

import com.petcare.petcareapp.domain.User;
import com.petcare.petcareapp.dto.auth.AuthResponseDto;
import com.petcare.petcareapp.dto.auth.LoginRequestDto;
import com.petcare.petcareapp.dto.auth.RegisterRequestDto;
import com.petcare.petcareapp.repository.UserRepository;
import com.petcare.petcareapp.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@Tag(name = "Authentication", description = "Endpoints for user registration and login")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Operation(summary = "Register a new user", description = "Creates a new user account.")
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequestDto registerRequest) {
        try {
            User registeredUser = authService.registerUser(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully! Username: " + registeredUser.getUsername());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "Login an existing user", description = "Authenticates a user and returns a JWT token.")
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestDto loginRequest) {
        try {
            String jwt = authService.loginUser(loginRequest);
            User user = userRepository.findByUsername(loginRequest.getUsername())
                            .orElseThrow(() -> new RuntimeException("User not found after successful login. This should not happen."));
            return ResponseEntity.ok(new AuthResponseDto(jwt, user.getId(), user.getUsername()));
        } catch (org.springframework.security.core.AuthenticationException e) { // More specific exception
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed: Invalid credentials");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Login failed: " + e.getMessage());
        }
    }
}
