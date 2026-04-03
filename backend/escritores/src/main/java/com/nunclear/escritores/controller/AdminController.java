package com.nunclear.escritores.controller;

import com.nunclear.escritores.entity.AppUser;
import com.nunclear.escritores.entity.Role;
import com.nunclear.escritores.entity.UserStatus;
import com.nunclear.escritores.repository.AppUserRepository;
import com.nunclear.escritores.service.AuthorizationService;
import com.nunclear.escritores.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminController {

    private final AppUserRepository userRepository;
    private final AuthorizationService authorizationService;
    private final JwtService jwtService;

    /**
     * GET /api/admin/users - Listar todos los usuarios
     */
    @GetMapping("/users")
    public ResponseEntity<?> listUsers(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status) {
        try {
            Integer userId = extractUserIdFromHeader(authHeader);
            Role userRole = extractRoleFromHeader(authHeader);

            if (userRole != Role.ADMINISTRADOR && userRole != Role.MODERADOR) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    Map.of("error", "No tienes permisos para listar usuarios")
                );
            }

            List<AppUser> users = userRepository.findAll();

            if (role != null) {
                users = users.stream()
                        .filter(u -> u.getRole().name().equals(role))
                        .toList();
            }

            if (status != null) {
                users = users.stream()
                        .filter(u -> u.getStatus().name().equals(status))
                        .toList();
            }

            return ResponseEntity.ok(Map.of(
                "total", users.size(),
                "users", users.stream().map(this::convertToDTO).toList()
            ));
        } catch (Exception e) {
            log.error("Error listando usuarios: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Map.of("error", e.getMessage())
            );
        }
    }

    /**
     * PUT /api/admin/users/{id}/role - Cambiar rol de usuario
     */
    @PutMapping("/users/{id}/role")
    public ResponseEntity<?> changeUserRole(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer id,
            @RequestBody ChangeRoleRequest request) {
        try {
            Integer userId = extractUserIdFromHeader(authHeader);
            Role userRole = extractRoleFromHeader(authHeader);

            if (userRole != Role.ADMINISTRADOR) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    Map.of("error", "Solo administradores pueden cambiar roles")
                );
            }

            Role newRole = Role.valueOf(request.getRole().toUpperCase());
            authorizationService.changeUserRole(userId, id, newRole, userRole);

            AppUser user = userRepository.findById(id).orElseThrow();
            return ResponseEntity.ok(Map.of(
                "message", "Rol actualizado",
                "user", convertToDTO(user)
            ));
        } catch (Exception e) {
            log.error("Error cambiando rol: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                Map.of("error", e.getMessage())
            );
        }
    }

    /**
     * PUT /api/admin/users/{id}/status - Cambiar estado de usuario
     */
    @PutMapping("/users/{id}/status")
    public ResponseEntity<?> changeUserStatus(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer id,
            @RequestBody ChangeStatusRequest request) {
        try {
            Integer userId = extractUserIdFromHeader(authHeader);
            Role userRole = extractRoleFromHeader(authHeader);

            UserStatus newStatus = UserStatus.valueOf(request.getStatus().toUpperCase());
            authorizationService.changeUserStatus(userId, id, newStatus, userRole);

            AppUser user = userRepository.findById(id).orElseThrow();
            return ResponseEntity.ok(Map.of(
                "message", "Estado actualizado",
                "user", convertToDTO(user)
            ));
        } catch (Exception e) {
            log.error("Error cambiando estado: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                Map.of("error", e.getMessage())
            );
        }
    }

    /**
     * GET /api/admin/users/{id} - Obtener detalles de usuario
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserDetails(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer id) {
        try {
            Integer userId = extractUserIdFromHeader(authHeader);
            Role userRole = extractRoleFromHeader(authHeader);

            if (userRole != Role.ADMINISTRADOR && userRole != Role.MODERADOR) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    Map.of("error", "No tienes permisos")
                );
            }

            AppUser user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            return ResponseEntity.ok(convertToDTO(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                Map.of("error", e.getMessage())
            );
        }
    }

    /**
     * GET /api/admin/users/me/permissions - Obtener permisos del usuario actual
     */
    @GetMapping("/users/me/permissions")
    public ResponseEntity<?> getMyPermissions(@RequestHeader("Authorization") String authHeader) {
        try {
            Integer userId = extractUserIdFromHeader(authHeader);
            AuthorizationService.UserPermissions permissions = authorizationService.getUserPermissions(userId);
            return ResponseEntity.ok(permissions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Map.of("error", e.getMessage())
            );
        }
    }

    /**
     * GET /api/admin/statistics/overview - Estadísticas generales
     */
    @GetMapping("/statistics/overview")
    public ResponseEntity<?> getStatisticsOverview(@RequestHeader("Authorization") String authHeader) {
        try {
            Role userRole = extractRoleFromHeader(authHeader);

            if (userRole != Role.ADMINISTRADOR) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    Map.of("error", "Solo administradores")
                );
            }

            long totalUsers = userRepository.count();
            long totalActiveUsers = userRepository.findAll().stream()
                    .filter(u -> u.getStatus() == UserStatus.ACTIVE)
                    .count();

            return ResponseEntity.ok(Map.of(
                "totalUsers", totalUsers,
                "activeUsers", totalActiveUsers,
                "suspendedUsers", totalUsers - totalActiveUsers,
                "admins", userRepository.findAll().stream().filter(u -> u.getRole() == Role.ADMINISTRADOR).count(),
                "moderators", userRepository.findAll().stream().filter(u -> u.getRole() == Role.MODERADOR).count()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Map.of("error", e.getMessage())
            );
        }
    }

    // Helper methods
    private Integer extractUserIdFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token no proporcionado");
        }
        String token = authHeader.substring(7);
        return jwtService.validateToken(token);
    }

    private Role extractRoleFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token no proporcionado");
        }
        String token = authHeader.substring(7);
        String roleStr = jwtService.extractRole(token);
        return Role.valueOf(roleStr);
    }

    private UserDTO convertToDTO(AppUser user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .role(user.getRole().name())
                .status(user.getStatus().name())
                .emailVerified(user.isEmailVerified())
                .createdAt(user.getCreatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .build();
    }

    // Request DTOs
    @lombok.Data
    public static class ChangeRoleRequest {
        private String role; // USUARIO, MODERADOR, ADMINISTRADOR
    }

    @lombok.Data
    public static class ChangeStatusRequest {
        private String status; // ACTIVE, SUSPENDED, BANNED
    }

    @lombok.Data
    @lombok.Builder
    public static class UserDTO {
        private Integer id;
        private String email;
        private String username;
        private String role;
        private String status;
        private boolean emailVerified;
        private java.time.LocalDateTime createdAt;
        private java.time.LocalDateTime lastLoginAt;
    }
}
