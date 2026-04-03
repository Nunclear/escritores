package com.nunclear.escritores.controller;

import com.nunclear.escritores.dto.AppUserDTO;
import com.nunclear.escritores.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AppUserController {

    private final AppUserService userService;

    @PostMapping("/register")
    public ResponseEntity<AppUserDTO> registerUser(@RequestBody AppUserRegisterRequest request) {
        AppUserDTO userDTO = AppUserDTO.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .displayName(request.getDisplayName())
                .build();
        AppUserDTO createdUser = userService.createUser(userDTO, request.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppUserDTO> getUserById(@PathVariable Integer id) {
        AppUserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<AppUserDTO>> getAllUsers() {
        List<AppUserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppUserDTO> updateUser(
            @PathVariable Integer id,
            @RequestBody AppUserDTO userDTO) {
        AppUserDTO updatedUser = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping("/{id}/change-password")
    public ResponseEntity<Void> changePassword(
            @PathVariable Integer id,
            @RequestBody ChangePasswordRequest request) {
        userService.changePassword(id, request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable Integer id) {
        userService.deactivateUser(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<AppUserDTO> getUserByUsername(@PathVariable String username) {
        AppUserDTO user = userService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    public static class AppUserRegisterRequest {
        public String username;
        public String email;
        public String displayName;
        public String password;

        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public String getDisplayName() { return displayName; }
        public String getPassword() { return password; }
    }

    public static class ChangePasswordRequest {
        public String oldPassword;
        public String newPassword;

        public String getOldPassword() { return oldPassword; }
        public String getNewPassword() { return newPassword; }
    }
}
