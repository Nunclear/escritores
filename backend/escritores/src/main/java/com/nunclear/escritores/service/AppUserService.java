package com.nunclear.escritores.service;

import com.nunclear.escritores.dto.AppUserDTO;
import com.nunclear.escritores.entity.AppUser;
import com.nunclear.escritores.entity.Role;
import com.nunclear.escritores.entity.UserStatus;
import com.nunclear.escritores.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AppUserService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AppUserDTO createUser(AppUserDTO userDTO, String rawPassword) {
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        AppUser user = AppUser.builder()
                .username(userDTO.getUsername())
                .email(userDTO.getEmail())
                .password(passwordEncoder.encode(rawPassword))
                .displayName(userDTO.getDisplayName())
                .bio(userDTO.getBio())
                .avatarUrl(userDTO.getAvatarUrl())
                .role(userDTO.getRole() != null ? userDTO.getRole() : Role.USUARIO)
                .status(UserStatus.ACTIVE)
                .build();

        AppUser savedUser = userRepository.save(user);
        return mapToDTO(savedUser);
    }

    public AppUserDTO getUserById(Integer id) {
        return userRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public AppUserDTO getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::mapToDTO)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public AppUserDTO getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::mapToDTO)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public List<AppUserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public AppUserDTO updateUser(Integer id, AppUserDTO userDTO) {
        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (userDTO.getDisplayName() != null) {
            user.setDisplayName(userDTO.getDisplayName());
        }
        if (userDTO.getBio() != null) {
            user.setBio(userDTO.getBio());
        }
        if (userDTO.getAvatarUrl() != null) {
            user.setAvatarUrl(userDTO.getAvatarUrl());
        }

        AppUser updatedUser = userRepository.save(user);
        return mapToDTO(updatedUser);
    }

    public void changePassword(Integer id, String oldPassword, String newPassword) {
        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void updateLastLogin(Integer id) {
        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public boolean validatePassword(Integer userId, String rawPassword) {
        return userRepository.findById(userId)
                .map(user -> passwordEncoder.matches(rawPassword, user.getPassword()))
                .orElse(false);
    }

    public void deactivateUser(Integer id) {
        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setStatus(UserStatus.SUSPENDED);
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    private AppUserDTO mapToDTO(AppUser user) {
        return AppUserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .bio(user.getBio())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole())
                .status(user.getStatus())
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
