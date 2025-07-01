package com.petcare.petcareapp.service;

import com.petcare.petcareapp.domain.User;
import com.petcare.petcareapp.dto.auth.LoginRequestDto;
import com.petcare.petcareapp.dto.auth.RegisterRequestDto;
import com.petcare.petcareapp.repository.UserRepository;
import com.petcare.petcareapp.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Transactional
    public User registerUser(RegisterRequestDto registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        User user = new User(
                registerRequest.getUsername(),
                passwordEncoder.encode(registerRequest.getPassword()),
                registerRequest.getEmail(),
                "USER" // Default role
        );
        return userRepository.save(user);
    }

    public String loginUser(LoginRequestDto loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Generate token using the username from the authenticated principal
        org.springframework.security.core.userdetails.User userDetails =
            (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        return tokenProvider.generateTokenFromUsername(userDetails.getUsername());
    }
}
