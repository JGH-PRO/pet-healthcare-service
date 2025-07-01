package com.petcare.petcareapp.service.admin;

import com.petcare.petcareapp.domain.User;
import com.petcare.petcareapp.dto.admin.user.AdminUserDto;
import com.petcare.petcareapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminUserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<AdminUserDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::mapToAdminUserDto);
    }

    @Transactional(readOnly = true)
    public AdminUserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
        return mapToAdminUserDto(user);
    }

    @Transactional
    public AdminUserDto updateUserRole(Long userId, String newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));

        // Role validation: Expecting "USER" or "ADMIN"
        String roleToSet = newRole != null ? newRole.trim().toUpperCase() : null;
        if (roleToSet == null || !(roleToSet.equals("USER") || roleToSet.equals("ADMIN"))) {
            throw new IllegalArgumentException("Invalid role specified. Must be USER or ADMIN.");
        }

        // Prevent admin from demoting the last admin. Stored roles are "ADMIN", "USER".
        if (user.getRole() != null && user.getRole().equals("ADMIN") &&
            userRepository.countByRole("ADMIN") <= 1 && roleToSet.equals("USER")) {
            throw new AccessDeniedException("Cannot remove the last admin role.");
        }

        user.setRole(roleToSet); // Store as "USER" or "ADMIN"
        User updatedUser = userRepository.save(user);
        return mapToAdminUserDto(updatedUser);
    }

    @Transactional
    public void deleteUser(Long userId, String currentAdminUsername) {
        User userToDelete = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));

        if (userToDelete.getUsername().equals(currentAdminUsername)) {
            throw new AccessDeniedException("Admin users cannot delete their own accounts.");
        }

        userRepository.delete(userToDelete);
    }

    private AdminUserDto mapToAdminUserDto(User user) {
        return new AdminUserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole() // This will now be "USER" or "ADMIN"
        );
    }
}
