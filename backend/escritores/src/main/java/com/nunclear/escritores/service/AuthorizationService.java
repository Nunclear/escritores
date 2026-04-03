package com.nunclear.escritores.service;

import com.nunclear.escritores.entity.AppUser;
import com.nunclear.escritores.entity.Role;
import com.nunclear.escritores.entity.UserStatus;
import com.nunclear.escritores.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthorizationService {

    private final AppUserRepository userRepository;

    /**
     * Verificar si usuario tiene permisos sobre un recurso
     */
    public boolean canAccessResource(Integer userId, Integer resourceOwnerId, Role userRole) {
        if (userRole == Role.ADMINISTRADOR) {
            return true; // Admin puede acceder a todo
        }

        if (userRole == Role.MODERADOR) {
            return true; // Moderador puede acceder para moderar
        }

        // Usuario normal solo puede acceder a sus propios recursos
        return userId.equals(resourceOwnerId);
    }

    /**
     * Cambiar rol de usuario (solo ADMINISTRADOR)
     */
    public void changeUserRole(Integer userId, Integer targetUserId, Role newRole, Role requesterRole) {
        if (requesterRole != Role.ADMINISTRADOR) {
            throw new RuntimeException("Solo administradores pueden cambiar roles");
        }

        if (userId.equals(targetUserId)) {
            throw new RuntimeException("No puedes cambiar tu propio rol");
        }

        AppUser user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setRole(newRole);
        userRepository.save(user);
    }

    /**
     * Activar o suspender cuenta
     */
    public void changeUserStatus(Integer userId, Integer targetUserId, UserStatus newStatus, Role requesterRole) {
        if (requesterRole != Role.ADMINISTRADOR && requesterRole != Role.MODERADOR) {
            throw new RuntimeException("No tienes permisos para cambiar estado de usuario");
        }

        if (requesterRole == Role.MODERADOR && newStatus == UserStatus.BANNED) {
            throw new RuntimeException("Solo administradores pueden banear permanentemente");
        }

        if (userId.equals(targetUserId)) {
            throw new RuntimeException("No puedes cambiar tu propio estado");
        }

        AppUser user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setStatus(newStatus);
        userRepository.save(user);
    }

    /**
     * Obtener usuarios por rol
     */
    public List<AppUser> getUsersByRole(Role role, Role requesterRole) {
        if (requesterRole != Role.ADMINISTRADOR && requesterRole != Role.MODERADOR) {
            throw new RuntimeException("No tienes permisos para listar usuarios");
        }

        // Implementar búsqueda por rol cuando JPA lo soporte
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == role)
                .toList();
    }

    /**
     * Obtener usuarios por estado
     */
    public List<AppUser> getUsersByStatus(UserStatus status, Role requesterRole) {
        if (requesterRole != Role.ADMINISTRADOR && requesterRole != Role.MODERADOR) {
            throw new RuntimeException("No tienes permisos para listar usuarios");
        }

        return userRepository.findAll().stream()
                .filter(u -> u.getStatus() == status)
                .toList();
    }

    /**
     * Obtener permisos efectivos del usuario
     */
    public UserPermissions getUserPermissions(Integer userId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return new UserPermissions(
            userId,
            user.getRole(),
            user.getStatus(),
            buildPermissionMap(user.getRole())
        );
    }

    /**
     * Construir mapa de permisos según el rol
     */
    private Map<String, Boolean> buildPermissionMap(Role role) {
        Map<String, Boolean> permissions = new HashMap<>();

        // Todos pueden leer contenido público
        permissions.put("read_public", true);
        permissions.put("register", role != Role.ADMINISTRADOR && role != Role.MODERADOR);
        permissions.put("login", true);

        // Usuario normal
        if (role == Role.USUARIO) {
            permissions.put("create_story", true);
            permissions.put("edit_own_story", true);
            permissions.put("comment", true);
            permissions.put("rate", true);
            permissions.put("follow_author", true);
            permissions.put("favorite_story", true);
            permissions.put("report_content", true);
            permissions.put("view_own_dashboard", true);
        }

        // Moderador - hereda permisos de usuario
        if (role == Role.MODERADOR || role == Role.ADMINISTRADOR) {
            permissions.put("create_story", true);
            permissions.put("edit_own_story", true);
            permissions.put("comment", true);
            permissions.put("rate", true);
            permissions.put("follow_author", true);
            permissions.put("favorite_story", true);
            permissions.put("report_content", true);
            permissions.put("view_own_dashboard", true);
            permissions.put("moderate_comments", true);
            permissions.put("review_reports", true);
            permissions.put("create_warning", true);
            permissions.put("apply_temp_ban", true);
            permissions.put("view_moderation_panel", true);
        }

        // Administrador - acceso total
        if (role == Role.ADMINISTRADOR) {
            permissions.put("view_admin_panel", true);
            permissions.put("change_user_role", true);
            permissions.put("change_user_status", true);
            permissions.put("apply_permanent_ban", true);
            permissions.put("remove_sanction", true);
            permissions.put("create_notice", true);
            permissions.put("view_global_statistics", true);
            permissions.put("access_all_content", true);
        }

        return permissions;
    }

    /**
     * DTO para respuesta de permisos
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class UserPermissions {
        private Integer userId;
        private Role role;
        private UserStatus status;
        private Map<String, Boolean> permissions;

        public boolean hasPermission(String permission) {
            return permissions.getOrDefault(permission, false);
        }
    }
}
